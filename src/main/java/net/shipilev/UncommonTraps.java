package net.shipilev;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
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
public class UncommonTraps {

    @Param({"false", "true"})
    private boolean shake;

    @Param({"1024"})
    private int size;

    private volatile boolean m;

    private Integer[] o;

    @Setup
    public void setup() {
        o = new Integer[size];
        for (int c = 0; c < size; c++) {
            o[c] = new Integer(c);
        }
    }

    @Setup(Level.Iteration)
    public void shakeIt() {
        if (shake) {
            m = true;
            test();

            m = false;
            test();
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Fork(jvmArgsAppend = "-XX:LoopUnrollLimit=1")
    public int test() {
        int s = 0;
        for (Integer i : o) {
            if (m) {
                s ^= (s >> 16); s *= 0x85ebca6b; s ^= (s >> 13);
                s ^= (s >> 16); s *= 0x85ebca6b; s ^= (s >> 13);
            }
            s += i;
        }
        return s;
    }

}
