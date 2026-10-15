# Shuttle Charging Simulator

This project simulates how electric shuttles are charged. Every shuttle has an
id, a priority, an arrival time and a service (charging) time. Waiting shuttles
are ordered by priority (higher first) and then by arrival time (earlier first).
The simulator searches for the smallest number of chargers that keeps the
average waiting time at or below a given limit.

## Classes
- `Shuttle`: data for one shuttle (id, priority, arrival, service)
- `Charger`: one charger; knows when it becomes free and which shuttle it holds
- `ShuttleComparator`: decides charging priority (priority, then arrival)
- `InputParser`: reads shuttle data from a file, a string or any `Reader`
- `Simulator`: runs the event-driven simulation and finds the minimum charger count
- `SimulationResult`: the result returned by the simulator (charger count, average wait)
- `ChargingRecord`: one charging event (shuttle, charger, arrival, start, end)
- `InputValidator`: reports problems in shuttle data (duplicate ids, negative values, unsorted input)

## Input format
```
N
id priority arrival service     (N lines, sorted by arrival)
```
A sample file is in `src/test/resources/shuttles_sample.txt`.

## Build, run and test (Maven, Java 17+)
```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.merickilic.shuttle.Simulator -Dexec.args="input.txt 5"
mvn test        # runs JUnit 5 tests, coverage report: target/site/jacoco/index.html
```
In Eclipse: *File > Import > Maven > Existing Maven Projects*.
In IntelliJ / VS Code: open the folder; the `pom.xml` is detected automatically.

## Design for testability
- Shuttle data is passed to the `Simulator` constructor; file reading lives in `InputParser`.
- Console output goes to an injectable `PrintStream`, so tests can capture it.
- `findMinimumChargers()` returns a `SimulationResult` instead of only printing.
- Simulation steps are package-private, so tests in the same package can call them one at a time.

Tests go in `src/test/java/com/merickilic/shuttle/`.
