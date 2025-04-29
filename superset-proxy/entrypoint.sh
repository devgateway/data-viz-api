#!/bin/bash

 		PROP_FILE="/etc/$1.properties"
	  truncate -s 0 $PROP_FILE
	  if [[ ! -z "${DEFAULT_PROP_FILE_NAME}" ]]; then
	    cat $DEFAULT_PROP_FILE_NAME > $PROP_FILE
    fi
  	echo "..................... NEW Writing to $PROP_FILE: ............... "


to_camel_case() {
  local input="$1"
  local output=""
  IFS='/' read -ra parts <<< "$input"
  output="${parts[0]}"
  for ((i=1; i<${#parts[@]}; i++)); do
    part="${parts[i]}"
    output+="${part^}"
  done
  echo "$output"
}

    #EUREKA_CLIENT_SERVICE/URL_DEFAULT/ZONE
    #eureka.client.serviceUrl.defaultZone

    while IFS='=' read -r -d '' n v; do
      if [[ $n == SPRING_* || $n == EUREKA_* ]]; then

        if [[ "$n" == *"/"* ]]; then

              new_parts=()
              VAR_NAME="$(echo "$n" | tr '[:upper:]_' '[:lower:].')"
              IFS='.' read -ra PARTS <<< "$VAR_NAME"
            for part in "${PARTS[@]}"; do
              IFS='/' read -ra text <<< "$part"
               if [[ "$part" == *"/"* ]]; then

                new_parts+=("$(to_camel_case "$part")")
                else
                   new_parts+=("$part")
                fi

            done
            final_var_name=$(IFS='.'; echo "${new_parts[*]}")
           echo "$final_var_name=$v" >> "$PROP_FILE"
        else
          VAR_NAME="$(echo "$n" | tr '[:upper:]_' '[:lower:].')"
          echo "$VAR_NAME=$v" >> "$PROP_FILE"
        fi
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

		echo "--- JAVA_OPTS ---"
		echo "$JAVA_OPTS"
		echo "--- JAVA_OPTS ---"

		exec su -s /bin/sh -c "java $JAVA_OPTS -jar '$JAR' --spring.config.location=file://$PROP_FILE $@" nobody
		;;
	*)
		exec $@
		;;
esac
