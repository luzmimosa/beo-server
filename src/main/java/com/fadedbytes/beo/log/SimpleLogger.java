package com.fadedbytes.beo.log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.function.Supplier;

public class SimpleLogger extends BaseLogger {

    private File latestFile;
    private FileOutputStream latestFileStream = null;

    public SimpleLogger() {
        super();

        try {
            File logFolder = new File("./logs");
            if (!logFolder.exists()) {
                logFolder.mkdir();
            }
        } catch (Exception ignored) {}

        latestFile = new File("./logs/latest.log");

        if (latestFile.exists()) {
            Path path = latestFile.toPath();

            try {
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);

                FileTime creationTime = attributes.lastModifiedTime();

                String oldFileName = "./logs/" + creationTime.toString().replace(":", "-") + ".log";

                File oldFile = new File(oldFileName);

                while (oldFile.exists()) {
                    creationTime = FileTime.fromMillis(creationTime.toMillis() + 1);
                    oldFileName = "./logs/" + creationTime.toString().replace(":", "-") + ".log";
                    oldFile = new File(oldFileName);
                }

                latestFile.renameTo(oldFile);

            } catch (Exception ignored) {}

            // clear the latest log file
            try {
                Files.writeString(path, "");
            } catch (Exception ignored) {}
        }


        try {
            latestFile = new File("./logs/latest.log");
            latestFile.createNewFile();
        } catch (Exception ignored) { }

        try {
            latestFileStream = new FileOutputStream(latestFile);
        } catch (Exception ignored) {}

    }

    @Override
    public void log(@NotNull LogLevel level, @NotNull Supplier<String> messageSupplier) {

        if (latestFileStream != null) {
            try {
                LocalTime time = LocalTime.now();
                String timeString = time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
                String message = "[" + timeString + "] " + "[" + Thread.currentThread().getName() + "/" + level + "]: " + messageSupplier.get() + "\n";

                latestFileStream.write(message.getBytes());
            } catch (Exception ignored) {}
        }

        HashMap<LogSubscriber, LogLevel> currentSubscribers = this.getSubscribers();
        for (LogSubscriber subscriber : currentSubscribers.keySet()) {
            LogLevel subscriberLevel = currentSubscribers.get(subscriber);
            if (subscriberLevel.isAtLeast(level)) {
                subscriber.processLog(level, messageSupplier.get());
            }
        }
    }
}
