package com.fadedbytes.beo.stats;

import com.google.gson.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LocalStatManager {
    private static LocalStatManager instance;
    private static final String STATS_FOLDER_PATH = "./stats/";
    private final File STATS_FOLDER = new File(STATS_FOLDER_PATH);

    private LocalStatManager() {
        if (!STATS_FOLDER.exists()) {
            STATS_FOLDER.mkdir();
        }
    }

    public static LocalStatManager getInstance() {
        if (instance == null) {
            instance = new LocalStatManager();
        }
        return instance;
    }

    public void saveLevelStats(LevelStatResult levelStats) {
        String levelName = levelStats.levelKey().getKey();
        String fileName = STATS_FOLDER.getAbsolutePath() + "/" + levelName + ".json";
        File file = new File(fileName);

        JsonArray statsArray;

        if (!file.exists()) {
            try {
                file.createNewFile();
                statsArray = new JsonArray();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } else {
            try {
                statsArray = JsonParser.parseReader(new FileReader(file)).getAsJsonArray();

            } catch (FileNotFoundException e) {
                statsArray = new JsonArray();
            }
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("timestamp", levelStats.time());

        JsonArray orbStatsArray = new JsonArray();
        for (OrbStatResult orbStat : levelStats.orbStats()) {
            JsonObject orbStatObject = new JsonObject();
            orbStatObject.addProperty("orbName", orbStat.orbName());

            JsonObject statMapObject = new JsonObject();
            for (Map.Entry<String, Integer> entry : orbStat.statMap().entrySet()) {
                statMapObject.addProperty(entry.getKey(), entry.getValue());
            }
            orbStatObject.add("statMap", statMapObject);

            orbStatsArray.add(orbStatObject);
        }
        jsonObject.add("orbStats", orbStatsArray);

        statsArray.add(jsonObject);

        try (FileWriter writer = new FileWriter(file, false)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(statsArray);
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readStatsToJsonString(String levelName) {
        String fileName = STATS_FOLDER + "/" + levelName + ".json";
        File file = new File(fileName);

        if (!file.exists()) {
            return "[]";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            return jsonString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> savedLevelStatsNames() {
        return Arrays.stream(STATS_FOLDER.listFiles(file -> file.getName().endsWith(".json"))).map(File::getName).toList();
    }
}
