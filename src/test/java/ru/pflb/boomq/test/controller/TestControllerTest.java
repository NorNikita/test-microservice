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
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.test.Action;
import ru.pflb.boomq.model.test.TestEventDto;
import ru.pflb.boomq.model.test.settings.TestSettingDto;
import ru.pflb.boomq.test.adapter.*;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.utils.Validator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:tc:postgresql:10-alpine:///test_db?stringtype=unspecified",
                "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"})
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRepository testRepository;

    @MockBean
    private Validator validator;

    @MockBean
    private IFileAdapterService fileAdapterService;

    @MockBean
    private ITestPlanAdapterService testPlanAdapterService;

    @MockBean
    private IResourcesAdapterService resourcesAdapterService;

    @MockBean
    private IContainerAdapterService containerAdapterService;

    @MockBean
    private ITestRunnerAdapterService testRunnerAdapterService;

    @MockBean
    private LicenseValidator licenseValidator;

    @MockBean
    private LicenseService licenseService;

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.controller.TestControllerStreamArgs#getParamsForRunTest")
    void startTest(Jwt jwt,
                   SimpleGrantedAuthority authority,
                   TestEventDto testEventDto,
                   String content,
                   TestPlan testPlan,
                   List<AllocatedResourceDto> allocatedResources,
                   List<ContainerDto> containerDtos) throws Exception {

        when(fileAdapterService.getFileContentAsString(anyString())).thenReturn(content);
        doNothing().when(validator).checkRunnigTest(anyLong());
        when(testPlanAdapterService.createTestPlan(any(Test.class))).thenReturn(testPlan);
        when(resourcesAdapterService.allocateResources(any(Test.class))).thenReturn(allocatedResources);
        when(containerAdapterService.buildContainers(anyListOf(AllocatedResourceDto.class), any(Test.class), any(TestPlan.class), any(TestSettingDto.class))).thenReturn(containerDtos);
        doNothing().when(testRunnerAdapterService)
                .createContainersAndRun(anyListOf(ContainerDto.class), anyListOf(AllocatedResourceDto.class), any(Test.class));


        MvcResult result = mockMvc.perform(post("/").with(jwt(jwt).authorities(authority))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testEventDto)))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.ACCEPTED.value());
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.controller.TestControllerStreamArgs#getParams")
    void updateTest(Jwt jwt, SimpleGrantedAuthority authority, Test test) throws Exception {
        Test savedTest = testRepository.save(test);
        jwt = correctUserIdJwt(jwt, test);

        doNothing().when(testRunnerAdapterService).stopTestContainer(savedTest);

        MvcResult result = mockMvc.perform(put("/" + savedTest.getTestId())
                .param("action", Action.STOP.name())
                .with(jwt(jwt).authorities(authority)))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.ACCEPTED.value());
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.controller.TestControllerStreamArgs#getParams")
    void getAllUserTest(Jwt jwt, SimpleGrantedAuthority authority, Test test) throws Exception {
        testRepository.save(test);

        MvcResult result = mockMvc.perform(get("")
                .with(jwt(jwt).authorities(authority)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.controller.TestControllerStreamArgs#getParams")
    void viewTest(Jwt jwt, SimpleGrantedAuthority authority, Test test) throws Exception {
        Test savedTest = testRepository.save(test);
        jwt = correctUserIdJwt(jwt, savedTest);

        MvcResult result = mockMvc.perform(get("/" + savedTest.getTestId())
                .with(jwt(jwt).authorities(authority)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
    }

    private Jwt correctUserIdJwt(Jwt jwt, Test test) {
        return Jwt.withTokenValue(jwt.getTokenValue())
                .claim("user_id", test.getUserId())
                .header("tip", "JWT")
                .build();
    }
}