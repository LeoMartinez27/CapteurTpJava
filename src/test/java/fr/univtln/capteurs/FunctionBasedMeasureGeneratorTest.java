package fr.univtln.capteurs;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import javax.measure.quantity.Temperature;
import org.junit.jupiter.api.Test;
import tech.units.indriya.unit.Units;

class FunctionBasedMeasureGeneratorTest {

    private final Instant start = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void generationSansBruitSuitLaFonction() {
        Sensor<Temperature> sensor = new Sensor<>("T_1");

        FunctionBasedMeasureGenerator.<Temperature>builder()
                .function(x -> 20 + x)
                .startTime(start).endTime(start.plusSeconds(10))
                .timeStep(Duration.ofSeconds(1))
                .variance(0).outlierProbability(0)
                .build()
                .generate(sensor, Units.CELSIUS);

        Iterator<Measurement<Temperature>> it = sensor.measurementIterator();
        int count = 0;
        while (it.hasNext()) {
            double v = it.next().value().getValue().doubleValue();
            assertEquals(20 + count, v, 1e-9);
            count++;
        }
        assertEquals(11, count);   // de 0 à 10 s inclus
    }

    @Test
    void memeGraineMemesResultats() {
        Sensor<Temperature> a = new Sensor<>("A");
        Sensor<Temperature> b = new Sensor<>("B");

        for (Sensor<Temperature> s : java.util.List.of(a, b)) {
            FunctionBasedMeasureGenerator.<Temperature>builder()
                    .function(x -> 20)
                    .startTime(start).endTime(start.plusSeconds(5))
                    .variance(2).outlierProbability(0.2).seed(42)
                    .build()
                    .generate(s, Units.CELSIUS);
        }

        Iterator<Measurement<Temperature>> ia = a.measurementIterator();
        Iterator<Measurement<Temperature>> ib = b.measurementIterator();
        while (ia.hasNext()) {
            assertEquals(ia.next().value().getValue().doubleValue(),
                         ib.next().value().getValue().doubleValue(), 1e-12);
        }
    }

    @Test
    void parametresInvalides() {
        var builder = FunctionBasedMeasureGenerator.<Temperature>builder()
                .function(x -> 0).startTime(start).endTime(start.plusSeconds(1));

        assertThrows(IllegalArgumentException.class, () -> builder.timeStep(Duration.ZERO).build());
        assertThrows(IllegalArgumentException.class,
                () -> builder.timeStep(Duration.ofSeconds(1)).outlierProbability(1.5).build());
    }
}
