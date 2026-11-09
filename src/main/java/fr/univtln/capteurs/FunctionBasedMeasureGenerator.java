package fr.univtln.capteurs;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Random;
import javax.measure.Quantity;
import javax.measure.Unit;
import tech.units.indriya.quantity.Quantities;

public class FunctionBasedMeasureGenerator<Q extends Quantity<Q>> {
    private static final double OUTLIER_FACTOR = 10; // anomalie = 10 écarts-types

    private final Function function;
    private final Instant startTime;
    private final Instant endTime;
    private final Duration timeStep;
    private final double variance;
    private final double outlierProbability;
    private final Random random;

    private FunctionBasedMeasureGenerator(Builder<Q> b) {
        this.function = b.function;
        this.startTime = b.startTime;
        this.endTime = b.endTime;
        this.timeStep = b.timeStep;
        this.variance = b.variance;
        this.outlierProbability = b.outlierProbability;
        this.random = b.seed == null ? new Random() : new Random(b.seed);
    }

    public static <Q extends Quantity<Q>> Builder<Q> builder() {
        return new Builder<>();
    }

    public void generate(Sensor<Q> sensor, Unit<Q> unit) {
        double sigma = Math.sqrt(variance);
        for (Instant t = startTime; !t.isAfter(endTime); t = t.plus(timeStep)) {
            long x = Duration.between(startTime, t).getSeconds();
            double value = function.apply(x) + random.nextGaussian() * sigma;
            if (random.nextDouble() < outlierProbability) {
                value += (random.nextBoolean() ? 1 : -1) * OUTLIER_FACTOR * sigma;
            }
            sensor.addMeasurement(t, Quantities.getQuantity(value, unit));
        }
    }

    public static class Builder<Q extends Quantity<Q>> {
        private Function function;
        private Instant startTime;
        private Instant endTime;
        private Duration timeStep = Duration.ofSeconds(1);
        private double variance = 0;
        private double outlierProbability = 0;
        private Long seed;

        public Builder<Q> function(Function function) { this.function = function; return this; }
        public Builder<Q> startTime(Instant startTime) { this.startTime = startTime; return this; }
        public Builder<Q> endTime(Instant endTime) { this.endTime = endTime; return this; }
        public Builder<Q> timeStep(Duration timeStep) { this.timeStep = timeStep; return this; }
        public Builder<Q> variance(double variance) { this.variance = variance; return this; }
        public Builder<Q> outlierProbability(double p) { this.outlierProbability = p; return this; }
        public Builder<Q> seed(long seed) { this.seed = seed; return this; }

        public FunctionBasedMeasureGenerator<Q> build() {
            Objects.requireNonNull(function, "function obligatoire");
            Objects.requireNonNull(startTime, "startTime obligatoire");
            Objects.requireNonNull(endTime, "endTime obligatoire");
            Objects.requireNonNull(timeStep, "timeStep obligatoire");
            if (timeStep.isZero() || timeStep.isNegative())
                throw new IllegalArgumentException("timeStep doit être > 0");
            if (endTime.isBefore(startTime))
                throw new IllegalArgumentException("endTime avant startTime");
            if (variance < 0)
                throw new IllegalArgumentException("variance doit être >= 0");
            if (outlierProbability < 0 || outlierProbability > 1)
                throw new IllegalArgumentException("outlierProbability doit être dans [0, 1]");
            return new FunctionBasedMeasureGenerator<>(this);
        }
    }
}
