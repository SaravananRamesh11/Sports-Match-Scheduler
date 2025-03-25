package com.sarva.distributed.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class BasketballMatchService {

    @Value("${allsports.api.key}")  // Read API key from application.properties
    private String apiKey;

    // Method to dynamically parse date formats
    private static LocalDate parseDate(String date) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (date.charAt(2) == '-') { // If 3rd char is '-', assume "dd-MM-yyyy"
                return LocalDate.parse(date, formatter1);
            } else {
                return LocalDate.parse(date, formatter2);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + date);
        }
    }

    public List<String> getBasketballMatches(String fromDate, String toDate) {
        // Convert to "yyyy-MM-dd" format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedFromDate = parseDate(fromDate).format(outputFormatter);
        String formattedToDate = parseDate(toDate).format(outputFormatter);

        // Build API URL dynamically
        String API_URL = "https://apiv2.allsportsapi.com/basketball/?met=Fixtures&APIkey=" 
                         + apiKey + "&from=" + formattedFromDate + "&to=" + formattedToDate;

        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = null;

        try {
            response = restTemplate.getForObject(API_URL, JsonNode.class);
            // Save response for debugging
            Files.writeString(Path.of("basketball_api_response.json"), response.toPrettyString());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList(); // Return empty list on error
        }

        // Define the allowed league_key values
        Set<Integer> allowedLeagues = Set.of(784, 759, 766, 995, 1128);
        Map<String, List<String>> leagueMatches = new TreeMap<>(); // TreeMap to maintain sorted order

        if (response != null && response.has("result")) {
            for (JsonNode event : response.get("result")) {
                int leagueKey = event.get("league_key").asInt();

                if (allowedLeagues.contains(leagueKey)) {
                    String homeTeam = event.has("event_home_team") ? event.get("event_home_team").asText() : "Unknown Team";
                    String awayTeam = event.has("event_away_team") ? event.get("event_away_team").asText() : "Unknown Team";
                    String leagueName = event.has("league_name") ? event.get("league_name").asText() : "Unknown League";
                    String eventTime = event.has("event_time") ? event.get("event_time").asText() : "Unknown Time";

                    String matchDetail = String.format("%s vs %s (%s, time: %s)", 
                                                        homeTeam, awayTeam, leagueName, eventTime);
                    
                    leagueMatches.computeIfAbsent(leagueName, k -> new ArrayList<>()).add(matchDetail);
                }
            }
        }

        // Flatten sorted matches into a single list
        List<String> sortedMatches = new ArrayList<>();
        leagueMatches.values().forEach(sortedMatches::addAll);

        System.out.println(sortedMatches);
        return sortedMatches;
    }
}
