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

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class StringCompressEquals {

    @Param({"1", "4096"})
    int size;

    private String cmp1_1;
    private String cmp1_2;
    private String cmp2_1;
    private String cmp2_2;

    @Setup
    public void setup() {
        long seed = Long.getLong("seed", 1234567890L);

        char[] cs = generateBase(seed);

        String base = new String(cs);
        cmp1_1 = new String(base + (char)0x0042);
        cmp1_2 = new String(base + (char)0x0043);
        cmp2_1 = new String(base + (char)0x4242);
        cmp2_2 = new String(base + (char)0x4243);
    }

    private char[] generateBase(long seed) {
        Random r = new Random(seed);
        char[] cs = new char[size - 1];
        int bound = (int) ((size - 1) * 1.0);
        for (int c = 0; c < bound; c++) {
            cs[c] = (char) (r.nextInt(Byte.MAX_VALUE) & 0x00FF);
        }
        for (int c = bound; c < size - 1; c++) {
            char bs;
            do {
                bs = (char) r.nextInt(Short.MAX_VALUE);
            } while ((bs & 0xFF00) == 0);
            cs[c] = bs;
        }
        return cs;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public boolean cmp1_cmp1() {
        return cmp1_1.equals(cmp1_2);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public boolean cmp1_cmp2() {
        return cmp1_1.equals(cmp2_2);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public boolean cmp2_cmp1() {
        return cmp2_1.equals(cmp1_2);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public boolean cmp2_cmp2() {
        return cmp2_1.equals(cmp2_2);
    }

}