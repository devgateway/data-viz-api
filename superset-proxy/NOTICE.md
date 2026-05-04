# Third-Party License Notices

This module (superset-proxy) is part of the Data Viz API project and is licensed under the
Apache License, Version 2.0.

The following third-party libraries are included or linked as dependencies. Libraries available
under Apache-2.0 exclusively are omitted. Where a library is available under multiple licenses,
the license chosen for use in this project is noted explicitly.

---

## Eclipse Public License 1.0 (EPL-1.0)

### ch.qos.logback:logback-classic, ch.qos.logback:logback-core

- **License chosen:** EPL-1.0 (dual-licensed EPL-1.0 / LGPL 2.1)
- **Used in:** superset-proxy (transitive via spring-boot-starter-logging)
- **Notes:** Logback is dual-licensed under EPL-1.0 and LGPL 2.1. EPL-1.0 is chosen because
  it imposes no copyleft obligations on this module's own source code. This module uses Logback
  as a runtime dependency only and does not distribute or modify its source.

---

## GPL-2.0-with-Classpath-Exception

The libraries below are licensed under GPL-2.0-with-Classpath-Exception
(https://spdx.org/licenses/GPL-2.0-with-classpath-exception.html). The classpath exception permits
use in non-GPL projects without triggering GPL obligations, provided the library is not modified.
GPL-2.0-with-Classpath-Exception is formally compatible with Apache-2.0 (ASF Category A).

### jakarta.annotation:jakarta.annotation-api

- **Used in:** superset-proxy (transitive via com.netflix.eureka:eureka-client)

### jakarta.ws.rs:jakarta.ws.rs-api

- **Used in:** superset-proxy (transitive via com.netflix.eureka:eureka-client)

### javax.annotation:javax.annotation-api

- **License chosen:** GPL-2.0-with-Classpath-Exception (dual-licensed CDDL-1.0 / GPL-2.0-with-Classpath-Exception)
- **Used in:** superset-proxy (transitive via com.netflix.eureka:eureka-client, runtime scope)

---

## BSD-3-Clause

### com.thoughtworks.xstream:xstream

- **License:** BSD-3-Clause
- **Used in:** superset-proxy (transitive via com.netflix.eureka:eureka-client)

### org.antlr:antlr-runtime, org.antlr:stringtemplate, antlr:antlr

- **License:** BSD (https://antlr.org/license.html)
- **Used in:** superset-proxy (transitive via com.netflix.netflix-commons:netflix-infix, runtime scope)

### org.hdrhistogram:HdrHistogram

- **License:** BSD-2-Clause
- **Used in:** superset-proxy (transitive via io.micrometer:micrometer-core, runtime scope)

---

## Indiana University Extreme! Lab Software License

### io.github.x-stream:mxparser

- **License:** Indiana University Extreme! Lab Software License (BSD-style;
  https://x-stream.github.io/mxparser/license.html)
- **Used in:** superset-proxy (transitive via com.thoughtworks.xstream:xstream)

---

## Bouncy Castle Licence

### org.bouncycastle:bcprov-jdk18on

- **License:** Bouncy Castle Licence (MIT-style; https://www.bouncycastle.org/licence.html)
- **Used in:** superset-proxy (transitive via org.springframework.security:spring-security-rsa)

---

## MIT License

### io.socket:socket.io-client, io.socket:engine.io-client

- **License:** MIT
- **Used in:** superset-proxy (direct dependency)

---

## Public Domain

### xmlpull:xmlpull

- **License:** Public Domain
- **Used in:** superset-proxy (transitive via io.github.x-stream:mxparser)

### org.latencyutils:LatencyUtils

- **License:** Public Domain, per Creative Commons CC0
  (https://creativecommons.org/publicdomain/zero/1.0/)
- **Used in:** superset-proxy (transitive via io.micrometer:micrometer-core, runtime scope)

---

## JSON License

### org.json:json

- **License:** JSON License (https://www.json.org/license.html)
- **Used in:** superset-proxy (transitive via io.socket:socket.io-client)
- **Notes:** The JSON License adds the clause "The Software shall be used for Good, not Evil,"
  which is not OSI-approved and is technically incompatible with Apache-2.0. This library is not
  used directly by this module's source code; it is pulled in transitively by socket.io-client
  and cannot be excluded without forking that dependency. It is documented here as a known
  third-party exception.
