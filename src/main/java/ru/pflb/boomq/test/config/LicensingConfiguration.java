package ru.pflb.boomq.test.config;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.pflb.boomq.licensing.service.LicenseService;
import ru.pflb.boomq.licensing.service.LicenseValidator;
import ru.pflb.boomq.licensing.service.ServerTimeSource;
import ru.pflb.boomq.licensing.service.ServerTimeVerificationService;

import java.util.List;

/**
 * Конфиг лицензирования, внимание - не нужно его отключать через spring profile или properties в интеграционных тестах или локально
 * Нужно чтобы отключить его в собранном приложении было невозможно без перекомпиляции кода
 * См. readme модуля licensing
 */
@EnableScheduling
public class LicensingConfiguration extends ru.pflb.boomq.licensing.config.LicensingConfigurationTemplate {

    @Bean
    @Override
    public LicenseValidator licenseValidator(LicenseService licenseService,
                                             ServerTimeVerificationService serverTimeVerificationService,
                                             ConfigurableApplicationContext applicationContext) {
        return super.licenseValidator(licenseService, serverTimeVerificationService, applicationContext);
    }

    @Bean
    public LicenseService licenseService(ConsulClient consulClient, ConsulConfigProperties consulConfigProperties) {
        return super.licenseService(consulClient, consulConfigProperties.getAclToken());
    }

    @Bean
    @Override
    public ServerTimeVerificationService serverTimeVerificationService(List<ServerTimeSource> serverTimeSources) {
        return super.serverTimeVerificationService(serverTimeSources);
    }
}
