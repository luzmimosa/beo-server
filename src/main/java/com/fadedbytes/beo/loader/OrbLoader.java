package com.fadedbytes.beo.loader;

import com.fadedbytes.beo.api.level.Level;
import com.fadedbytes.beo.api.level.world.entity.Orb;
import com.fadedbytes.beo.server.BeoServer;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class OrbLoader {
    private final String folderPath;
    private final File folder;
    private final BeoServer server;
    private final HashMap<String, List<Constructor<Orb>>> orbLevelMap = new HashMap<>();

    public OrbLoader(String folderPath, BeoServer server) {
        this.folderPath = folderPath;
        this.server = server;

        this.folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public String getFolderPath() {
        return folder.getAbsolutePath();
    }

    public void loadOrbs() {
        orbLevelMap.clear();

        File[] jarFiles = folder.listFiles((dir, name) -> name.endsWith(".jar"));

        if (jarFiles == null) {
            server.getLogger().error("No orb files found in " + folder.getAbsolutePath());
            return;
        }
        server.getLogger().debug("Found " + jarFiles.length + " orb files in " + folder.getAbsolutePath());

        for (File jarFile : jarFiles) {
            try (JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFile))) {
                JarEntry entry;
                while ((entry = jarInputStream.getNextJarEntry()) != null) {
                    if (entry.getName().equals("orbmeta.yml")) {
                        Map<String, Object> yamlData = extractYamlData(jarInputStream);
                        Constructor<Orb> orb = orbConstructorFromYaml(yamlData, jarFile);
                        if (orb != null) {
                            for (String levelName : extractChallengesFromYaml(yamlData)) {
                                orbLevelMap.computeIfAbsent(levelName, k -> new ArrayList<>()).add(orb);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Object> extractYamlData(InputStream inputStream) {
        return new Yaml().load(inputStream);
    }

    private Constructor<Orb> orbConstructorFromYaml(Map<String, Object> yamlData, File file) {
        Constructor<Orb> orbConstructor = null;

        try {
            String mainClassName = (String) yamlData.get("main");

            URL[] urls = { file.toURI().toURL() };
            URLClassLoader classLoader = new URLClassLoader(urls);

            Class<?> levelClass = Class.forName(mainClassName, true, classLoader);
            if (Orb.class.isAssignableFrom(levelClass)) {
                orbConstructor = (Constructor<Orb>) levelClass.getDeclaredConstructor();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orbConstructor;
    }

    private List<String> extractChallengesFromYaml(Map<String, Object> yamlData) {
        List<String> challenges = new ArrayList<>();

        try {
            List<String> challengesList = (List<String>) yamlData.get("challenges");
            if (challengesList != null) {
                challenges.addAll(challengesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return challenges;
    }

    public void joinOrbsForLevel(Level level) {

        List<Constructor<Orb>> orbConstructors = orbLevelMap.get(level.KEY.getKey());
        if (orbConstructors != null) {
            for (Constructor<Orb> orbConstructor : orbConstructors) {
                try {
                    Orb orb = orbConstructor.newInstance();
                    level.addOrb(orb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}