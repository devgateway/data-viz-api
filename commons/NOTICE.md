# Third-Party License Notices

This module (commons) is part of the Data Viz API project and is licensed under the
Apache License, Version 2.0.

The following third-party libraries are included or linked as dependencies. Libraries available
under Apache-2.0 exclusively are omitted. Where a library is available under multiple licenses,
the license chosen for use in this project is noted explicitly.

---

## Eclipse Public License 1.0 (EPL-1.0)

### ch.qos.logback:logback-classic, ch.qos.logback:logback-core

- **License chosen:** EPL-1.0 (dual-licensed EPL-1.0 / LGPL 2.1)
- **Used in:** commons (transitive via spring-boot-starter-logging)
- **Notes:** Logback is dual-licensed under EPL-1.0 and LGPL 2.1. EPL-1.0 is chosen because
  it imposes no copyleft obligations on this module's own source code. This module uses Logback
  as a runtime dependency only and does not distribute or modify its source.

---

## LGPL-2.1-or-later

### org.hibernate.orm:hibernate-core, org.hibernate.common:hibernate-commons-annotations

- **License:** LGPL-2.1-or-later
- **Used in:** commons (transitive via spring-boot-starter-data-jpa)
- **Notes:** This module dynamically links against Hibernate. Dynamic linking satisfies the LGPL
  requirements without imposing LGPL obligations on this module's own source code.

---

## GPL-2.0-with-Classpath-Exception

The libraries below are licensed under GPL-2.0-with-Classpath-Exception
(https://spdx.org/licenses/GPL-2.0-with-classpath-exception.html). The classpath exception permits
use in non-GPL projects without triggering GPL obligations, provided the library is not modified.
GPL-2.0-with-Classpath-Exception is formally compatible with Apache-2.0 (ASF Category A).

### jakarta.annotation:jakarta.annotation-api

- **Used in:** commons (transitive via spring-data-jpa)

### jakarta.transaction:jakarta.transaction-api

- **Used in:** commons (transitive via org.hibernate.orm:hibernate-core)

### jakarta.ws.rs:jakarta.ws.rs-api

- **Used in:** commons (transitive via com.netflix.eureka:eureka-client)

### javax.xml.bind:jaxb-api

- **License chosen:** GPL-2.0-with-Classpath-Exception (dual-licensed CDDL-1.1 / GPL-2.0-with-Classpath-Exception)
- **Used in:** commons (transitive via org.liquibase:liquibase-core)

### javax.annotation:javax.annotation-api

- **License chosen:** GPL-2.0-with-Classpath-Exception (dual-licensed CDDL-1.0 / GPL-2.0-with-Classpath-Exception)
- **Used in:** commons (transitive via com.netflix.eureka:eureka-client, runtime scope)

### com.sun.mail:jakarta.mail

- **License chosen:** GPL-2.0-with-Classpath-Exception (dual-licensed EPL-2.0 / GPL-2.0-with-Classpath-Exception)
- **Used in:** commons (direct dependency)

---

## Eclipse Distribution License 1.0 (EDL-1.0)

EDL-1.0 is equivalent to BSD-3-Clause and imposes no copyleft obligations.

### jakarta.persistence:jakarta.persistence-api

- **License chosen:** EDL-1.0 (dual-licensed EPL-2.0 / Eclipse Distribution License 1.0)
- **Used in:** commons (direct dependency)

---

## Eclipse Public License 2.0 (EPL-2.0)

### org.aspectj:aspectjweaver

- **License:** EPL-2.0
- **Used in:** commons (transitive via spring-boot-starter-aop)

### org.eclipse.jdt:ecj

- **License:** EPL-2.0
- **Used in:** commons (transitive via com.querydsl:querydsl-apt)

### org.junit.jupiter:junit-jupiter, junit-jupiter-api, junit-jupiter-params, junit-jupiter-engine
### org.junit.platform:junit-platform-commons, junit-platform-engine

- **License:** EPL-2.0
- **Used in:** commons (direct dependency — exposed as compile-scope test utilities for
  consuming modules)

---

## BSD-3-Clause

### com.thoughtworks.xstream:xstream

- **License:** BSD-3-Clause
- **Used in:** commons (transitive via com.netflix.eureka:eureka-client)

### org.antlr:antlr-runtime, org.antlr:stringtemplate, antlr:antlr

- **License:** BSD (https://antlr.org/license.html)
- **Used in:** commons (transitive via com.netflix.netflix-commons:netflix-infix, runtime scope)

### org.antlr:antlr4-runtime

- **License:** BSD (https://antlr.org/license.html)
- **Used in:** commons (transitive via org.hibernate.orm:hibernate-core)

### org.hdrhistogram:HdrHistogram

- **License:** BSD-2-Clause
- **Used in:** commons (transitive via io.micrometer:micrometer-core, runtime scope)

### org.postgresql:postgresql

- **License:** BSD-2-Clause
- **Used in:** commons (direct dependency)

---

## Indiana University Extreme! Lab Software License

### io.github.x-stream:mxparser

- **License:** Indiana University Extreme! Lab Software License (BSD-style;
  https://x-stream.github.io/mxparser/license.html)
- **Used in:** commons (transitive via com.thoughtworks.xstream:xstream)

---

## Bouncy Castle Licence

### org.bouncycastle:bcprov-jdk18on

- **License:** Bouncy Castle Licence (MIT-style; https://www.bouncycastle.org/licence.html)
- **Used in:** commons (transitive via org.springframework.security:spring-security-rsa)

---

## Public Domain

### xmlpull:xmlpull

- **License:** Public Domain
- **Used in:** commons (transitive via io.github.x-stream:mxparser)

### org.latencyutils:LatencyUtils

- **License:** Public Domain, per Creative Commons CC0
  (https://creativecommons.org/publicdomain/zero/1.0/)
- **Used in:** commons (transitive via io.micrometer:micrometer-core, runtime scope)
