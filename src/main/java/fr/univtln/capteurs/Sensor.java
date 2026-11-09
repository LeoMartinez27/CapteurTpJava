package fr.univtln.capteurs;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.measure.Quantity;

public class Sensor<Q extends Quantity<Q>> {
    private final String name;
    private final List<Measurement<Q>> measurements = new ArrayList<>();

    public Sensor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** Retourne this pour permettre le chaînage : sensor.addMeasurement(...).addMeasurement(...) */
    public Sensor<Q> addMeasurement(Instant timestamp, Quantity<Q> value) {
        measurements.add(new Measurement<>(this, timestamp, value));
        return this;
    }

    public Iterator<Measurement<Q>> measurementIterator() {
        return Collections.unmodifiableList(measurements).iterator();
    }

    @Override
    public String toString() {
        return "Sensor[%s, %d mesures]".formatted(name, measurements.size());
    }
}
