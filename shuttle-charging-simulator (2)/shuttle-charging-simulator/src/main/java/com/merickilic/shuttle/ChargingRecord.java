package com.merickilic.shuttle;

/**
 * One charging event: which shuttle was charged on which charger, and when.
 * The Simulator creates one record every time it assigns a shuttle.
 */
public class ChargingRecord {
 public final int shuttleId;
 public final int chargerId;
 public final int arrivalTime;
 public final int startTime;
 public final int endTime;

 // Summary: Constructs a charging record
 // Precondition: arrivalTime <= startTime <= endTime

 public ChargingRecord(int shuttleId, int chargerId, int arrivalTime, int startTime, int endTime) {
     this.shuttleId = shuttleId;
     this.chargerId = chargerId;
     this.arrivalTime = arrivalTime;
     this.startTime = startTime;
     this.endTime = endTime;
 }

 // Summary: Returns how long the shuttle waited before charging started

 public int getWaitingTime() {
     return startTime - arrivalTime;
 }

 // Summary: Returns how long the charging took

 public int getChargingDuration() {
     return endTime - startTime;
 }

 @Override
 public String toString() {
     return String.format("ChargingRecord{shuttle=%d, charger=%d, start=%d, end=%d}",
         shuttleId, chargerId, startTime, endTime);
 }
}
