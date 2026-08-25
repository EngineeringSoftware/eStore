#!/bin/bash
# estore Development Tasks
# Usage: ./s [command]

if [ $(whoami) = "gligoric" ]; then
        if command -v /usr/libexec/java_home >/dev/null 2>&1; then
                export JAVA_HOME="$(/usr/libexec/java_home -v 1.8 2>/dev/null)"
        else
                export JAVA_HOME="$HOME/opt/jdk-8"
        fi
        export PATH="$JAVA_HOME/bin:$PATH"
fi

# ----------
# Functions.

function check_deps() {
        # Check dependencies for this project.
        ! hash "mvn" && \
                { echo "missing maven (https://maven.apache.org/download.cgi)"; return 1; }

        if ! hash "java" 2>/dev/null || ! hash "javac" 2>/dev/null; then
                echo "no JDK available (CI uses Oracle JDK 25; bytecode target is Java 8)"
                return 1
        fi

        ! hash "wget" && \
                { echo "missing wget (apt-get install wget)"; return 1; }

        ! hash "zstd" && \
                { echo "missing zstd (apt-get install zstd)"; return 1; }

        ! hash "tar" && \
                { echo "missing tar (apt-get install tar)"; return 1; }

        ! hash "gzip" && \
                { echo "missing gzip (apt-get install gzip)"; return 1; }

        ! hash "nc" && \
                { echo "missing nc (apt-get install netcat-openbsd)"; return 1; }

        echo "deps ok"

        return 0
}

function install_deps() {
        echo "Installing dependencies..."

        if ! hash "mvn" 2>/dev/null; then
                echo "Installing Maven..."
                apt-get update && apt-get install -y maven || \
                        { echo "Failed to install Maven"; return 1; }
        fi

        if ! hash "java" 2>/dev/null || ! hash "javac" 2>/dev/null; then
                echo "No JDK found. Install Oracle JDK 25 from https://www.oracle.com/java/technologies/downloads/"
                return 1
        fi

        if ! hash "wget" 2>/dev/null; then
                echo "Installing wget..."
                apt-get update && apt-get install -y wget || \
                        { echo "Failed to install wget"; return 1; }
        fi

        if ! hash "zstd" 2>/dev/null; then
                echo "Installing zstd..."
                apt-get update && apt-get install -y zstd || \
                        { echo "Failed to install zstd"; return 1; }
        fi

        if ! hash "tar" 2>/dev/null; then
                echo "Installing tar..."
                apt-get update && apt-get install -y tar || \
                        { echo "Failed to install tar"; return 1; }
        fi

        if ! hash "gzip" 2>/dev/null; then
                echo "Installing gzip..."
                apt-get update && apt-get install -y gzip || \
                        { echo "Failed to install gzip"; return 1; }
        fi

        if ! hash "nc" 2>/dev/null; then
                echo "Installing netcat..."
                apt-get update && apt-get install -y netcat-openbsd || \
                        { echo "Failed to install netcat"; return 1; }
        fi

        echo "Dependencies installed successfully!"
        return 0
}

function compile_estore() {
        # Build the project.
        ( cd estore
          mvn clean compile || \
                  { echo "could not compile estore"; return 1; }
        )
}

function download_data() {
        # Download data for estore test.
        ./scripts/download_test_data || \
                { echo "could not download data"; return 1; }
}

function install_estore() {
        check_deps || \
                { echo "Dependencies not satisfied. Please install with: ./s install_deps"; return 1; }
        download_data || \
                { echo "could not download data"; return 1; }
        ( cd estore
          mvn install || \
                  { echo "could not install estore"; return 1; }
        )
}

function exec_estore() {
        check_deps || \
                { echo "Dependencies not satisfied. Please install with: ./s install_deps"; return 1; }
        echo "Usage: $0 [port]"
        local port=${1:-1234}
        echo "Example: echo 'MATCH (n) return n' | nc -q 0 localhost $port"
        echo "send 'q' to exit"
        compile_estore
        ( cd estore
          mvn exec:java@main -Dexec.args="$port" &
          echo "Running db as $!"
        )
}

function prepare_eval() {
        #Prepares evaluation by copying needed jars
        ( cd eval
          rm -rf libs && mkdir libs
          cp ../estore/target/*.jar libs || \
                  { echo "could not copy estore jars into libs in eval"; return 1; }
        )
}

function end_to_end() {
        check_deps || \
                { echo "deps not satisfied"; exit 1; }
        install_estore || \
                { echo "could not install estore"; exit 1; }
        prepare_eval || \
                { echo "could not prepare evaluation"; exit 1; }
}

"$@"
