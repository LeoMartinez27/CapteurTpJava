package fr.univtln.capteurs;

import java.time.Duration;
import java.time.Instant;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import lombok.extern.slf4j.Slf4j;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

@Slf4j
public class App {
    public static void main(String[] args) {
        Instant now = Instant.now();

        // Réseau et capteurs
        SensorNetwork network = SensorNetwork.of("Sensor network 1");
        Sensor<Temperature> sensor1 = network.addTemperatureSensor("T_1");
        Sensor<Pressure> sensor2 = network.addPressureSensor("P_1");

        // Mesures initiales (on peut mélanger °C et K : même grandeur)
        sensor1.addMeasurement(now, Quantities.getQuantity(25, Units.CELSIUS))
               .addMeasurement(now.plusSeconds(15), Quantities.getQuantity(300.15, Units.KELVIN));
        sensor2.addMeasurement(now, Quantities.getQuantity(1013.25, Units.PASCAL));

        // Générateur : +1 °C par seconde, avec bruit et anomalies
        FunctionBasedMeasureGenerator<Temperature> temperatureGenerator =
                FunctionBasedMeasureGenerator.<Temperature>builder()
                        .function(x -> 20 + x)
                        .startTime(now)
                        .endTime(now.plusSeconds(10))
                        .timeStep(Duration.ofSeconds(1))
                        .variance(2)
                        .outlierProbability(0.1)
                        .build();

        temperatureGenerator.generate(sensor1, Units.CELSIUS);

        // Affichage
        log.info("{}", network);
        var sensorIterator = network.sensorIterator();
        while (sensorIterator.hasNext()) {
            Sensor<?> sensor = sensorIterator.next();
            log.info("{}", sensor);

            var measurementIterator = sensor.measurementIterator();
            while (measurementIterator.hasNext()) {
                log.info("  {}", measurementIterator.next());
            }
        }
    }
}
