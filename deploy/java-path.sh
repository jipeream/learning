#!/usr/bin/env bash
#JAVA_HOME=/binarios/jdk1.7.0_79
JAVA_HOME=/binarios/jdk1.8.0_40
echo JAVA_HOME=$JAVA_HOME
export JAVA_HOME

PATH=$JAVA_HOME/bin:$PATH
echo PATH=$PATH
export PATH

java -version