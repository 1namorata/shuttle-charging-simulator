package com.merickilic.shuttle;

import java.io.*;
import java.util.*;

/**
 * Reads shuttle data in the simulator's input format.
 * Extracted from Simulator.initialize() so that parsing can be tested
 * on its own, without real files (any Reader can be passed in).
 *
 * Input format:
 *   N
 *   id priority arrival service     (N lines)
 */
public final class InputParser {

 private InputParser() {
 }


 // Summary: Reads shuttle data from the given reader
 // Precondition: Reader is not null and data is properly formatted
 // Postcondition: Returns the list of shuttles in file order

 public static List<Shuttle> parse(BufferedReader br) throws IOException {
     List<Shuttle> shuttles = new ArrayList<>();
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
     return shuttles;
 }


 // Summary: Reads shuttle data from text (convenient for tests)
 // Precondition: content is not null
 // Postcondition: Returns the list of shuttles described by content

 public static List<Shuttle> parseString(String content) throws IOException {
     try (BufferedReader br = new BufferedReader(new StringReader(content))) {
         return parse(br);
     }
 }


 // Summary: Reads shuttle data from a file
 // Precondition: File exists and is properly formatted
 // Postcondition: Returns the list of shuttles in file order

 public static List<Shuttle> parseFile(String filename) throws IOException {
     try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
         return parse(br);
     }
 }
}
