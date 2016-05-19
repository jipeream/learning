#!/usr/bin/env bash

#export JAVA_HOME=/binarios/jdk1.7.0_79
export JAVA_HOME=/binarios/jdk1.8.0_40
echo JAVA_HOME=$JAVA_HOME

export PATH=$JAVA_HOME/bin:$PATH
echo PATH=$PATH

java -version