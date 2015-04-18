package net.shipilev;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class AutoBoxEscape {

    private Set<Integer> contains;
    private Set<Integer> notContains;

    @Param({"42", "4242"})
    int value;

    @Setup
    public void setup() {
        contains = new HashSet<Integer>();
        notContains = new HashSet<Integer>();

        contains.add(value);
    }

    @Benchmark
    public boolean contains_valueOf() {
        return contains.contains(Integer.valueOf(value));
    }

    @Benchmark
    public boolean notContains_valueOf() {
        return notContains.contains(Integer.valueOf(value));
    }

    @Benchmark
    public boolean contains_new() {
        return contains.contains(new Integer(value));
    }

    @Benchmark
    public boolean notContains_new() {
        return notContains.contains(new Integer(value));
    }

}
