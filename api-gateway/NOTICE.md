# Third-Party License Notices

This module (api-gateway) is part of the Data Viz API project and is licensed under the
Apache License, Version 2.0.

The following third-party libraries are included or linked as dependencies. Libraries available
under Apache-2.0 exclusively are omitted. Where a library is available under multiple licenses,
the license chosen for use in this project is noted explicitly.

---

## LGPL-2.1-only

### ch.qos.logback:logback-classic, ch.qos.logback:logback-core

- **License:** LGPL-2.1-only
- **Used in:** api-gateway (transitive via spring-boot-starter-logging)
- **Notes:** This module dynamically links against Logback. Dynamic linking satisfies the LGPL
  requirements without imposing LGPL obligations on this module's own source code.

---

## GPL-2.0-with-Classpath-Exception

The libraries below are licensed under GPL-2.0-with-Classpath-Exception
(https://spdx.org/licenses/GPL-2.0-with-classpath-exception.html). The classpath exception permits
use in non-GPL projects without triggering GPL obligations, provided the library is not modified.

### jakarta.annotation:jakarta.annotation-api

- **Used in:** api-gateway (transitive via com.netflix.eureka:eureka-client)

### jakarta.ws.rs:jakarta.ws.rs-api

- **Used in:** api-gateway (transitive via com.netflix.eureka:eureka-client)

### javax.annotation:javax.annotation-api

- **License chosen:** GPL-2.0-with-Classpath-Exception (dual-licensed CDDL-1.0 /
  GPL-2.0-with-Classpath-Exception)
- **Used in:** api-gateway (transitive via com.netflix.eureka:eureka-client, runtime scope)

---

## BSD-3-Clause

### com.thoughtworks.xstream:xstream

- **License:** BSD-3-Clause
- **Used in:** api-gateway (transitive via com.netflix.eureka:eureka-client)

### org.antlr:antlr-runtime, org.antlr:stringtemplate, antlr:antlr

- **License:** BSD (https://antlr.org/license.html)
- **Used in:** api-gateway (transitive via com.netflix.netflix-commons:netflix-infix, runtime scope)

---

## Indiana University Extreme! Lab Software License

### io.github.x-stream:mxparser

- **License:** Indiana University Extreme! Lab Software License (BSD-style;
  https://x-stream.github.io/mxparser/license.html)
- **Used in:** api-gateway (transitive via com.thoughtworks.xstream:xstream)

---

## Bouncy Castle Licence

### org.bouncycastle:bcprov-jdk18on

- **License:** Bouncy Castle Licence (MIT-style; https://www.bouncycastle.org/licence.html)
- **Used in:** api-gateway (transitive via org.springframework.security:spring-security-rsa)

---

## Public Domain

### xmlpull:xmlpull

- **License:** Public Domain
- **Used in:** api-gateway (transitive via io.github.x-stream:mxparser)
