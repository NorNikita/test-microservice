package ru.pflb.boomq.test.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pflb.boomq.model.projectservice.dto.testproject.TestType;
import ru.pflb.boomq.model.test.TestState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Test {

    @Id
    @Column(name = "test_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name="user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "bucket_uri")
    private String bucketUri;

    @Column(name = "settings")
    private String settings;

    @Column(name = "test_profile")
    private String testProfile;

    @Enumerated(EnumType.STRING)
    private TestState state;

    @Enumerated(EnumType.STRING)
    private TestType type;

    @Column(name = "count_users")
    private Long countUsers;

    @Column(name = "comment")
    private String comment;

    @Column(name = "from_date")
    private LocalDateTime fromDate;

    @Column(name = "to_date")
    private LocalDateTime toDate;

    @Column(name = "total_duration")
    private Long totalDuration;

    @Column(name = "version")
    private String version;

}
