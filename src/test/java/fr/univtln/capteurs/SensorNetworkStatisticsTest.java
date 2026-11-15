package fr.univtln.capteurs;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

class SensorNetworkStatisticsTest {

    @Test
    void statistiquesDeTousLesCapteurs() {
        SensorNetwork net = SensorNetwork.of("R");
        Sensor<Temperature> t = net.addTemperatureSensor("T_1");
        Sensor<Pressure> p = net.addPressureSensor("P_1");
        net.addPressureSensor("P_vide");   // capteur vide : ignoré
        Instant now = Instant.now();

        t.addMeasurement(now, Quantities.getQuantity(20, Units.CELSIUS))
         .addMeasurement(now.plusSeconds(1), Quantities.getQuantity(30, Units.CELSIUS));
        p.addMeasurement(now, Quantities.getQuantity(100, Units.PASCAL));

        var stats = net.statistics();
        assertEquals(2, stats.size());
        assertEquals(293.15, stats.get("T_1").getMin(), 1e-6);   // en kelvin
        assertEquals(303.15, stats.get("T_1").getMax(), 1e-6);
        assertEquals(100.0, stats.get("P_1").getAverage(), 1e-6);
    }

    @Test
    void anomaliesDuReseau() {
        SensorNetwork net = SensorNetwork.of("R");
        Sensor<Temperature> t = net.addTemperatureSensor("T_1");
        Instant now = Instant.now();
        for (int i = 0; i < 20; i++) {
            t.addMeasurement(now.plusSeconds(i), Quantities.getQuantity(20 + (i % 2), Units.CELSIUS));
        }
        t.addMeasurement(now.plusSeconds(20), Quantities.getQuantity(100, Units.CELSIUS));

        assertEquals(1, net.anomalies(3).size());
    }
}
