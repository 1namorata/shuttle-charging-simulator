package com.merickilic.shuttle;

public class Shuttle {
 public final int id;
 public final int priority;
 public final int arrival;
 public final int service;

 // Summary: Constructs a Shuttle object with given parameters
 
 public Shuttle(int id, int priority, int arrival, int service) {
     this.id = id;
     this.priority = priority;
     this.arrival = arrival;
     this.service = service;
 }
}