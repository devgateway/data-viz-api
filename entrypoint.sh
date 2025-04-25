***REMOVED***!/bin/bash

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

		echo "--- JAVA_OPTS ---"
		echo "$JAVA_OPTS"
		echo "--- JAVA_OPTS ---"

		exec su -s /bin/sh -c "java $JAVA_OPTS -jar '$JAR' --spring.config.location=file://$PROP_FILE $@" nobody
		;;
	*)
		exec $@
		;;
esac
