package com.grzegorzjasinski.hardwareusagedetector;

import com.sun.prism.Texture;
import io.reactivex.subjects.PublishSubject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MeasurementDisplay extends Application {
    private static final String SAVE_TO = "/home/grzegorz/hdd/hardware_usage";
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private volatile double memoryUsage = 0;
    private volatile double cpuUsage = 0;
    private volatile double swapUsage = 0;
    private volatile LocalDateTime dateTime = LocalDateTime.now();
    private Label memoryUsageLbl;
    private Label cpuUsageLbl;
    private Label swapUsageLbl;
    private Label dateTimeLbl;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException, UnsupportedEncodingException {
        initializeDataFlow();
        drawScene(primaryStage);
        updateStageTask();
    }

    private void updateStageTask() {
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                int i = 0;
                while (true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            memoryUsageLbl.setText(String.format("Memory usage: %.2f GB", memoryUsage));
                            cpuUsageLbl.setText(String.format("CPU usage: %.2f %%", cpuUsage * 100));
                            swapUsageLbl.setText(String.format("Swap usage: %.2f GB", swapUsage));
                            dateTimeLbl.setText(dateTime.toLocalTime().format(dtf));
                        }
                    });
                    Thread.sleep(UsageDetector.MEASUREMENT_INTERVAL_IN_MS);
                }
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void drawScene(Stage primaryStage) {
        this.memoryUsageLbl = new Label();
        this.cpuUsageLbl = new Label();
        this.swapUsageLbl = new Label();
        this.dateTimeLbl = new Label();
        primaryStage.setTitle("Memory usage App");

        StackPane root = new StackPane();
        root.getChildren().add(memoryUsageLbl);
        root.getChildren().add(cpuUsageLbl);
        root.getChildren().add(swapUsageLbl);
        root.getChildren().add(dateTimeLbl);
        StackPane.setAlignment(memoryUsageLbl, Pos.TOP_CENTER);
        StackPane.setAlignment(cpuUsageLbl, Pos.CENTER);
        StackPane.setAlignment(swapUsageLbl, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(dateTimeLbl, Pos.BOTTOM_LEFT);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    private void initializeDataFlow() throws FileNotFoundException, UnsupportedEncodingException {
        UsageDetector usageDetector = new UsageDetector();
        PublishSubject<Measurement> measurementsSubject = usageDetector.getPublishSubject();
        MeasurementSaver measurementSaver = new MeasurementSaver(measurementsSubject, SAVE_TO);
        new Thread(usageDetector).start();
        new Thread(measurementSaver).start();
        measurementsSubject.subscribe(this::updateValues);
    }

    private void updateValues(Measurement measurement) {
        this.memoryUsage = measurement.getMemoryUsage();
        this.cpuUsage = measurement.getSystemCpuLoad();
        this.swapUsage = measurement.getSwapUsage();
        this.dateTime = measurement.getLocalDateTime();
    }
}