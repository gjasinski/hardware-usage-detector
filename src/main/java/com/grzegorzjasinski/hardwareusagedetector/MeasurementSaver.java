package com.grzegorzjasinski.hardwareusagedetector;

import io.reactivex.subjects.PublishSubject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

public class MeasurementSaver implements Runnable {
    private final PublishSubject<Measurement> measurements;
    private final PrintWriter printWriter;

    MeasurementSaver(PublishSubject<Measurement> measurements, String dir) throws FileNotFoundException,
            UnsupportedEncodingException {
        this.measurements = measurements;
        this.printWriter = new PrintWriter(dir + "/" + LocalDate.now().toString(), "UTF-8");
    }

    @Override
    public void run() {
        measurements.subscribe(this::saveMeasurement);
    }

    private void saveMeasurement(Measurement measurement) {
        this.printWriter.println(measurement.toStoringFormat());
        this.printWriter.flush();
    }

}
