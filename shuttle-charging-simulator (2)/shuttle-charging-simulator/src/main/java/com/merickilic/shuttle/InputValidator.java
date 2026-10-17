package com.merickilic.shuttle;

import java.util.*;

/**
 * Checks shuttle data for problems before a simulation and reports them
 * as messages. It does not change the data.
 */
public final class InputValidator {

 private InputValidator() {
 }

 // Summary: Checks the shuttle list and collects all problems found
 // Precondition: shuttles != null
 // Postcondition: Returns error messages; an empty list means the data is valid

 public static List<String> validate(List<Shuttle> shuttles) {
     List<String> errors = new ArrayList<>();
     if (shuttles.isEmpty()) {
         errors.add("No shuttles in input");
         return errors;
     }
     Set<Integer> seenIds = new HashSet<>();
     for (int i = 0; i < shuttles.size(); i++) {
         Shuttle s = shuttles.get(i);
         if (!seenIds.add(s.id)) {
             errors.add("Line " + (i + 1) + ": duplicate shuttle id " + s.id);
         }
         if (s.arrival < 0) {
             errors.add("Line " + (i + 1) + ": negative arrival " + s.arrival);
         }
         if (s.service <= 0) {
             errors.add("Line " + (i + 1) + ": service must be positive");
         }
         if (i > 0 && s.arrival < shuttles.get(i - 1).arrival) {
             errors.add("Line " + (i + 1) + ": not sorted by arrival time");
         }
     }
     return errors;
 }

 // Summary: Returns true if validate() finds no errors

 public static boolean isValid(List<Shuttle> shuttles) {
     return validate(shuttles).isEmpty();
 }
}
