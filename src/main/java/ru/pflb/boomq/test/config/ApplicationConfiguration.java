package ru.pflb.boomq.test.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import ru.pflb.boomq.test.config.properties.InfluxProperties;
import ru.pflb.boomq.test.config.properties.Minio;
import ru.pflb.boomq.test.config.properties.Security;
import ru.pflb.boomq.test.config.properties.TestServiceProperties;

import java.util.Collections;

// конфигурация лицензирования подключается через импорт для того чтобы на неё была ссылка из реального используемого класса
// без такой ссылки для обхода лицензии можно было бы просто удалить класс LicensingConfiguration из jar, без необходимости перекомпилировать код
@Import(LicensingConfiguration.class)
@Configuration
@EnableConfigurationProperties(TestServiceProperties.class)
public class ApplicationConfiguration {

    private TestServiceProperties properties;

    public ApplicationConfiguration(TestServiceProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ClientCredentialsResourceDetails getClientCredentialResourceDetails() {
        ClientCredentialsResourceDetails clientCredentialsResourceDetails = new ClientCredentialsResourceDetails();
        Security security = properties.getSecurity();

        clientCredentialsResourceDetails.setClientId(security.getClientId());
        clientCredentialsResourceDetails.setClientSecret(security.getClientSecret());
        clientCredentialsResourceDetails.setAccessTokenUri(security.getAccessTokenUri());
        clientCredentialsResourceDetails.setGrantType(security.getGrantType());
        clientCredentialsResourceDetails.setScope(Collections.singletonList(security.getScope()));

        return clientCredentialsResourceDetails;
    }

    @Bean
    public MinioClient getMinioClient(Minio minio) {
        try {
            return new MinioClient(minio.getEndpoint(),
                    minio.getAccesskey(),
                    minio.getSecretkey());
        } catch (MinioException e) {
            throw new BeanInitializationException(
                    String.format("Failed to initialize %s!", MinioClient.class),
                    e);
        }
    }

    @Bean
    public InfluxProperties getInfluxProperties() {
        return properties.getInfluxProperties();
    }

    @Bean
    public Minio getMinio() {
        return properties.getMinio();
    }

    @Bean
    public ObjectMapper yamlObjectMapper() {
        YAMLFactory factory = new YAMLFactory();
        factory.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);

        return new ObjectMapper(factory).configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDateFormat(new StdDateFormat());
    }

}
