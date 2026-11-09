package fr.univtln.capteurs;

import java.util.Collections;
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

    @Override
    public String toString() {
        return "SensorNetwork[%s, %d capteurs]".formatted(name, sensors.size());
    }
}
