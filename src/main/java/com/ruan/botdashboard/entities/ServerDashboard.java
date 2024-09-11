package com.ruan.botdashboard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name ="tb_serverDashboard")
public class ServerDashboard implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serverDashboard_id")
    private Integer id;

    @Column(name = "serverDashboard_guildId", length = 255, nullable = false)
    private String guildId;

    @Column(name = "serverDashboard_serverName", length = 255, nullable = false)
    private String serverName;

    @Column(name = "serverDashboard_token", length = 255, nullable = false)
    private String token;

    @Column(name = "serverDashboard_dashChannel", length = 255, nullable = false)
    private String dashChannel;

    @Column(name = "serverDashboard_alertsChannel", length = 255, nullable = false)
    private String alertsChannel;

    @Column(name = "serverDashboard_cryoChannel", length = 255, nullable = false)
    private String cryoChannel;

    @Column(name = "serverDashboard_playersChannel", length = 255, nullable = false)
    private String playersChannel;

    @Column(name = "serverDashboard_parasaurChannel", length = 255, nullable = false)
    private String parasaurChannel;

    @Column(name = "serverDashboard_serverstatusChannel", length = 255, nullable = false)
    private String serverstatusChannel;

}
