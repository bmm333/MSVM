package it.uniupo.msvm.core.runtime;

import java.util.List;

/**
 * Fotografia immutabile dello stato della VM in un preciso istante.
 * Thread-Safe: I dati contenuti (array e liste) devono essere copie, non riferimenti originali.
 */
public record VmStateSnapshot(
    int ip,
    boolean isHalted,
    List<Integer> stack,
    int[] memory,
    int lastChangedAddress
) {}
