package ru.pflb.boomq.test.model.entity;

import lombok.*;
import ru.pflb.boomq.model.testrunner.State;

import javax.persistence.*;

@Entity
@Table(name = "used_resource")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsedResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "container_id")
    private String containerId;

    @Column(name = "server_id")
    private String serverId;

    @Column(name = "count_users")
    private Long countUsers;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "number_test")
    private Test test;
}
