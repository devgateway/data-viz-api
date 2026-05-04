# Third-Party License Notices

This project (Data Viz API) is licensed under the Apache License, Version 2.0.

The following third-party libraries are included or linked as dependencies. Where a library is
available under multiple licenses, the license chosen for use in this project is noted explicitly.

---

## Eclipse Public License 1.0 (EPL-1.0)

### ch.qos.logback:logback-classic, ch.qos.logback:logback-core

- **License chosen:** EPL-1.0 (dual-licensed EPL-1.0 / LGPL 2.1)
- **Used in:** api-gateway, api-security, commons, registry, superset-proxy (transitive via Spring Boot)
- **Notes:** Logback is dual-licensed under EPL-1.0 and LGPL 2.1. EPL-1.0 is chosen because
  it imposes no copyleft obligations on this project's own source code. This project uses Logback
  as a runtime dependency only and does not distribute or modify its source.

---

## LGPL-2.1-or-later (deferred upgrade)

### org.hibernate.orm:hibernate-core, org.hibernate.common:hibernate-commons-annotations

- **License chosen:** LGPL-2.1-or-later
- **Used in:** api-security, commons (transitive via spring-boot-starter-data-jpa)
- **Notes:** Hibernate ORM 6.x is used, which is licensed under LGPL-2.1-or-later. This project
  dynamically links against Hibernate; dynamic linking satisfies LGPL requirements. Hibernate ORM 7
  (Apache-2.0) requires Spring Boot 3.5+. Upgrading Spring Boot is tracked as a separate effort;
  at that point this entry should be removed and replaced with an Apache-2.0 notice.

---

## GPL-2.0-with-Classpath-Exception

The libraries below are licensed under GPL-2.0-with-Classpath-Exception
(https://spdx.org/licenses/GPL-2.0-with-classpath-exception.html). The classpath exception permits
use in non-GPL projects without triggering GPL obligations, provided the library is not modified.
GPL-2.0-with-Classpath-Exception is formally compatible with Apache-2.0 (ASF Category A).

### jakarta.annotation:jakarta.annotation-api

- **License chosen:** GPL-2.0-with-Classpath-Exception
- **Used in:** api-gateway, api-security, commons, registry, superset-proxy

### jakarta.ws.rs:jakarta.ws.rs-api

- **License chosen:** GPL-2.0-with-Classpath-Exception
- **Used in:** api-gateway, api-security, commons, registry, superset-proxy

### jakarta.transaction:jakarta.transaction-api

- **License chosen:** GPL-2.0-with-Classpath-Exception
- **Used in:** api-security, commons

### javax.xml.bind:jaxb-api

- **License chosen:** GPL-2.0-with-Classpath-Exception (dual-licensed CDDL-1.1 / GPL-2.0-with-Classpath-Exception)
- **Used in:** commons

### com.sun.mail:jakarta.mail

- **License chosen:** GPL-2.0-with-Classpath-Exception (dual-licensed EPL-2.0 / GPL-2.0-with-Classpath-Exception)
- **Used in:** commons (also declared in root pom.xml)

### org.glassfish.jersey.connectors:jersey-apache-connector
### org.glassfish.jersey.containers:jersey-container-servlet
### org.glassfish.jersey.containers:jersey-container-servlet-core
### org.glassfish.jersey.core:jersey-client
### org.glassfish.jersey.core:jersey-common
### org.glassfish.jersey.core:jersey-server
### org.glassfish.jersey.inject:jersey-hk2

- **License chosen:** GPL-2.0-with-Classpath-Exception (dual-licensed EPL-2.0 / GPL-2.0-with-Classpath-Exception)
- **Used in:** registry (transitive via spring-cloud-starter-netflix-eureka-server)

### org.glassfish.hk2.external:aopalliance-repackaged
### org.glassfish.hk2:hk2-api
### org.glassfish.hk2:hk2-locator
### org.glassfish.hk2:hk2-utils
### org.glassfish.hk2:osgi-resource-locator
### org.glassfish.hk2:spring-bridge

- **License chosen:** GPL-2.0-with-Classpath-Exception (dual-licensed EPL-2.0 / GPL-2.0-with-Classpath-Exception)
- **Used in:** registry (transitive via spring-cloud-starter-netflix-eureka-server)

---

## Apache License, Version 2.0

### org.javassist:javassist

- **License chosen:** Apache-2.0 (triple-licensed: Apache-2.0 / LGPL-2.1 / MPL-1.1;
  see https://www.javassist.org/)
- **Used in:** registry (transitive via spring-cloud-starter-netflix-eureka-server)

---

## JSON License

### org.json:json

- **License:** JSON License (https://www.json.org/license.html)
- **Used in:** superset-proxy (transitive via io.socket:socket.io-client)
- **Notes:** The JSON License adds the clause "The Software shall be used for Good, not Evil,"
  which is not OSI-approved and is technically incompatible with Apache-2.0. This library is not
  used directly by this project's source code; it is pulled in transitively by socket.io-client
  and cannot be excluded without forking that dependency. It is documented here as a known
  third-party exception.
