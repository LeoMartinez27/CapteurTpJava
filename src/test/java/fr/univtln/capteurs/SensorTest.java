package fr.univtln.capteurs;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Iterator;
import javax.measure.quantity.Temperature;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

class SensorTest {

    @Test
    void ajoutEtParcoursDesMesures() {
        Sensor<Temperature> sensor = new Sensor<>("T_1");
        Instant now = Instant.now();

        sensor.addMeasurement(now, Quantities.getQuantity(25, Units.CELSIUS))
              .addMeasurement(now.plusSeconds(15), Quantities.getQuantity(300.15, Units.KELVIN));

        Iterator<Measurement<Temperature>> it = sensor.measurementIterator();
        int count = 0;
        while (it.hasNext()) {
            assertEquals(sensor, it.next().sensor());
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void iteratorNePermetPasLaSuppression() {
        Sensor<Temperature> sensor = new Sensor<>("T_1");
        sensor.addMeasurement(Instant.now(), Quantities.getQuantity(20, Units.CELSIUS));

        Iterator<Measurement<Temperature>> it = sensor.measurementIterator();
        it.next();
        assertThrows(UnsupportedOperationException.class, it::remove);
    }
}
