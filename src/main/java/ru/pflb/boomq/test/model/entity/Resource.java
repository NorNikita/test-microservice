package ru.pflb.boomq.test.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "resource")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "server_id")
    private String serverId;

    @Column(name = "location")
    private String location;

    @Column(name = "host")
    private String host;

    @Column(name = "port")
    private Integer port;

    @Column(name = "priority")
    private Long priority;

    @Column(name = "max_user_count")
    private Long maxUserCount;

    @Column(name = "for_free")
    private Boolean forFree;
}
