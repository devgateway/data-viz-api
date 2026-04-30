# Third-Party License Notices

This module (api-security) is part of the Data Viz API project and is licensed under the
Apache License, Version 2.0.

The following third-party libraries are included or linked as dependencies. Libraries available
under Apache-2.0 exclusively are omitted. Where a library is available under multiple licenses,
the license chosen for use in this project is noted explicitly.

---

## LGPL-2.1-only

### ch.qos.logback:logback-classic, ch.qos.logback:logback-core

- **License:** LGPL-2.1-only
- **Used in:** api-security (transitive via spring-boot-starter-logging)
- **Notes:** This module dynamically links against Logback. Dynamic linking satisfies the LGPL
  requirements without imposing LGPL obligations on this module's own source code.

---

## LGPL-2.1-or-later

### org.hibernate.orm:hibernate-core, org.hibernate.common:hibernate-commons-annotations

- **License:** LGPL-2.1-or-later
- **Used in:** api-security (transitive via spring-boot-starter-data-jpa)
- **Notes:** This module dynamically links against Hibernate. Dynamic linking satisfies the LGPL
  requirements without imposing LGPL obligations on this module's own source code.

---

## GPL-2.0-with-Classpath-Exception

The libraries below are licensed under GPL-2.0-with-Classpath-Exception
(https://spdx.org/licenses/GPL-2.0-with-classpath-exception.html). The classpath exception permits
use in non-GPL projects without triggering GPL obligations, provided the library is not modified.

### jakarta.annotation:jakarta.annotation-api

- **Used in:** api-security (transitive via com.netflix.eureka:eureka-client)

### jakarta.ws.rs:jakarta.ws.rs-api

- **Used in:** api-security (transitive via com.netflix.eureka:eureka-client)

### jakarta.transaction:jakarta.transaction-api

- **Used in:** api-security (transitive via org.hibernate.orm:hibernate-core)

### javax.annotation:javax.annotation-api

- **License chosen:** GPL-2.0-with-Classpath-Exception (dual-licensed CDDL-1.0 /
  GPL-2.0-with-Classpath-Exception)
- **Used in:** api-security (transitive via com.netflix.eureka:eureka-client, runtime scope)

---

## Eclipse Public License 2.0 (EPL-2.0)

### jakarta.persistence:jakarta.persistence-api

- **License chosen:** EPL-2.0 (dual-licensed EPL-2.0 / Eclipse Distribution License 1.0)
- **Used in:** api-security (transitive via org.hibernate.orm:hibernate-core)

### org.aspectj:aspectjweaver

- **License:** EPL-2.0
- **Used in:** api-security (transitive via spring-boot-starter-aop)

---

## BSD-3-Clause

### com.thoughtworks.xstream:xstream

- **License:** BSD-3-Clause
- **Used in:** api-security (transitive via com.netflix.eureka:eureka-client)

### org.antlr:antlr-runtime, org.antlr:stringtemplate, antlr:antlr

- **License:** BSD (https://antlr.org/license.html)
- **Used in:** api-security (transitive via com.netflix.netflix-commons:netflix-infix, runtime scope)

### org.antlr:antlr4-runtime

- **License:** BSD (https://antlr.org/license.html)
- **Used in:** api-security (transitive via org.hibernate.orm:hibernate-core)

### org.postgresql:postgresql

- **License:** BSD-2-Clause
- **Used in:** api-security (direct dependency, runtime scope)

---

## Indiana University Extreme! Lab Software License

### io.github.x-stream:mxparser

- **License:** Indiana University Extreme! Lab Software License (BSD-style;
  https://x-stream.github.io/mxparser/license.html)
- **Used in:** api-security (transitive via com.thoughtworks.xstream:xstream)

---

## Bouncy Castle Licence

### org.bouncycastle:bcprov-jdk18on

- **License:** Bouncy Castle Licence (MIT-style; https://www.bouncycastle.org/licence.html)
- **Used in:** api-security (transitive via org.springframework.security:spring-security-rsa)

---

## Public Domain

### xmlpull:xmlpull

- **License:** Public Domain
- **Used in:** api-security (transitive via io.github.x-stream:mxparser)
