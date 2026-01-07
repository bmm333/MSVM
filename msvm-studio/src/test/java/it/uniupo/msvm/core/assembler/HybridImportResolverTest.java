package it.uniupo.msvm.core.assembler;

import it.uniupo.msvm.core.assembler.impl.HybridImportResolver;
import it.uniupo.msvm.core.exceptions.VmException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HybridImportResolverTest {

    // Mockiamo l'interfaccia, NON la classe concreta!
    private CachingResolver mockLocal;
    private ImportResolver mockRemote;
    private HybridImportResolver hybrid;

    @BeforeEach
    void setUp() {
        mockLocal = mock(CachingResolver.class);
        mockRemote = mock(ImportResolver.class);
        hybrid = new HybridImportResolver(mockLocal, mockRemote);
    }

    @Test
    @DisplayName("Fast Path: Should return cached result if available")
    void testCacheHit() {
        String lib = "mylib";
        List<String> cachedCode = List.of("PUSH 1");

        // Mocking pulito su interfaccia
        when(mockLocal.resolve(lib)).thenReturn(cachedCode);

        List<String> result = hybrid.resolve(lib);

        assertEquals(cachedCode, result);
        verify(mockRemote, never()).resolve(anyString());
    }

    @Test
    @DisplayName("Slow Path: Fetch remote and save to cache")
    void testCacheMissAndFetch() {
        String lib = "remoteLib";
        List<String> remoteCode = List.of("POP");

        when(mockLocal.resolve(lib)).thenReturn(null);
        when(mockRemote.resolve(lib)).thenReturn(remoteCode);

        List<String> result = hybrid.resolve(lib);

        assertEquals(remoteCode, result);
        // Verifica che venga chiamato il metodo sull'interfaccia
        verify(mockLocal).saveToCache(lib, remoteCode);
    }

    @Test
    void testFailure() {
        when(mockLocal.resolve("ghost")).thenReturn(null);
        when(mockRemote.resolve("ghost")).thenReturn(null);
        assertThrows(VmException.class, () -> hybrid.resolve("ghost"));
    }
}