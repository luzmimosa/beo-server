package com.fadedbytes.beo.stats;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Scanner;

public class JvmStatProvider {
    private static JvmStatProvider instance;
    private static Thread statThread;

    private JvmStatProvider() {
        // Constructor privado para asegurar que solo haya una instancia de la clase
    }

    public static JvmStatProvider getInstance() {
        if (instance == null) {
            instance = new JvmStatProvider();
        }
        return instance;
    }

    public void startStatThread() {
        statThread = new Thread(() -> {
            while (true) {
                // Realizar operaciones de estadísticas aquí
                // Puedes llamar a los métodos correspondientes para obtener los valores deseados
                // y almacenarlos en variables o imprimirlos según sea necesario
                try {
                    Thread.sleep(1000); // Esperar 1 segundo antes de la siguiente recopilación de estadísticas
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        statThread.start();
    }

    public int getMaxMemory() {
        return (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024)); // Convertir a MB
    }

    public int getUsedMemory() {
        return (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)); // Convertir a MB
    }

    public int getRootSize(String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            long size = calculateFolderSize(folder);
            return (int) (size / (1024 * 1024)); // Convertir a MB
        }
        return 0;
    }

    private long calculateFolderSize(File folder) {
        long size = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += calculateFolderSize(file);
                }
            }
        }
        return size;
    }

    public int getRootSizeLimit() {
        return 10 * 1024; // Límite predefinido de 10 GB en MB
    }

    public int getJvmCoresUsed() {
        return Runtime.getRuntime().availableProcessors();
    }

    public int getSystemCoresTotal() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return osBean.getAvailableProcessors();
    }

    public double getAverageProcessorSpeed() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        String osName = osBean.getName().toLowerCase();

        if (osName.contains("windows")) {
            return getWindowsProcessorSpeed();
        } else if (osName.contains("mac")) {
            return getMacProcessorSpeed();
        } else if (osName.contains("linux") || osName.contains("unix")) {
            return getLinuxProcessorSpeed();
        }

        return 0.0;
    }

    private double getWindowsProcessorSpeed() {
        String[] cmd = {"wmic", "cpu", "get", "MaxClockSpeed"};
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.getOutputStream().close();
            Scanner scanner = new Scanner(process.getInputStream());
            while (scanner.hasNext()) {
                if (scanner.hasNextInt()) {
                    int speed = scanner.nextInt();
                    return speed / 1000.0; // Convertir a GHz
                } else {
                    scanner.next();
                }
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private double getMacProcessorSpeed() {
        String[] cmd = {"/usr/sbin/sysctl", "machdep.cpu.brand_string"};
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.getOutputStream().close();
            Scanner scanner = new Scanner(process.getInputStream());
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.contains("GHz")) {
                    int startIndex = line.indexOf("@") + 1;
                    int endIndex = line.indexOf("GHz");
                    String speedString = line.substring(startIndex, endIndex).trim();
                    double speed = Double.parseDouble(speedString);
                    return speed;
                }
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private double getLinuxProcessorSpeed() {
        String[] cmd = {"cat", "/proc/cpuinfo"};
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.getOutputStream().close();
            Scanner scanner = new Scanner(process.getInputStream());
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.startsWith("cpu MHz")) {
                    String[] parts = line.split(":");
                    String speedString = parts[1].trim();
                    double speed = Double.parseDouble(speedString);
                    return speed / 1000.0; // Convertir a GHz
                }
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public String generateCurrentJsonString() {
        JsonObject json = new JsonObject();

        json.addProperty("maxMemory", getMaxMemory());
        json.addProperty("usedMemory", getUsedMemory());
        json.addProperty("rootSize", getRootSize("./"));
        json.addProperty("rootSizeLimit", getRootSizeLimit());
        json.addProperty("jvmCoresUsed", getJvmCoresUsed());
        json.addProperty("systemCoresTotal", getSystemCoresTotal());
        json.addProperty("averageProcessorSpeed", getAverageProcessorSpeed());

        Gson gson = new Gson();
        return gson.toJson(json);
    }

}

