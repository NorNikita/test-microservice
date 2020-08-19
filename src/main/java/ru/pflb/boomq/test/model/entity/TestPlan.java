package ru.pflb.boomq.test.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pflb.boomq.model.testplan.enums.FileExtension;
import ru.pflb.boomq.model.testplan.enums.RestrictionsLevel;
import ru.pflb.boomq.model.testplan.enums.TestingTool;

import javax.persistence.*;

@Entity
@Table(name = "test_plan")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TestPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_id")
    private Long testId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "jmx_uri")
    private String jmxUri;

    @Column(name = "file_name")
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "restriction_level")
    private RestrictionsLevel restrictionsLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "testing_tool")
    private TestingTool testingTool;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_extension")
    private FileExtension fileExtension;
}
