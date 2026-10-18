#!/bin/bash

if [[ -f "$1-0.0.1-SNAPSHOT.jar" ]]; then
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
		read -r -a java_options <<< "${JAVA_OPTS:-}"

		echo "--- JAVA_OPTS ---"
		echo "$JAVA_OPTS"
		echo "--- JAVA_OPTS ---"

		JAVA_CMD=""
		if [[ -n "${JAVA_HOME:-}" ]] && "$JAVA_HOME/bin/java" -version >/dev/null 2>&1; then
			JAVA_CMD="$JAVA_HOME/bin/java"
		else
			while IFS= read -r java_directory; do
				java_candidate="${java_directory:-.}/java"
				if "$java_candidate" -version >/dev/null 2>&1; then
					JAVA_CMD="$java_candidate"
					break
				fi
			done < <(printf '%s\n' "$PATH" | tr ':' '\n')
		fi

		if [[ -z "$JAVA_CMD" ]]; then
			echo "Error: No runnable Java found in JAVA_HOME or PATH" >&2
			if [[ -n "${JAVA_HOME:-}" ]]; then
				"$JAVA_HOME/bin/java" -version >&2
			fi
			exit 127
		fi

		JAVA_CMD=$(readlink -f "$JAVA_CMD")
		export JAVA_HOME=$(dirname "$(dirname "$JAVA_CMD")")
		export PATH="${JAVA_HOME}/bin:${PATH}"
		exec su -s /bin/sh -c 'exec "$@"' -- nobody launcher "$JAVA_CMD" "${java_options[@]}" -jar "$JAR" "--spring.config.location=file://$PROP_FILE" "$@"
else
		exec "$@"
fi
