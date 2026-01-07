package it.uniupo.msvm.core.assembler;

import java.util.List;

/**
 * Interfaccia per un Resolver che supporta anche la scrittura (Caching).
 * Segue il principio di Interface Segregation (ISP): separiamo la capacità di leggere da quella di scrivere,
 * ma qui le uniamo perché il LocalResolver fa entrambe.
 */
public interface CachingResolver extends ImportResolver{
    void saveToCache(String libName, List<String> code);
}
