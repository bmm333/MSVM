package it.uniupo.msvm.core.assembler.impl;

import it.uniupo.msvm.core.assembler.CachingResolver;
import it.uniupo.msvm.core.assembler.ImportResolver;
import it.uniupo.msvm.core.exceptions.VmException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LocalImportResolver  implements CachingResolver {

    private final Path rootPath;

    public LocalImportResolver(String rootPath) {
        this.rootPath=Paths.get(rootPath);
        ensureRootExists();
    }
    private void ensureRootExists() {
        try{
            if(!Files.exists(rootPath)) Files.createDirectories(rootPath);
        }catch (IOException e){
            throw new VmException("Cannot create root directory: "+rootPath);
        }
    }
    @Override
    public List<String> resolve(String libName)
    {
        validatePath(libName);
        Path file=rootPath.resolve(libName + ".msvm");
        if(Files.exists(file))
        {
            try {
                return Files.readAllLines(file);
            }catch (IOException e){
                System.err.println("[LOCAL] Error reading file: " + file);
            }
        }
        return  null; //non trovato; permette il fallback
    }
    /**
     * Salva il codice scaricato nel filesystem locale.
     */
    @Override
    public void saveToCache(String libName, List<String> code) {
        validatePath(libName);
        try {
            Path file = rootPath.resolve(libName + ".msvm");
            if (file.getParent() != null) Files.createDirectories(file.getParent());
            Files.write(file, code);
            System.out.println("[Local] Cachig " + libName);
        } catch (IOException e) {
            System.err.println("[Local] ERRORE Caching: " + e.getMessage());
        }
    }
    private void validatePath(String path) {
        if (path.contains("..") || path.startsWith("/") || path.contains("\\")) {
            throw new SecurityException("Path Traversal Attempt Detected: " + path);
        }
    }
}
