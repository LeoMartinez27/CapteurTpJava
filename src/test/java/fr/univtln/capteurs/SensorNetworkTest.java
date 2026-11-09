package fr.univtln.capteurs;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import org.junit.jupiter.api.Test;

class SensorNetworkTest {

    @Test
    void ajoutDeCapteursDeTypesDifferents() {
        SensorNetwork net = SensorNetwork.of("Réseau 1");
        Sensor<Temperature> t = net.addTemperatureSensor("T_1");
        Sensor<Pressure> p = net.addPressureSensor("P_1");

        assertEquals("T_1", t.getName());
        assertEquals("P_1", p.getName());

        Iterator<Sensor<?>> it = net.sensorIterator();
        assertEquals("T_1", it.next().getName());   // ordre d'insertion
        assertEquals("P_1", it.next().getName());
        assertFalse(it.hasNext());
    }

    @Test
    void identifiantDuplique() {
        SensorNetwork net = SensorNetwork.of("Réseau 1");
        net.addTemperatureSensor("T_1");
        assertThrows(IllegalArgumentException.class, () -> net.addPressureSensor("T_1"));
    }
}
