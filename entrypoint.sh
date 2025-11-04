#!/bin/bash

 		PROP_FILE="/etc/$1.properties"
	  truncate -s 0 $PROP_FILE
  	echo "..................... Writing to $PROP_FILE: ............... "

    while IFS='=' read -r -d '' n v; do
        if [[ $n == SPRING*  ||   $n == EUREKA* ]]; then
				  VAR_NAME="$(echo "$n" | tr '[:upper:]' '[:lower:]' | tr '_' '.' |  sed 's/--/_/g' )"
				  echo "$VAR_NAME=$v" >> $PROP_FILE
			  fi
    done < <(env -0)

    while IFS='=' read -r -d '' n v; do
        if [[ $n == VIZ_* ]]; then
				  VAR_NAME="$(echo "$n" | tr '[:upper:]_' '[:lower:].')"
				  echo "$VAR_NAME=$v" >> $PROP_FILE
			  fi
    done < <(env -0)


    echo "................. Properties ................."
    cat $PROP_FILE
    echo "................. End sou ................."

    MODULE="$1"
		shift
		JAR="$MODULE-0.0.1-SNAPSHOT.jar"
		JAVA_OPTS="$JAVA_OPTS --spring.config.location=file://$PROP_FILE"

		echo "--- JAVA_OPTS ---"
		echo "$JAVA_OPTS"
		echo "--- JAVA_OPTS ---"

		# Find Java executable - eclipse-temurin typically installs at /opt/java/openjdk
		if [ -z "$JAVA_HOME" ]; then
			if [ -d "/opt/java/openjdk" ]; then
				export JAVA_HOME="/opt/java/openjdk"
			elif [ -f "/usr/lib/jvm/java-21-openjdk-amd64/bin/java" ]; then
				export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
			else
				export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
			fi
		fi
		export PATH="${JAVA_HOME}/bin:${PATH}"
		JAVA_CMD="${JAVA_HOME}/bin/java"
		
		# Ensure java is executable
		if [ ! -x "$JAVA_CMD" ]; then
			echo "Error: Java not found at $JAVA_CMD"
			exit 1
		fi
		
		exec su -s /bin/sh -c "export JAVA_HOME='$JAVA_HOME' && export PATH='${JAVA_HOME}/bin:${PATH}' && '$JAVA_CMD' -jar '$JAR' $JAVA_OPTS $@" nobody
		;;
	*)
		exec $@
		;;
esac
