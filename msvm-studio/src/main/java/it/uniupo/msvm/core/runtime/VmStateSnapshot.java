package it.uniupo.msvm.core.runtime;

import java.util.List;

public record VmStateSnapshot(
    int ip,
    boolean isHalted,
    List<Integer> stack,
    int[] memory,
    int lastChangedAddress
) {
    //record immutabili scelta per il multithreading
}
