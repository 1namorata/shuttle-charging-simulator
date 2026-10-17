package com.merickilic.shuttle;

import java.util.Comparator;

public class ShuttleComparator implements Comparator<Shuttle> {

 // Summary: Compares two shuttles for priority queue ordering
 // Precondition: Both shuttles are non-null
 // Postcondition: Returns comparison result (-1, 0, 1)
 
 @Override
 public int compare(Shuttle s1, Shuttle s2) {
     // First by priority (higher first)
     if (s1.priority != s2.priority) {
         return Integer.compare(s2.priority, s1.priority);
     }
     // Then by arrival time (earlier first)
     return Integer.compare(s1.arrival, s2.arrival);
 }
}