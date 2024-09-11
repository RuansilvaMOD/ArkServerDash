package com.ruan.botdashboard.service;

import com.ruan.botdashboard.dto.ServerDashboardRequestDTO;
import com.ruan.botdashboard.entities.ServerDashboard;
import com.ruan.botdashboard.repository.ServerDashboardRepository;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ServerDashboardService {

    private final ServerDashboardRepository repository;
    private final JDA jda;

    public ResponseEntity<String> registerServerDashboard(ServerDashboardRequestDTO body) {

        Guild guild = jda.getGuildById(body.guildId());
        if (guild != null) {
            Category category = guild.getCategoriesByName("Dashboard " + body.serverName(), true)
                    .stream().findFirst().orElse(null);

            if (category == null) {
                // Cria a categoria e os canais de texto
                CompletableFuture<List<String>> channelsFuture = guild.createCategory("Dashboard " + body.serverName())
                        .submit()
                        .thenCompose(createdCategory -> createTextChannels(guild, createdCategory, body.serverName()));

                // Quando os canais forem criados, salva no banco de dados
                channelsFuture.thenAccept(channelsIds -> {
                    ServerDashboard dashboard = new ServerDashboard();
                    dashboard.setServerName(body.serverName());
                    dashboard.setGuildId(body.guildId());
                    dashboard.setToken(body.token());
                    dashboard.setDashChannel(channelsIds.get(0));
                    dashboard.setAlertsChannel(channelsIds.get(1));
                    dashboard.setCryoChannel(channelsIds.get(2));
                    dashboard.setPlayersChannel(channelsIds.get(3));
                    dashboard.setParasaurChannel(channelsIds.get(4));
                    dashboard.setServerstatusChannel(channelsIds.get(5));
                    repository.save(dashboard);
                }).join(); // Garante que o código espere até todas as operações terminarem

                return ResponseEntity.ok("Server dashboard registered successfully");
            } else {
                return ResponseEntity.badRequest().body("Dashboard already exists");
            }

        }

        return ResponseEntity.badRequest().body("Guild not found");
    }

    private CompletableFuture<List<String>> createTextChannels(Guild guild, Category category, String serverName) {
        List<String> channelsIds = new ArrayList<>();
        String suffix = serverName.substring(serverName.length() - 4);//channelsIds

        CompletableFuture<Void> dashFuture = guild.createTextChannel("dash-" + suffix, category)
                .submit() // Cria o canal e retorna um Future
                .thenAccept(channel -> {
                    // Adiciona o ID do canal à lista
                    channelsIds.add(channel.getId());

                    // Envia uma mensagem ao canal
                    channel.sendMessage("Bot update every 1m!").queue();
                });

        CompletableFuture<Void> tribeLogFuture = guild.createTextChannel("tribe-log-" + suffix, category)
                .submit().thenAccept(channel -> channelsIds.add(channel.getId()));

        CompletableFuture<Void> alertsFuture = guild.createTextChannel("alerts-" + suffix, category)
                .submit().thenAccept(channel -> channelsIds.add(channel.getId()));

        CompletableFuture<Void> cryoFuture = guild.createTextChannel("cryo-" + suffix, category)
                .submit().thenAccept(channel -> channelsIds.add(channel.getId()));

        CompletableFuture<Void> playersFuture = guild.createTextChannel("players-" + suffix, category)
                .submit().thenAccept(channel -> channelsIds.add(channel.getId()));

        CompletableFuture<Void> parasaurFuture = guild.createTextChannel("parasaur-" + suffix, category)
                .submit().thenAccept(channel -> channelsIds.add(channel.getId()));

        CompletableFuture<Void> serverStatusFuture = guild.createTextChannel("serverstatus-" + suffix, category)
                .submit().thenAccept(channel -> channelsIds.add(channel.getId()));

        // Espera até que todas as operações de criação de canais sejam concluídas
        return CompletableFuture.allOf(
                dashFuture, tribeLogFuture, alertsFuture, cryoFuture,
                playersFuture, parasaurFuture, serverStatusFuture
        ).thenApply(v -> channelsIds); // Retorna a lista de IDs de canais após todas as operações
    }

    public List<ServerDashboard> findAllDashboards() {
        return repository.findAll();
    }
}
