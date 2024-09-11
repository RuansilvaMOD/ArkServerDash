package com.ruan.botdashboard.service;

import com.ruan.botdashboard.entities.ServerDashboard;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduledTaskService {

    @Autowired
    private JDA jda;

    @Autowired
    private ServerDashboardService serverDashboardService;

    @Scheduled(fixedRate = 60000) // 1 minuto
    public void fetchDataFromDatabase() {

        try{
            TimeUnit.SECONDS.sleep(10);
            URL url = new URL("https://cdn2.arkdedicated.com/servers/asa/officialserverlist.json");

            // Abrir conexão
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Definir o método de requisição como GET
            connection.setRequestMethod("GET");

            // Obter o código de resposta
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Ler a resposta
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            // Fechar as streams
            in.close();
            connection.disconnect();

            JSONArray jsonArray = new JSONArray(content.toString());

            JSONObject foundSession = null;

            List<ServerDashboard> dashboards = serverDashboardService.findAllDashboards();
            System.out.println("Buscando dashboards: " + dashboards.size() + " encontrados");

            List<String> sessionsNamesToFind = new ArrayList<>();
            List<String> dashIds = new ArrayList<>();

            for (ServerDashboard dashboard : dashboards) {

                sessionsNamesToFind.add(dashboard.getServerName());
                //jda.getGuildById(dashboard.getGuildId()).getTextChannelById(dashboard.getDashChannel()).editMessageEmbedsById(jda.getTextChannelById(dashboard.getDashChannel()).getLatestMessageId(), );
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject sessionObject = jsonArray.getJSONObject(i);
                String sessionName = sessionObject.getString("Name");

                for(String sessionNameToFind : sessionsNamesToFind) {
                    if (sessionName.equals(sessionNameToFind)) {
                        foundSession = sessionObject;

                        // Exibir os dados da sessão encontrada
                        System.out.println("Found Session Data: " + foundSession.toString(2)); // formata o JSON para uma exibição mais legível

                        long unixTimestamp = Instant.now().getEpochSecond();

                        // Formatar o timestamp para o Discord
                        String discordTimestamp = "<t:" + unixTimestamp + ":R>"; // ":R" para "relative time"

                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle("Tribelog dashboard for " + sessionName.substring(sessionName.length() - 4));
                        embedBuilder.setColor(Color.GREEN);

                        embedBuilder.addField("Status", ":green_circle: Online", true); // Inline
                        embedBuilder.addField(":pencil: Servername", foundSession.getString("Name"), true); // Inline
                        embedBuilder.addField(":map: Map", foundSession.getString("MapName"), true); // Inline
                        embedBuilder.addField(":video_game: Current players", foundSession.getInt("NumPlayers") + "/70", true); // Inline (na mesma linha)
                        embedBuilder.addField(":busts_in_silhouette: Tribemembers", "noone streaming", true); // Inline (na mesma linha)
                        embedBuilder.addField(":timer: Upload Timer", "You need TEK gauntlets while streaming the tribelog to see this data", true);
                        embedBuilder.addField(":house_with_garden: IP:Port", foundSession.getString("IP") + ":" + foundSession.getInt("Port"), true);
                        embedBuilder.addField(":clock1: Current day", foundSession.getString("DayTime"), true);
                        embedBuilder.addField(":signal_strength: Ping", ""+foundSession.getInt("ServerPing"), true);
                        embedBuilder.addField(":chart_with_upwards_trend: Last stream", discordTimestamp, true);
                        embedBuilder.addField(":clock830: Last updated", "noone streaming", true);
                        embedBuilder.addField(":people_holding_hands: Tribemember online", "noone streaming", true);
                        embedBuilder.setFooter("Tribelogbot by Ruan");


                        for (ServerDashboard dashboard : dashboards) {

                            if(dashboard.getServerName().equals(sessionName)) {
                                //jda.getGuildById(dashboard.getGuildId()).getTextChannelById(dashboard.getDashChannel()).editMessageById(jda.getTextChannelById(dashboard.getDashChannel()).getLatestMessageId(), "").queue();
                                jda.getGuildById(dashboard.getGuildId()).getTextChannelById(dashboard.getDashChannel()).editMessageEmbedsById(jda.getTextChannelById(dashboard.getDashChannel()).getLatestMessageId(), embedBuilder.build()).queue();
                            }
                            //jda.getGuildById(dashboard.getGuildId()).getTextChannelById(dashboard.getDashChannel()).editMessageEmbedsById(jda.getTextChannelById(dashboard.getDashChannel()).getLatestMessageId(), );
                        }

                        // bot.getGuildById("1099342694502895748").getTextChannelById("1283147846270587011").sendMessageEmbeds(embedBuilder.build()).queue();
                        // esse pro 2454 bot.getGuildById("1099342694502895748").getTextChannelById("1283169992203833364").sendMessageEmbeds(embedBuilder.build()).queue();
                       // bot.getGuildById("1099342694502895748").getTextChannelById("1283147846270587011").editMessageEmbedsById("1283166420829601846", embedBuilder.build()).queue();

                    }
                }
            }

            System.out.println("Fetching data from database...");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
