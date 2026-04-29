# Third-Party License Notices

This project (Data Viz API) is licensed under the Apache License, Version 2.0.

The following third-party libraries are included or linked as dependencies. Where a library is
available under multiple licenses, the license chosen for use in this project is noted explicitly.

---

## LGPL-2.1-only

### ch.qos.logback:logback-classic, ch.qos.logback:logback-core

- **License chosen:** LGPL-2.1-only
- **Used in:** api-gateway, api-security, commons, registry, superset-proxy (transitive via Spring Boot)
- **Notes:** This project dynamically links against Logback. Dynamic linking satisfies the LGPL
  requirements without imposing LGPL obligations on this project's own source code.

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

- **License chosen:** GPL-2.0-with-Classpath-Exception
- **Used in:** commons

---

## Eclipse Public License 2.0 (EPL-2.0)

### com.sun.mail:jakarta.mail

- **License chosen:** EPL-2.0 (dual-licensed EPL-2.0 / GPL-2.0-with-Classpath-Exception)
- **Used in:** commons (also declared in root pom.xml)

### org.glassfish.jersey.connectors:jersey-apache-connector
### org.glassfish.jersey.containers:jersey-container-servlet
### org.glassfish.jersey.containers:jersey-container-servlet-core
### org.glassfish.jersey.core:jersey-client
### org.glassfish.jersey.core:jersey-common
### org.glassfish.jersey.core:jersey-server
### org.glassfish.jersey.inject:jersey-hk2

- **License chosen:** EPL-2.0 (dual-licensed EPL-2.0 / GPL-2.0-with-Classpath-Exception)
- **Used in:** registry (transitive via spring-cloud-starter-netflix-eureka-server)
- **Notes:** EPL-2.0 is chosen in preference to GPL-2.0-CPE for clearer compatibility with this
  project's Apache-2.0 license.

### org.glassfish.hk2.external:aopalliance-repackaged
### org.glassfish.hk2:hk2-api
### org.glassfish.hk2:hk2-locator
### org.glassfish.hk2:hk2-utils
### org.glassfish.hk2:osgi-resource-locator
### org.glassfish.hk2:spring-bridge

- **License chosen:** EPL-2.0 (dual-licensed EPL-2.0 / GPL-2.0-with-Classpath-Exception)
- **Used in:** registry (transitive via spring-cloud-starter-netflix-eureka-server)

---

## Apache License, Version 2.0

### org.javassist:javassist

- **License chosen:** Apache-2.0 (triple-licensed: Apache-2.0 / LGPL-2.1 / MPL-1.1;
  see https://www.javassist.org/)
- **Used in:** registry (transitive via spring-cloud-starter-netflix-eureka-server)
