package it.uniupo.msvm.core.assembler;
/**Partiale for now missing running registry*/
import  it.uniupo.msvm.core.assembler.impl.RmiImportResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RmiImportResolverTest {

    @Test
    void testConnectionRefusedHandlesGracefully() {
        // Connect to a port that definitely isn't running RMI
        RmiImportResolver resolver = new RmiImportResolver("localhost", 65535, "FakeService");

        // Should return null (and print error to stderr), NOT throw exception
        List<String> result = resolver.resolve("anyLib");

        assertNull(result, "Should return null on connection failure to allow fallback");
    }
}