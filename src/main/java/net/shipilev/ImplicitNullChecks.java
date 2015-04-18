package net.shipilev;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
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
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(jvmArgsAppend = "-XX:LoopUnrollLimit=1")
@OperationsPerInvocation(10000)
public class ImplicitNullChecks {

    public static final NullPointerException NULL_POINTER_EXCEPTION = new NullPointerException();

    @Param("10000")
    private int count;

    @Param({"false", "true"})
    private boolean poison;

    private MyObject[] objs;

    @Setup
    public void setup() {
        objs = new MyObject[count];

        for (int c = 0; c < count; c++) {
            objs[c] = new MyObject();
        }

        if (poison) {
            // POISON ALL THE THINGS!
            for (int c = 0; c < count; c++) {
                objs[c] = null;
            }

            for (int c = 0; c < 10000; c++) {
                try {
                    test();
                } catch (NullPointerException e) {
                    // expected
                }
            }
        }

        for (int c = 0; c < count; c++) {
            objs[c] = new MyObject();
        }
    }

    @Benchmark
    public long test() {
        long s = 0;
        for (MyObject o : objs) {
            if (o == null) {
                throw NULL_POINTER_EXCEPTION;
            }
            s += o.x;
        }
        return s;
    }

    private static class MyObject {
        public int x;
    }

}
