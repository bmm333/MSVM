package it.uniupo.msvm.core.assembler.impl;

import it.uniupo.msvm.core.assembler.CachingResolver;
import it.uniupo.msvm.core.assembler.ImportResolver;
import it.uniupo.msvm.core.exceptions.VmException;

import java.util.List;

/**
 * Orchestratore della risoluzione delle dipendenze
 * Implementa il paattern Cache-Aside
 * <ul>
 *     <li>Controlla la cache locale (Hit veloce)</li>
 *     <li>Se non lo trova,scarica da remoto e aggiorna la cache</li>
 * </ul>
 * */
public class HybridImportResolver  implements ImportResolver {

    private final CachingResolver localLayer;
    private final ImportResolver remoteLayer;

    public HybridImportResolver(CachingResolver localLayer,ImportResolver remoteLayer) {
        this.localLayer=localLayer;
        this.remoteLayer=remoteLayer;
    }
    @Override
    public List<String> resolve(String libName)
    {
        //Fast path
        List<String> cachedCode=localLayer.resolve(libName);
        if(cachedCode!=null)
        {
            return cachedCode;
        }
        //Slow path : Fetch remoto
        System.out.println("[HybridResolver] Cache MISS for '" + libName + "'. Fetching server...");
        List<String> remoteCode = remoteLayer.resolve(libName);
        if (remoteCode != null && !remoteCode.isEmpty()) {
            localLayer.saveToCache(libName, remoteCode);
            return remoteCode;
        }
        throw new VmException("Dependency Resolution Failed: Library '" + libName + "' not found either locally or remotely.");
    }
}
