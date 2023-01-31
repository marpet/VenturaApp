#!/bin/bash

# Set the path to the Java executable
JAVA_EXE="/usr/bin/java"

# Set the path to the jar file
JAR_FILE="./VenturaApp.jar"

# Invoke the Java application
$JAVA_EXE -jar $JAR_FILE -DDEBUG=false -DSETTINGS_FILE=./ventura.properties