package fr.univtln.capteurs;

import java.time.Instant;
import javax.measure.Quantity;

/** Une mesure : quel capteur, à quel instant, quelle valeur (avec son unité). */
public record Measurement<Q extends Quantity<Q>>(Sensor<Q> sensor, Instant timestamp, Quantity<Q> value) {

    @Override
    public String toString() {
        return "Measurement[%s @ %s = %s]".formatted(sensor.getName(), timestamp, value);
    }
}
