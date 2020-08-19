package ru.pflb.boomq.test.utils;

import org.springframework.stereotype.Component;
import ru.pflb.boomq.model.projectservice.dto.testproject.Request;
import ru.pflb.boomq.model.test.ExceptionMessage;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.model.test.testprofile.TestProfileDto;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.test.repository.TestRepository;

import java.util.Optional;

@Component
public class Validator {
    private TestRepository testRepository;

    public Validator(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public void validateTestProfileDTO(TestProfileDto dto) {
        if(dto.getGroups() == null || dto.getGroups().isEmpty()) {
            throw new TestServiceException(ExceptionMessage.ERROR_VALIDATE_TEST_PROFILE);
        } else {
            Optional<Request> first = dto.getGroups().stream()
                    .flatMap(group -> group.getRequests().stream())
                    .filter(request -> null == request.getUrl() || null == request.getMethod())
                    .findFirst();
            if(first.isPresent()) throw new TestServiceException(ExceptionMessage.ERROR_VALIDATE_TEST_PROFILE);
        }
    }

    public void checkRunnigTest(Long projectId)  {
        if(testRepository.findAllByProjectIdAndState(projectId, TestState.RUNNING).size() > 0) {
            throw new TestServiceException(ExceptionMessage.MANY_TEST_RUN);
        }
    }

    public void chaeckSubscribtions() {
        // TODO обращение к другому сервису. проверка подписки
    }
}
