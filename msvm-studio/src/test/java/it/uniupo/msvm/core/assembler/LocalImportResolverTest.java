package it.uniupo.msvm.core.assembler;

import it.uniupo.msvm.core.assembler.impl.LocalImportResolver;
import it.uniupo.msvm.core.exceptions.VmException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocalImportResolverTest {

    @TempDir
    Path tempDir;

    private LocalImportResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new LocalImportResolver(tempDir.toString());
    }

    @Test
    @DisplayName("Should resolve existing file")
    void resolveExistingFile() throws IOException {
        Path libPath = tempDir.resolve("mylib.msvm");
        Files.write(libPath, List.of("PUSH 10", "HALT"));
        List<String> result = resolver.resolve("mylib");
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("PUSH 10", result.get(0));
    }

    @Test
    @DisplayName("Should return null for missing file")
    void resolveMissingFile() {
        List<String> result = resolver.resolve("non_existent");
        assertNull(result);
    }

    @Test
    @DisplayName("Should save to cache correctly")
    void saveToCache() {
        List<String> code = List.of("ADD", "SUB");
        resolver.saveToCache("math/ops", code);
        Path expectedPath = tempDir.resolve("math/ops.msvm");
        assertTrue(Files.exists(expectedPath));
    }

    @Test
    @DisplayName("Should prevent path traversal")
    void securityCheck() {
        assertThrows(SecurityException.class, () -> resolver.resolve("../../etc/passwd"));
        assertThrows(SecurityException.class, () -> resolver.saveToCache("/root/file", List.of()));
    }
}