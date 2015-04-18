package net.shipilev;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import sun.misc.Contended;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(5)
@State(Scope.Group)
public class RememberedSets {

    private Object src = new Object();
    private long longSrc = 42;

    @Contended
    Object sink1;

    @Contended
    Object sink2;

    @Contended
    long longSink1;

    @Contended
    long longSink2;

    @Benchmark
    @Group("ref")
    public void r1() {
        sink1 = src;
    }

    @Benchmark
    @Group("ref")
    public void r2() {
        sink2 = src;
    }

    @Benchmark
    @Group("long")
    public void l1() {
        longSink1 = longSrc;
    }

    @Benchmark
    @Group("long")
    public void l2() {
        longSink2 = longSrc;
    }

}
