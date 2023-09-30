#!/bin/bash

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not found on this system. Please install Java to run this program."
    exit 1
fi

# Compile the Java file
javac capacity.java

# Run the Java program with the appropriate classpath
java -cp .:lib/ojdbc11.jar capacity
