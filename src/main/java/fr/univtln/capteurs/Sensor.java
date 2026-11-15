package fr.univtln.capteurs;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.ToDoubleFunction;
import javax.measure.Quantity;
import javax.measure.Unit;
import tech.units.indriya.quantity.Quantities;

public class Sensor<Q extends Quantity<Q>> {
    private final String name;
    private final List<Measurement<Q>> measurements = new ArrayList<>();

    public Sensor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Sensor<Q> addMeasurement(Instant timestamp, Quantity<Q> value) {
        measurements.add(new Measurement<>(this, timestamp, value));
        return this;
    }

    public Iterator<Measurement<Q>> measurementIterator() {
        return Collections.unmodifiableList(measurements).iterator();
    }

    // ---------- Statistiques ----------

    /** Statistiques dans l'unité demandée (chaque mesure est d'abord convertie). */
    public DoubleSummaryStatistics statistics(Unit<Q> unit) {
        return measurements.stream()
                .mapToDouble(m -> m.value().to(unit).getValue().doubleValue())
                .summaryStatistics();
    }

    /** Statistiques dans l'unité SI de la grandeur ; vide si aucune mesure. */
    public Optional<DoubleSummaryStatistics> statistics() {
        if (measurements.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(statistics(systemUnit()));
    }

    public Optional<Quantity<Q>> average(Unit<Q> unit) {
        return stat(unit, DoubleSummaryStatistics::getAverage);
    }

    public Optional<Quantity<Q>> min(Unit<Q> unit) {
        return stat(unit, DoubleSummaryStatistics::getMin);
    }

    public Optional<Quantity<Q>> max(Unit<Q> unit) {
        return stat(unit, DoubleSummaryStatistics::getMax);
    }

    private Optional<Quantity<Q>> stat(Unit<Q> unit, ToDoubleFunction<DoubleSummaryStatistics> extractor) {
        if (measurements.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Quantities.getQuantity(extractor.applyAsDouble(statistics(unit)), unit));
    }

    private Unit<Q> systemUnit() {
        return measurements.get(0).value().getUnit().getSystemUnit();
    }

    // ---------- Détection d'anomalies (z-score) ----------

    /** Mesures dont |z-score| dépasse le seuil. Le z-score ne dépend pas de l'unité choisie. */
    public List<Measurement<Q>> anomalies(double threshold) {
        if (measurements.size() < 2) {
            return List.of();
        }
        Unit<Q> unit = systemUnit();
        double[] v = measurements.stream()
                .mapToDouble(m -> m.value().to(unit).getValue().doubleValue())
                .toArray();
        double mean = Arrays.stream(v).average().orElse(0);
        double std = Math.sqrt(Arrays.stream(v).map(x -> (x - mean) * (x - mean)).sum() / v.length);
        if (std == 0) {
            return List.of();
        }
        List<Measurement<Q>> result = new ArrayList<>();
        for (int i = 0; i < v.length; i++) {
            if (Math.abs((v[i] - mean) / std) > threshold) {
                result.add(measurements.get(i));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Sensor[%s, %d mesures]".formatted(name, measurements.size());
    }
}
