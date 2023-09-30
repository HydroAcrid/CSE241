#!/bin/bash

# Compile the Java file
javac capacity.java

# Run the Java program with the appropriate classpath
java -cp .:lib/ojdbc11.jar capacity
