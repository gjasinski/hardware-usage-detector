package com.grzegorzjasinski.hardwareusagedetector;

import io.reactivex.subjects.PublishSubject;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class App {
    private static final String SAVE_TO = "/home/grzegorz/hdd/hardware_usage";

    public static void main(String[] args) {
        try {
            UsageDetector usageDetector = new UsageDetector();
            PublishSubject<Measurement> publishSubject = usageDetector.getPublishSubject();
            MeasurementSaver measurementSaver = new MeasurementSaver(publishSubject, SAVE_TO);
            new Thread(usageDetector).start();
            new Thread(measurementSaver).start();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
