package net.shipilev;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
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
public class FieldStoreCoalesce {

    private byte x = 42;

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Object test_default() {
        return new MyObject();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Object test_argument() {
        return new MyObject(x);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Object test_argument_peel() {
        if (x == 42) {
            return new MyObject();
        } else {
            return new MyObject(x);
        }
    }

    static class MyObject {
        private final byte x1;
        private final byte x2;
        private final byte x3;
        private final byte x4;

        public MyObject() {
            x1 = x2 = x3 = x4 = 42;
        }

        public MyObject(byte x) {
            x1 = x2 = x3 = x4 = x;
        }

    }

}
