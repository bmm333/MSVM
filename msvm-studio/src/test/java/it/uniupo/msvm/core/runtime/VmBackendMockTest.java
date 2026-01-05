package it.uniupo.msvm.core.runtime;

import it.uniupo.msvm.core.exceptions.VmException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests VmRunner in isolation using a fake VmBackend.
 */
class VmBackendMockTest {

    static class MockBackend implements VmBackend {
        int ip = 0;
        boolean halted = false;
        AtomicInteger stepCount = new AtomicInteger(0);
        int[] memory = new int[10];
        List<Integer> stack = new ArrayList<>();

        @Override
        public void step() throws VmException {
            stepCount.incrementAndGet();
            ip++;
            if (ip >= 5) {
                halted = true;
            }
        }

        @Override
        public boolean isHalted() {
            return halted;
        }

        @Override
        public int getIp() {
            return ip;
        }

        @Override
        public List<Integer> getStackSnapshot() {
            return new ArrayList<>(stack);
        }

        @Override
        public int[] getMemorySnapshot() {
            return memory.clone();
        }

        @Override
        public int getLastWrittenAddress() {
            return -1;
        }

        @Override
        public void reset() {
            ip = 0;
            halted = false;
            stepCount.set(0);
        }
    }

    @Test
    void testRunnerUsesBackendCorrectly() throws InterruptedException {
        MockBackend mock = new MockBackend();
        VmRunner runner = new VmRunner(mock);
        runner.setFrequency(100); // Fast execution
        runner.start();
        int attempts = 0;
        while (runner.isRunning() && attempts < 10) {
            Thread.sleep(50);
            attempts++;
        }
        assertTrue(mock.stepCount.get() >= 5, "Should have executed at least 5 steps");
        assertTrue(mock.isHalted(), "Backend should be halted");
        assertFalse(runner.isRunning(), "Runner should stop when backend halts");
    }
    @Test
    void testManualStep() {
        MockBackend mock = new MockBackend();
        VmRunner runner = new VmRunner(mock);
        runner.step();
        assertEquals(1, mock.stepCount.get());
        assertEquals(1, mock.getIp());
    }
}