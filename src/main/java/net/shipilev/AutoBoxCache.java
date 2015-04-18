package net.shipilev;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class AutoBoxCache {

    private int x = 200;

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Fork(jvmArgs = {"-Xmx4m", "-Xms4m", "-Djava.lang.Integer.IntegerCache.high=128"})
    public Integer heap4M_cache128() {
        return x;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Fork(jvmArgs = {"-Xmx4m", "-Xms4m", "-Djava.lang.Integer.IntegerCache.high=256"})
    public Integer heap4M_cache256() {
        return x;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Fork(jvmArgs = {"-Xmx128m", "-Xms128m", "-Djava.lang.Integer.IntegerCache.high=128"})
    public Integer heap128M_cache128() {
        return x;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Fork(jvmArgs = {"-Xmx128m", "-Xms128m", "-Djava.lang.Integer.IntegerCache.high=256"})
    public Integer heap128M_cache256() {
        return x;
    }

}
