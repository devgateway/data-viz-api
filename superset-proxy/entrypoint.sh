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


    echo  'server.compression.enabled=true' >> $PROP_FILE
    echo  '# ========================================
           # GZIP Compression Settings for Spring Boot
           # ========================================
           server.compression.enabled=true
           server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain
           server.compression.min-response-size=1024
           server.compression.enabled-methods=GET,POST
           management.endpoints.web.exposure.include=health,actuator,info

           ' >> $PROP_FILE



    echo "................. Properties ................."
    cat $PROP_FILE
    echo "................. End sou ................."

    MODULE="$1"
		shift
		JAR="superset-proxy-0.0.1-SNAPSHOT.jar"
		JAVA_OPTS="$JAVA_OPTS --spring.config.location=file://$PROP_FILE"

		echo "--- JAVA_OPTS ---"
		echo "$JAVA_OPTS"
		echo "--- JAVA_OPTS ---"

		exec su -s /bin/sh -c "java -jar '$JAR' $JAVA_OPTS $@" nobody
		;;
	*)
		exec $@
		;;
esac
