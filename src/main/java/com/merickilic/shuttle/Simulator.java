package com.merickilic.shuttle;

import java.io.*;
import java.util.*;

/**
 * Finds the minimum number of chargers that keeps the average waiting time
 * at or below a limit.
 *
 * Testability changes (behaviour is unchanged):
 *  - Shuttle data is passed to the constructor instead of being read from a
 *    file inside the class (file reading moved to InputParser).
 *  - Output goes to an injectable PrintStream instead of System.out.
 *  - findMinimumChargers() returns a SimulationResult instead of only printing.
 *  - Helper steps are package-private (not private) so tests in the same
 *    package can call them one by one.
 */
public class Simulator {
 private final List<Shuttle> shuttles;
 private final PrintStream out;
 private PriorityQueue<Shuttle> waitingQueue;
 private int currentTime;
 private Charger[] chargers;
 private int shuttleIndex;
 private final List<ChargingRecord> records = new ArrayList<>();


 // Summary: Constructs a Simulator that prints to standard output
 // Precondition: shuttles != null
 // Postcondition: Simulator ready to run on the given shuttles

 public Simulator(List<Shuttle> shuttles) {
     this(shuttles, System.out);
 }


 // Summary: Constructs a Simulator that prints to the given stream
 // Precondition: shuttles != null, out != null
 // Postcondition: Simulator ready to run on the given shuttles

 public Simulator(List<Shuttle> shuttles, PrintStream out) {
     this.shuttles = new ArrayList<>(shuttles);
     this.out = out;
     this.waitingQueue = new PriorityQueue<>(new ShuttleComparator());
 }


 // Summary: Main method to start the simulation
 // Precondition: Valid filename and max waiting time provided
 // Postcondition: Simulation runs and results are printed

 public static void main(String[] args) {
     if (args.length != 2) {
         System.err.println("Usage: java Simulator <filename> <maxAvgWaitingTime>");
         System.exit(1);
     }

     runSimulation(args[0], Integer.parseInt(args[1]));
 }


 // Summary: Reads the file and runs the complete simulation
 // Precondition: File exists and is properly formatted
 // Postcondition: Results printed; returns them, or null if the file cannot be read

 public static SimulationResult runSimulation(String filename, int maxWait) {
     try {
         List<Shuttle> shuttles = InputParser.parseFile(filename);
         return new Simulator(shuttles).findMinimumChargers(maxWait);
     } catch (IOException e) {
         System.err.println("Error reading input file: " + e.getMessage());
         return null;
     }
 }


 // Summary: Runs complete simulation with increasing chargers
 // Precondition: Data is properly initialized
 // Postcondition: Finds minimal chargers meeting waiting time constraint

 public SimulationResult findMinimumChargers(int maxAvgWaitingTime) {
     int K = 1; // Start with 1 charger
     while (true) {
         double avgWait = simulateWithKChargers(K);
         if (avgWait <= maxAvgWaitingTime) {
             printResults(K, avgWait);
             return new SimulationResult(K, avgWait);
         }
         K++;
     }
 }


 // Summary: Simulates one complete run with K chargers
 // Precondition: K >= 1, data is initialized
 // Postcondition: Returns average waiting time for this configuration

 public double simulateWithKChargers(int K) {
     initializeSimulation(K);

     while (shuttleIndex < shuttles.size() || !waitingQueue.isEmpty()) {
         addArrivingShuttles();
         assignShuttlesToChargers();
         currentTime = getNextEventTime();
     }

     return calculateAverageWaitingTime();
 }


 // Summary: Initializes simulation variables for a new run
 // Precondition: K >= 1
 // Postcondition: Simulation ready to run with K chargers

 void initializeSimulation(int K) {
     chargers = new Charger[K];
     for (int i = 0; i < K; i++) {
         chargers[i] = new Charger(i);
     }

     currentTime = 0;
     shuttleIndex = 0;
     waitingQueue.clear();
     records.clear();
 }


 // Summary: Adds arriving shuttles to waiting queue
 // Precondition: Simulation is running
 // Postcondition: All arrived shuttles are in waiting queue

 void addArrivingShuttles() {
     while (shuttleIndex < shuttles.size() &&
            shuttles.get(shuttleIndex).arrival <= currentTime) {
         waitingQueue.add(shuttles.get(shuttleIndex++));
     }
 }


 // Summary: Assigns shuttles to available chargers
 // Precondition: Simulation is running
 // Postcondition: Eligible shuttles assigned to chargers

 void assignShuttlesToChargers() {
     for (Charger charger : chargers) {
         if (charger.isAvailable(currentTime) && !waitingQueue.isEmpty()) {
             Shuttle shuttle = waitingQueue.poll();
             charger.assignShuttle(shuttle, currentTime);
             records.add(new ChargingRecord(shuttle.id, charger.id, shuttle.arrival,
                 currentTime, charger.availableTime));
             out.printf("Charger %d takes shuttle %d at minute %d (wait: %d mins)%n",
                 charger.id, shuttle.id, currentTime, currentTime - shuttle.arrival);
         }
     }
 }


 // Summary: Calculates next important simulation time
 // Precondition: Simulation is running
 // Postcondition: Returns time of next charger availability or shuttle arrival

 int getNextEventTime() {
     int nextTime = Integer.MAX_VALUE;

     for (Charger c : chargers) {
         if (!c.isAvailable(currentTime)) {
             nextTime = Math.min(nextTime, c.availableTime);
         }
     }

     if (shuttleIndex < shuttles.size()) {
         nextTime = Math.min(nextTime, shuttles.get(shuttleIndex).arrival);
     }

     return nextTime == Integer.MAX_VALUE ? currentTime + 1 : nextTime;
 }


 // Summary: Calculates average waiting time
 // Precondition: Simulation completed
 // Postcondition: Returns average waiting time

 double calculateAverageWaitingTime() {
     double totalWaitingTime = 0;
     for (Shuttle s : shuttles) {
         totalWaitingTime += (getDepartureTime(s) - s.arrival - s.service);
     }
     return totalWaitingTime / shuttles.size();
 }


 // Summary: Finds departure time for a shuttle
 // Precondition: Simulation completed
 // Postcondition: Returns departure time of given shuttle

 int getDepartureTime(Shuttle shuttle) {
     for (Charger c : chargers) {
         if (c.currentShuttle != null && c.currentShuttle.id == shuttle.id) {
             return c.availableTime;
         }
     }
     return 0;
 }


 // Summary: Prints final results
 // Precondition: Simulation completed successfully
 // Postcondition: Results printed to the output stream

 void printResults(int K, double avgWait) {
     out.println("Minimum number of chargers required: " + K);
     out.printf("Average waiting time: %.5f minutes%n", avgWait);
 }


 // Summary: Returns the charging events of the last simulated run
 // Precondition: simulateWithKChargers() has been called
 // Postcondition: Returns a read-only list in assignment order

 public List<ChargingRecord> getChargingRecords() {
     return Collections.unmodifiableList(records);
 }


 // ---- Accessors for tests (package-private) ----

 int getCurrentTime() {
     return currentTime;
 }

 void setCurrentTime(int time) {
     this.currentTime = time;
 }

 Charger[] getChargers() {
     return chargers;
 }

 int getWaitingQueueSize() {
     return waitingQueue.size();
 }
}
