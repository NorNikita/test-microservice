package ru.pflb.boomq.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.model.entity.Test;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long>, JpaSpecificationExecutor<Test> {

    List<Test> findAllByProjectIdAndState(Long projectId, TestState state);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Test> findTestByState(TestState testState);

    Optional<Test> findFirstByOrderByFromDateDesc();

}
