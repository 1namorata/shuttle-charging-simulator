package com.merickilic.shuttle;

/**
 * Outcome of a simulation: the minimum number of chargers found and the
 * average waiting time with that many chargers.
 */
public class SimulationResult {
 public final int chargerCount;
 public final double averageWaitingTime;

 public SimulationResult(int chargerCount, double averageWaitingTime) {
     this.chargerCount = chargerCount;
     this.averageWaitingTime = averageWaitingTime;
 }

 @Override
 public String toString() {
     return String.format("SimulationResult{chargers=%d, avgWait=%.5f}", chargerCount, averageWaitingTime);
 }
}
