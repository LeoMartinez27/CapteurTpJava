package fr.univtln.capteurs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;

public class SensorNetwork {
    private final String name;
    private final Map<String, Sensor<?>> sensors = new LinkedHashMap<>();

    private SensorNetwork(String name) {
        this.name = name;
    }

    public static SensorNetwork of(String name) {
        return new SensorNetwork(name);
    }

    public Sensor<Temperature> addTemperatureSensor(String id) {
        return register(new Sensor<Temperature>(id));
    }

    public Sensor<Pressure> addPressureSensor(String id) {
        return register(new Sensor<Pressure>(id));
    }

    // Méthode générique privée : évite de dupliquer la vérification d'unicité
    private <S extends Sensor<?>> S register(S sensor) {
        if (sensors.containsKey(sensor.getName())) {
            throw new IllegalArgumentException("Capteur déjà présent : " + sensor.getName());
        }
        sensors.put(sensor.getName(), sensor);
        return sensor;
    }

    public Iterator<Sensor<?>> sensorIterator() {
        return Collections.unmodifiableMap(sensors).values().iterator();
    }

    /** Statistiques par capteur (id -> stats), dans l'unité SI de chaque capteur. */
    public Map<String, DoubleSummaryStatistics> statistics() {
        Map<String, DoubleSummaryStatistics> result = new LinkedHashMap<>();
        sensors.forEach((id, sensor) -> sensor.statistics().ifPresent(st -> result.put(id, st)));
        return result;
    }

    /** Anomalies (|z-score| > seuil) de tous les capteurs du réseau. */
    public List<Measurement<?>> anomalies(double threshold) {
        List<Measurement<?>> result = new ArrayList<>();
        sensors.values().forEach(sensor -> result.addAll(sensor.anomalies(threshold)));
        return result;
    }

    @Override
    public String toString() {
        return "SensorNetwork[%s, %d capteurs]".formatted(name, sensors.size());
    }
}
