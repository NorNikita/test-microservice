package ru.pflb.boomq.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import ru.pflb.boomq.model.testrunner.State;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.UsedResource;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface UsedResourceRepository extends JpaRepository<UsedResource, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<UsedResource> findAllByServerIdInAndState(List<String> serverIds, State state);

    List<UsedResource> findAllByTest(Test test);
}
