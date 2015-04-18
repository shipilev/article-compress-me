package net.shipilev;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class CompressedOops {

    @Param({"1024", "1048576"})
    private int size;

    private Integer[] o;

    @Setup
    public void setup() {
        o = new Integer[size];
        for (int c = 0; c < size; c++) {
            o[c] = new Integer(c);
        }
    }

    @Benchmark
    @Fork(value = 3, jvmArgs = {"-Xmx1g", "-XX:LoopUnrollLimit=1"})
    public void test_01G() {
        makeRun();
    }

    @Benchmark
    @Fork(value = 3, jvmArgs = {"-Xmx4g", "-XX:LoopUnrollLimit=1"})
    public void test_04G() {
        makeRun();
    }

    @Benchmark
    @Fork(value = 3, jvmArgs = {"-Xmx8g", "-XX:LoopUnrollLimit=1"})
    public void test_08G() {
        makeRun();
    }

    @Benchmark
    @Fork(value = 3, jvmArgs = {"-Xmx40g", "-XX:LoopUnrollLimit=1"})
    public void test_40G() {
        makeRun();
    }

    @Benchmark
    @Fork(value = 3, jvmArgs = {"-Xmx40g", "-XX:ObjectAlignmentInBytes=16", "-XX:LoopUnrollLimit=1"})
    public void test_40G_16() {
        makeRun();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int makeRun() {
        int s = 0;
        for (Integer i : o) {
            s += i;
        }
        return s;
    }

}
