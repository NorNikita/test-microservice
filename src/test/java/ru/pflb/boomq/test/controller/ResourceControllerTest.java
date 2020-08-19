package ru.pflb.boomq.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.pflb.boomq.licensing.service.LicenseService;
import ru.pflb.boomq.licensing.service.LicenseValidator;
import ru.pflb.boomq.model.testrunner.ResourceDto;
import ru.pflb.boomq.model.testrunner.Server;
import ru.pflb.boomq.test.adapter.testrunner.ResourcesAdapterService;
import ru.pflb.boomq.test.config.properties.InfluxProperties;
import ru.pflb.boomq.test.model.entity.Resource;
import ru.pflb.boomq.test.repository.ResourceRepository;
import ru.pflb.boomq.test.utils.Builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:tc:postgresql:10-alpine:///test_db?stringtype=unspecified",
                "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"})
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private Builder builder;

    @MockBean
    private ResourcesAdapterService resourcesAdapterService;

    @MockBean
    private InfluxProperties influxProperties;

    @MockBean
    private LicenseValidator licenseValidator;

    @MockBean
    private LicenseService licenseService;

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.controller.ResourceControllerStreamArgs#getArgsFor_createResource")
    void createResource(Jwt jwt,
                        SimpleGrantedAuthority authority,
                        ResourceDto resourceDto,
                        Server server) throws Exception {

        when(resourcesAdapterService.createServer(any(Resource.class))).thenReturn(server);

        MvcResult result = mockMvc.perform(post("/resource").with(jwt(jwt).authorities(authority))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(resourceDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.CREATED.value());
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.controller.ResourceControllerStreamArgs#getArgsFor_createResource")
    void getResource(Jwt jwt,
                        SimpleGrantedAuthority authority,
                        ResourceDto resourceDto,
                        Server server) throws Exception {

        Resource resourceSaved = resourceRepository.save(builder.buildResource(resourceDto));

        MvcResult result = mockMvc.perform(get("/resource/" + resourceSaved.getId()).with(jwt(jwt).authorities(authority))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(resourceDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
    }
}