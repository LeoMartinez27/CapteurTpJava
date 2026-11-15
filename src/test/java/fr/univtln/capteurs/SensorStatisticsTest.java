package fr.univtln.capteurs;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import javax.measure.quantity.Temperature;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

class SensorStatisticsTest {

    @Test
    void statistiquesAvecUnitesMelangees() {
        Sensor<Temperature> sensor = new Sensor<>("T_1");
        Instant now = Instant.now();
        sensor.addMeasurement(now, Quantities.getQuantity(20, Units.CELSIUS))
              .addMeasurement(now.plusSeconds(1), Quantities.getQuantity(30, Units.CELSIUS))
              .addMeasurement(now.plusSeconds(2), Quantities.getQuantity(303.15, Units.KELVIN)); // = 30 °C

        assertEquals(20.0, sensor.min(Units.CELSIUS).orElseThrow().getValue().doubleValue(), 1e-6);
        assertEquals(30.0, sensor.max(Units.CELSIUS).orElseThrow().getValue().doubleValue(), 1e-6);
        assertEquals(80.0 / 3, sensor.average(Units.CELSIUS).orElseThrow().getValue().doubleValue(), 1e-6);
    }

    @Test
    void capteurVideRetourneOptionalVide() {
        Sensor<Temperature> sensor = new Sensor<>("T_vide");
        assertTrue(sensor.average(Units.CELSIUS).isEmpty());
        assertTrue(sensor.min(Units.CELSIUS).isEmpty());
        assertTrue(sensor.max(Units.CELSIUS).isEmpty());
        assertTrue(sensor.statistics().isEmpty());
    }

    @Test
    void zScoreDetecteLAnomalie() {
        Sensor<Temperature> sensor = new Sensor<>("T_1");
        Instant now = Instant.now();
        for (int i = 0; i < 20; i++) {
            sensor.addMeasurement(now.plusSeconds(i), Quantities.getQuantity(20 + (i % 2), Units.CELSIUS));
        }
        sensor.addMeasurement(now.plusSeconds(20), Quantities.getQuantity(100, Units.CELSIUS)); // anomalie

        var anomalies = sensor.anomalies(3);
        assertEquals(1, anomalies.size());
        assertEquals(100.0, anomalies.get(0).value().to(Units.CELSIUS).getValue().doubleValue(), 1e-6);
    }

    @Test
    void pasDAnomalieSiToutEstIdentique() {
        Sensor<Temperature> sensor = new Sensor<>("T_1");
        Instant now = Instant.now();
        for (int i = 0; i < 5; i++) {
            sensor.addMeasurement(now.plusSeconds(i), Quantities.getQuantity(20, Units.CELSIUS));
        }
        assertTrue(sensor.anomalies(3).isEmpty());
    }
}
