#!/bin/bash

if [ -f "$1-0.0.1-SNAPSHOT.jar" ]; then
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

		JAVA_CMD="${JAVA_HOME:+$JAVA_HOME/bin/java}"
		if [ -z "$JAVA_CMD" ] || ! "$JAVA_CMD" -version > /dev/null 2>&1; then
			JAVA_CMD=""
			IFS=: read -r -a JAVA_DIRECTORIES <<< "$PATH"
			for JAVA_DIRECTORY in "${JAVA_DIRECTORIES[@]}"; do
				JAVA_CANDIDATE="${JAVA_DIRECTORY:-.}/java"
				if "$JAVA_CANDIDATE" -version > /dev/null 2>&1; then
					JAVA_CMD="$JAVA_CANDIDATE"
					break
				fi
			done
		fi
		if [ -z "$JAVA_CMD" ]; then
			echo "Error: No runnable Java found. JAVA_HOME='${JAVA_HOME:-unset}', PATH='$PATH'" >&2
			if [ -n "${JAVA_HOME:-}" ]; then
				"$JAVA_HOME/bin/java" -version >&2 || true
			fi
			exit 1
		fi
		JAVA_CMD=$(readlink -f "$JAVA_CMD")
		export JAVA_HOME="$(dirname "$(dirname "$JAVA_CMD")")"
		export PATH="${JAVA_HOME}/bin:${PATH}"

		exec su -s /bin/sh -c "export JAVA_HOME='$JAVA_HOME' && export PATH='${JAVA_HOME}/bin:${PATH}' && exec '$JAVA_CMD' -jar '$JAR' $JAVA_OPTS $@" nobody
else
	exec "$@"
fi
