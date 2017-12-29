package com.grzegorzjasinski.hardwareusagedetecor;

import java.time.LocalDateTime;

class Measurement {
    private final LocalDateTime localDateTime;
    private final long memoryUsage;
    private final double systemCpuLoad;
    private final long swapUsage;

    Measurement(LocalDateTime localDateTime, long memoryUsage, double systemCpuLoad, long swapUsage) {
        this.localDateTime = localDateTime;
        this.memoryUsage = memoryUsage;
        this.systemCpuLoad = systemCpuLoad;
        this.swapUsage = swapUsage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measurement that = (Measurement) o;

        if (memoryUsage != that.memoryUsage) return false;
        if (Double.compare(that.systemCpuLoad, systemCpuLoad) != 0) return false;
        return swapUsage == that.swapUsage;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (memoryUsage ^ (memoryUsage >>> 32));
        temp = Double.doubleToLongBits(systemCpuLoad);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (swapUsage ^ (swapUsage >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "memoryUsage=" + memoryUsage +
                ", systemCpuLoad=" + systemCpuLoad +
                ", swapUsage=" + swapUsage +
                '}';
    }

    private double convertBytesToGigaBytes(long bytes) {
        return ((double) bytes) / 1024 / 1024 / 1024;
    }

    public String toStoringFormat() {
        return String.format("%s,%f,%f,%f", this.localDateTime, convertBytesToGigaBytes(this.memoryUsage),
                this.systemCpuLoad, convertBytesToGigaBytes(this.swapUsage));
    }
}
