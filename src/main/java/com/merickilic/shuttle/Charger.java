package com.merickilic.shuttle;

public class Charger {
 public final int id;
 public int availableTime;
 public Shuttle currentShuttle;


 // Summary: Constructs a Charger with given ID
 // Precondition: id must be non-negative
 // Postcondition: A new Charger object is created
 
 public Charger(int id) {
     this.id = id;
     this.availableTime = 0;
     this.currentShuttle = null;
 }


 // Summary: Checks if charger is available at given time
 // Precondition: time must be non-negative
 // Postcondition: Returns true if charger is available

 public boolean isAvailable(int time) {
     return availableTime <= time;
 }


 // Summary: Assigns a shuttle to this charger
 // Precondition: shuttle != null, time >= shuttle.arrival
 // Postcondition: Charger is occupied until service completes

 public void assignShuttle(Shuttle shuttle, int time) {
     this.currentShuttle = shuttle;
     this.availableTime = time + shuttle.service;
 }
}