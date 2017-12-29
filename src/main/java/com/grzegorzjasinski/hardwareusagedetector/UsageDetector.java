package com.grzegorzjasinski.hardwareusagedetector;

import com.sun.management.OperatingSystemMXBean;
import io.reactivex.subjects.PublishSubject;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;

public class UsageDetector implements Runnable {
    public final static int MEASUREMENT_INTERVAL_IN_MS = 500;
    private final PublishSubject<Measurement> publishSubject;
    private volatile boolean shutDown = false;

    UsageDetector() {
        this.publishSubject = PublishSubject.create();
    }

    public PublishSubject<Measurement> getPublishSubject() {
        return publishSubject;
    }

    private Measurement createMeasurement() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long memoryUsage = operatingSystemMXBean.getTotalPhysicalMemorySize() - operatingSystemMXBean.getFreePhysicalMemorySize();
        double systemCpuLoad = operatingSystemMXBean.getSystemCpuLoad();
        long swapUsage = operatingSystemMXBean.getTotalSwapSpaceSize() - operatingSystemMXBean.getFreeSwapSpaceSize();
        return new Measurement(LocalDateTime.now(), memoryUsage, systemCpuLoad, swapUsage);
    }

    void shutDown() {
        this.shutDown = true;
    }

    @Override
    public void run() {
        try {
            while (!shutDown) {
                Measurement measurement = createMeasurement();
                publishSubject.onNext(measurement);
                Thread.sleep(MEASUREMENT_INTERVAL_IN_MS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            publishSubject.onComplete();
        }
    }
}
