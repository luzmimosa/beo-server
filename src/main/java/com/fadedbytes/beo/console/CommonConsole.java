package com.fadedbytes.beo.console;

import com.fadedbytes.beo.console.message.ConsoleMessage;
import com.fadedbytes.beo.log.BeoLogger;
import com.fadedbytes.beo.log.LogLevel;
import com.fadedbytes.beo.util.data.structure.CircularQueue;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public sealed abstract class CommonConsole
        implements BeoConsole
        permits ServerConsole, UserConsole
{
    private static final ThreadGroup CONSOLE_THREAD_GROUP = new ThreadGroup("Console group");

    private final @NotNull NamespacedKey consoleKey;
    private final @NotNull ArrayList<OutputStream> outputStreams;
    private final @NotNull CircularQueue<ConsoleMessage> history;
    private final @NotNull ArrayList<BeoLogger> subscribedLoggers;
    private final @NotNull Thread listeningThread;
    private boolean active;

    public CommonConsole(
            @NotNull NamespacedKey consoleKey,
            int historyLimit,
            OutputStream... outputStreams
    ) {
        this.consoleKey = consoleKey;

        this.outputStreams = new ArrayList<>(Arrays.stream(outputStreams).toList());
        this.history = new CircularQueue<>(historyLimit);
        this.subscribedLoggers = new ArrayList<>();
        this.listeningThread = new Thread(CONSOLE_THREAD_GROUP, this::listen);
    }

    @Override
    public void bindLogger(@NotNull BeoLogger logger, LogLevel level) {
        if (!subscribedLoggers.contains(logger)) {
            subscribedLoggers.add(logger);
            logger.subscribe(level, this);
        }
    }

    @Override
    public void unbindLogger(@NotNull BeoLogger logger) {
        if (subscribedLoggers.contains(logger)) {
            subscribedLoggers.remove(logger);
            logger.unsubscribe(this);
        }
    }

    @Override
    public synchronized void addOutputStream(@NotNull OutputStream outputStream) {
        if (!outputStreams.contains(outputStream)) {
            outputStreams.add(outputStream);
        }
    }

    @Override
    public synchronized void removeOutputStream(@NotNull OutputStream outputStream) {
        outputStreams.remove(outputStream);
    }

    @Override
    public Set<OutputStream> getOutputStreams() {
        return Set.copyOf(outputStreams);
    }

    private void listen() {
        while (this.shouldListen()) {
            
        }
    }

    private boolean shouldListen() {
        return this.active;
    }

    @Override
    public void startConsole() {
        if (this.active) return;
        this.active = true;
        this.listeningThread.start();
    }

    @Override
    public void close() {
        this.active = false;
        for (OutputStream outputStream : outputStreams) {
            try {
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (BeoLogger logger : subscribedLoggers) {
            logger.unsubscribe(this);
        }

        this.listeningThread.interrupt();
    }

    @Override
    public int historyLimit() {
        return this.history.capacity();
    }

    @Override
    public void processLog(@NotNull LogLevel level, @NotNull String message) {
        this.printMessage(Thread.currentThread().getName() + "/" + level.name(), ConsoleMessage.of(message));
    }

    @Override
    public void printMessage(@Nullable String origin, @NotNull ConsoleMessage message) {
        String formattedMessage = message.format(origin).trim() + "\n";

        for (OutputStream outputStream : outputStreams) {
            try {
                outputStream.write(formattedMessage.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
