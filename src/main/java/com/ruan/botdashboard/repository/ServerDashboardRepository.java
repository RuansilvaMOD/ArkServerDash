package com.ruan.botdashboard.repository;

import com.ruan.botdashboard.entities.ServerDashboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServerDashboardRepository extends JpaRepository<ServerDashboard, Integer> {
    Optional<ServerDashboard>findByGuildId(String guildId);
}
