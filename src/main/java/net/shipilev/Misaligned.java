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
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Fork(5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class Misaligned {

        /*
        "Performance Horror Stories" issue #(N+1): "Misaligned accesses"
          aleksey.shipilev@oracle.com, @shipilev

        It is a "common wisdom" (tm) that misaligned accesses have the associated performance
        penalties. Let's try to quantify these costs. The benchmark code is below, and the
        explanation for the benchmark choices is inlined there.

        The results and discussion follow the benchmark code.
    */

    private static final Unsafe U;

    private long aligned;
    private long misaligned;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            U = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Total chunk size we are walking.
     */
    @Param({"4096", "65536", "1048576", "16777216"})
    private int size;

    /**
     * Should the test cross the cache line or not?
     */
    @Param({"false", "true"})
    private boolean crossCL;

    private int sizeMask;

    /**
     * Some large power of 2. Any cache line size that's power of 2 and less
     * than CACHE_LINE_MAX would be covered by the code.
     */
    private static final int CACHE_LINE_MAX = 256;

    /**
     * Walking stride for inlined PRNG. We walk the chunk randomly to avoid
     * streaming reads.
     */
    private static final int OFFSET_ADD = CACHE_LINE_MAX * 1337;

    @Setup
    public void init() {
        /*
         * Whatever the actual cache line size is, $aligned always starts at a cache line
         * boundary. Depending on $crossCL setting, we either step into the cache line,
         * thus making sure $misaligned is always within the cache line, or step out the
         * cache line for $misaligned.
         */

        long addr = U.allocateMemory(size + CACHE_LINE_MAX);
        aligned = (addr & ~(CACHE_LINE_MAX - 1)) + CACHE_LINE_MAX;
        misaligned = aligned + CACHE_LINE_MAX;

        if (crossCL) {
            misaligned -= 1;
        } else {
            misaligned += 1;
        }

        sizeMask = (size - 1);

        if (aligned % CACHE_LINE_MAX != 0) {
            throw new IllegalStateException("Base address is not aligned");
        }

        if ((size & (size - 1)) != 0) {
            throw new IllegalStateException("Size is not a power of two:" + size);
        }

        int off = 0;
        for (int c = 0; c < size; c++) {
            off = (off + OFFSET_ADD)  & sizeMask;
            if ((aligned + off) % 4 != 0) throw new IllegalStateException("Aligned address is not really aligned");
            if ((misaligned + off) % 4 == 0) throw new IllegalStateException("Misaligned address is really aligned");
        }
    }

    @Benchmark
    public void read_aligned() {
        int off = 0;
        int lSize = size;
        int lSizeMask = sizeMask;
        long base = aligned;
        for (int c = 0; c < lSize; c++) {
            off = (off + OFFSET_ADD) & lSizeMask;
            doReadWith(base + off);
        }
    }

    @Benchmark
    public void read_misaligned() {
        int off = 0;
        int lSize = size;
        int lSizeMask = sizeMask;
        long base = misaligned;
        for (int c = 0; c < lSize; c++) {
            off = (off + OFFSET_ADD) & lSizeMask;
            doReadWith(base + off);
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int doReadWith(long addr) {
        return U.getInt(addr);
    }

    @Benchmark
    public void cas_aligned() {
        int off = 0;
        int lSize = size;
        int lSizeMask = sizeMask;
        long base = aligned;
        for (int c = 0; c < lSize; c++) {
            off = (off + OFFSET_ADD) & lSizeMask;
            doCasWith(base + off);
        }
    }

    @Benchmark
    public void cas_misaligned() {
        int off = 0;
        int lSize = size;
        int lSizeMask = sizeMask;
        long base = misaligned;
        for (int c = 0; c < lSize; c++) {
            off = (off + OFFSET_ADD) & lSizeMask;
            doCasWith(base + off);
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int doCasWith(long addr) {
        return U.getAndAddInt(null, addr, 1);
    }


}
