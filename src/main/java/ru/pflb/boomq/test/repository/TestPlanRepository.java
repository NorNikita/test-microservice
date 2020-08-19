package ru.pflb.boomq.test.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pflb.boomq.test.model.entity.TestPlan;

@Repository
public interface TestPlanRepository extends JpaRepository<TestPlan, Long> {

    TestPlan findByTestId(Long aLong);
}
