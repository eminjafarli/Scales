package com.agrarco.agrovers.Models;

import com.fazecast.jSerialComm.SerialPort;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CASScaleReader {

    public enum ScaleMode {
        CI201A,
        CI200A
    }

    private final SerialPort comPort;
    private final ScaleMode mode;
    private volatile boolean running = false;
    private final List<Consumer<String>> listeners = new ArrayList<>();

    public CASScaleReader(String portName, ScaleMode mode) {
        this.mode = mode;
        comPort = SerialPort.getCommPort(portName);
        comPort.setBaudRate(9600);
        comPort.setNumDataBits(8);
        comPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        comPort.setParity(SerialPort.NO_PARITY);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 500, 500);
    }

    public boolean connect() {
        return comPort.openPort();
    }

    public void disconnect() {
        running = false;
        if (comPort.isOpen()) {
            comPort.closePort();
        }
    }

    public void startReading(int intervalMs) {
        if (!comPort.isOpen()) {
            throw new IllegalStateException("Serial port not open. Call connect() first.");
        }

        running = true;
        Thread readerThread = new Thread(() -> {
            try {
                while (running) {

                    if (mode == ScaleMode.CI201A) {
                        String command = "SI\r\n";
                        comPort.writeBytes(command.getBytes(StandardCharsets.US_ASCII), command.length());
                        Thread.sleep(100);
                    }

                    if (comPort.bytesAvailable() > 0) {
                        byte[] buffer = new byte[256];
                        int bytesRead = comPort.readBytes(buffer, buffer.length);
                        if (bytesRead > 0) {
                            String response = new String(buffer, 0, bytesRead, StandardCharsets.US_ASCII).trim();
                            String weight = parseWeight(response);

                            if (weight != null && !weight.isEmpty()) {
                                notifyListeners(weight);
                            }
                        }
                    }

                    Thread.sleep(intervalMs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private String parseWeight(String response) {
        response = response.replaceAll("[^\\x20-\\x7E]", "").trim();

        Matcher matcher = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*kg").matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }

        matcher = Pattern.compile("\\b(\\d+(?:\\.\\d+)?)\\b").matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public void addWeightListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    private void notifyListeners(String weight) {
        for (Consumer<String> listener : listeners) {
            listener.accept(weight);
        }
    }

    public static void main(String[] args) {
        CASScaleReader scale = new CASScaleReader("COM3", ScaleMode.CI200A);

        if (scale.connect()) {
            System.out.println("Connected to CAS Scale!");
            scale.addWeightListener(weight -> {
                System.out.println("Weight: " + weight + " kg");
            });
            scale.startReading(500);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scale.disconnect();
        } else {
            System.out.println("Failed to connect to scale!");
        }
    }
}
