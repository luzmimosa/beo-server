package com.fadedbytes.beo.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.fadedbytes.beo.api.level.Level;
import com.fadedbytes.beo.server.BeoServer;
import org.yaml.snakeyaml.Yaml;

public class LevelLoader {
    private final String folderPath;
    private final File folder;
    private final BeoServer server;

    public LevelLoader(String folderPath, BeoServer server) {
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

    public List<Level> loadLevels() {
        List<Level> levels = new ArrayList<>();

        File[] jarFiles = folder.listFiles((dir, name) -> name.endsWith(".jar"));
        server.getLogger().debug("Found " + jarFiles.length + " level files in " + folder.getAbsolutePath());

        if (jarFiles != null) {
            for (File jarFile : jarFiles) {
                try (JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFile))) {
                    JarEntry entry;
                    while ((entry = jarInputStream.getNextJarEntry()) != null) {
                        if (entry.getName().equals("levelmeta.yml")) {
                            Level level = loadLevelFromYaml(jarInputStream, jarFile);
                            if (level != null) {
                                levels.add(level);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return levels;
    }

    private Level loadLevelFromYaml(InputStream inputStream, File file) {
        Yaml yaml = new Yaml();
        Level level = null;

        try {
            Map<String, Object> data = yaml.load(inputStream);
            String mainClassName = (String) data.get("main");

            URL[] urls = { file.toURI().toURL() };
            URLClassLoader classLoader = new URLClassLoader(urls);

            Class<?> levelClass = Class.forName(mainClassName, true, classLoader);
            if (Level.class.isAssignableFrom(levelClass)) {
                level = (Level) levelClass.getDeclaredConstructor(BeoServer.class).newInstance(server);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return level;
    }
}
