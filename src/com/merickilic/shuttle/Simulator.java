package com.merickilic.shuttle;

import java.io.*;
import java.util.*;

public class Simulator {
 private PriorityQueue<Shuttle> waitingQueue;
 private List<Shuttle> shuttles;
 private int maxAvgWaitingTime;
 private int currentTime;
 private Charger[] chargers;
 private int shuttleIndex;


 // Summary: Main method to start the simulation
 // Precondition: Valid filename and max waiting time provided
 // Postcondition: Simulation runs and results are printed

 public static void main(String[] args) {
     if (args.length != 2) {
         System.err.println("Usage: java Simulator <filename> <maxAvgWaitingTime>");
         System.exit(1);
     }

     Simulator simulator = new Simulator();
     simulator.runSimulation(args[0], Integer.parseInt(args[1]));
 }


 // Summary: Initializes simulation data from input file
 // Precondition: File exists and is properly formatted
 // Postcondition: Shuttle data loaded and structures initialized

 private void initialize(String filename) throws IOException {
     shuttles = new ArrayList<>();
     waitingQueue = new PriorityQueue<>(new ShuttleComparator());

     try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
         int N = Integer.parseInt(br.readLine());
         for (int i = 0; i < N; i++) {
             String[] parts = br.readLine().split("\\s+");
             shuttles.add(new Shuttle(
                 Integer.parseInt(parts[0]),
                 Integer.parseInt(parts[1]),
                 Integer.parseInt(parts[2]),
                 Integer.parseInt(parts[3])
             ));
         }
     }
 }

 
 // Summary: Runs complete simulation with increasing chargers
 // Precondition: Data is properly initialized
 // Postcondition: Finds minimal chargers meeting waiting time constraint

 public void runSimulation(String filename, int maxWait) {
     try {
         initialize(filename);
         this.maxAvgWaitingTime = maxWait;
         
         int K = 1; // Start with 1 charger
         while (true) {
             double avgWait = simulateWithKChargers(K);
             if (avgWait <= maxAvgWaitingTime) {
                 printResults(K, avgWait);
                 break;
             }
             K++;
         }
     } catch (IOException e) {
         System.err.println("Error reading input file: " + e.getMessage());
     }
 }

 
 // Summary: Simulates one complete run with K chargers
 // Precondition: K >= 1, data is initialized
 // Postcondition: Returns average waiting time for this configuration
 
 private double simulateWithKChargers(int K) {
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

 private void initializeSimulation(int K) {
     chargers = new Charger[K];
     for (int i = 0; i < K; i++) {
         chargers[i] = new Charger(i);
     }
     
     currentTime = 0;
     shuttleIndex = 0;
     waitingQueue.clear();
 }


 // Summary: Adds arriving shuttles to waiting queue
 // Precondition: Simulation is running
 // Postcondition: All arrived shuttles are in waiting queue

 private void addArrivingShuttles() {
     while (shuttleIndex < shuttles.size() && 
            shuttles.get(shuttleIndex).arrival <= currentTime) {
         waitingQueue.add(shuttles.get(shuttleIndex++));
     }
 }


 // Summary: Assigns shuttles to available chargers
 // Precondition: Simulation is running
 // Postcondition: Eligible shuttles assigned to chargers

 private void assignShuttlesToChargers() {
     for (Charger charger : chargers) {
         if (charger.isAvailable(currentTime) && !waitingQueue.isEmpty()) {
             Shuttle shuttle = waitingQueue.poll();
             charger.assignShuttle(shuttle, currentTime);
             System.out.printf("Charger %d takes shuttle %d at minute %d (wait: %d mins)%n",
                 charger.id, shuttle.id, currentTime, currentTime - shuttle.arrival);
         }
     }
 }

 
 // Summary: Calculates next important simulation time
 // Precondition: Simulation is running
 // Postcondition: Returns time of next charger availability or shuttle arrival

 private int getNextEventTime() {
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

 private double calculateAverageWaitingTime() {
     double totalWaitingTime = 0;
     for (Shuttle s : shuttles) {
         totalWaitingTime += (getDepartureTime(s) - s.arrival - s.service);
     }
     return totalWaitingTime / shuttles.size();
 }


 // Summary: Finds departure time for a shuttle
 // Precondition: Simulation completed
 // Postcondition: Returns departure time of given shuttle
 
 private int getDepartureTime(Shuttle shuttle) {
     for (Charger c : chargers) {
         if (c.currentShuttle != null && c.currentShuttle.id == shuttle.id) {
             return c.availableTime;
         }
     }
     return 0;
 }


 // Summary: Prints final results
 // Precondition: Simulation completed successfully
 // Postcondition: Results printed to standard output

 private void printResults(int K, double avgWait) {
     System.out.println("Minimum number of chargers required: " + K);
     System.out.printf("Average waiting time: %.5f minutes%n", avgWait);
 }
}