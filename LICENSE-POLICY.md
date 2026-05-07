# License Selection Policy

This document captures the criteria used to select licenses for dual-licensed third-party
dependencies in Apache-2.0 projects. Apply these rules when updating NOTICE.md files or
evaluating new dependencies.

---

## Quick Reference

| License options | Choose | Reason |
|---|---|---|
| EPL-1.0 / LGPL 2.1 | **EPL-1.0** | No re-linking clause; safer for Docker fat JARs |
| EPL-2.0 / GPL-2.0-CPE | **GPL-2.0-CPE** | GPL-2.0-CPE is ASF Category A; EPL-2.0 patent clause conflicts with Apache-2.0 |
| EPL-2.0 / EDL-1.0 | **EDL-1.0** | EDL-1.0 = BSD-3-Clause; fully permissive, no copyleft |
| CDDL / GPL-2.0-CPE | **GPL-2.0-CPE** | Consistent with other Java EE deps; smaller license ecosystem |
| Apache-2.0 / anything | **Apache-2.0** | Always choose Apache-2.0 when available |
| LGPL (any) / other | **other** | Avoid LGPL re-linking clause in containerized deployments |

---

## Guiding Principles

1. **Prefer the most permissive option.** When a dependency is dual-licensed, choose the license
   that imposes the fewest obligations on this project's own source code.

2. **Minimize the license ecosystem.** Fewer distinct licenses means simpler audits and less
   explanation required. Consolidate into a small set of well-understood licenses.

3. **Prefer licenses formally compatible with Apache-2.0.** The Apache Software Foundation
   maintains a categorization of third-party licenses. Prefer Category A licenses; avoid
   Category X.

4. **Document the choice explicitly.** Every dual-licensed dependency must state which license
   was chosen and why, so future audits (e.g. Trivy, FOSSA) can be answered consistently.

---

## Apache Software Foundation License Categories

| Category | Meaning | Examples |
|---|---|---|
| **A** | Compatible with Apache-2.0; can be included in Apache works | Apache-2.0, MIT, BSD-2/3-Clause, GPL-2.0-CPE, CDDL-1.0, EDL-1.0 |
| **B** | Can be used as a dependency but not bundled/included | EPL-1.0, EPL-2.0, LGPL (dynamic linking only) |
| **X** | Cannot be used in Apache works | GPL-2.0-only, GPL-3.0-only, AGPL-3.0 |

---

## Decision Rules by License Pair

### EPL-1.0 vs LGPL 2.1 (e.g. logback)

**Choose EPL-1.0.**

LGPL 2.1 has a re-linking clause: users must be able to replace the library with a modified
version and re-link the application against it. For Docker-based deployments using Spring Boot
fat JARs, this requirement is technically satisfied (the JAR is a discrete file), but the
replacement path is non-obvious and can raise questions in audits. EPL-1.0 has no re-linking
requirement, making it the cleaner choice for containerized Java applications.

### EPL-2.0 vs GPL-2.0-with-Classpath-Exception (e.g. Jersey, HK2, jakarta.mail)

**Choose GPL-2.0-with-Classpath-Exception.**

Despite the "GPL" name, GPL-2.0-with-Classpath-Exception is ASF Category A and was specifically
designed for Java library usage. The Classpath Exception explicitly removes any copyleft
obligation for projects that link against the library without modifying it. EPL-2.0 is ASF
Category B — its patent retaliation clause creates a technical incompatibility with the
Apache-2.0 patent grant. For an Apache-2.0 project, GPL-2.0-CPE is the formally correct choice.

### EPL-2.0 vs EDL-1.0 (e.g. jakarta.persistence-api)

**Choose EDL-1.0.**

The Eclipse Distribution License 1.0 (EDL-1.0) is equivalent to BSD-3-Clause: fully
permissive, no copyleft at all. EPL-2.0 requires modifications to EPL code to remain EPL.
When a library offers EDL-1.0 as an alternative, it is always the preferred choice.

### CDDL vs GPL-2.0-with-Classpath-Exception (e.g. jaxb-api, javax.annotation-api)

**Choose GPL-2.0-with-Classpath-Exception.**

Both are ASF Category A and impose no obligations on the consuming project's own source code.
GPL-2.0-CPE is chosen for consistency: it is already used for other Java EE dependencies in
this project, keeps the license ecosystem smaller, and is more widely understood in the
Java ecosystem than CDDL.

### Multi-licensed with Apache-2.0 as an option (e.g. javassist: Apache-2.0 / LGPL-2.1 / MPL-1.1)

**Choose Apache-2.0.**

When Apache-2.0 is one of the available options, always choose it. It is identical to this
project's own license and imposes no additional obligations.

---

## SPDX Identifier Conventions

Use informal names as the library documents them, not SPDX machine identifiers, unless the
context requires SPDX precision (e.g. a tool configuration file).

| Informal name (use in NOTICE.md) | SPDX identifier (for reference) |
|---|---|
| LGPL 2.1 | `LGPL-2.1-only` |
| LGPL 2.1 or later | `LGPL-2.1-or-later` |
| GPL-2.0-with-Classpath-Exception | `GPL-2.0-with-classpath-exception` |
| EPL-1.0 | `EPL-1.0` |
| EPL-2.0 | `EPL-2.0` |
| EDL-1.0 | `LicenseRef-Eclipse-EDL-1.0` |

Note: Trivy and other scanners report SPDX identifiers. When a scanner reports `LGPL-2.1-only`,
this corresponds to what the library itself calls "LGPL version 2.1" — the `-only` suffix is
SPDX convention, not language from the library's own license page.

---

## Licenses That Require No Active Choice

These licenses are not copyleft and require no choice between alternatives. Document them
in NOTICE.md but no rationale is needed:

- MIT
- BSD-2-Clause, BSD-3-Clause
- Apache-2.0
- Public Domain / CC0
- Bouncy Castle Licence (MIT-style)
- Indiana University Extreme! Lab Software License (BSD-style)

---

## Licenses That Are Always Documented as Exceptions

These licenses are legally problematic or incompatible with Apache-2.0 and must be documented
with an explicit exception note if they cannot be removed:

| License | Reason | Action |
|---|---|---|
| JSON License | "Good, not Evil" clause is not OSI-approved; incompatible with Apache-2.0 | Document as known transitive exception if it cannot be excluded |
| GPL-2.0-only | No classpath exception; incompatible with Apache-2.0 | Must be excluded or removed |
| GPL-3.0-only | Same | Must be excluded or removed |
| AGPL-3.0 | Network copyleft; incompatible with Apache-2.0 | Must be excluded or removed |

---

## Approved Exceptions Register

This section records formally approved exceptions for dependencies that cannot be removed or
replaced, and where the standard policy cannot be satisfied. Each entry must state the dependency,
the reason it cannot be excluded, and the decision made.

### EXC-001 — org.json:json (JSON License)

- **Dependency:** `org.json:json`
- **License:** JSON License — includes the clause "The Software shall be used for Good, not Evil"
- **Brought in by:** `io.socket:socket.io-client:2.1.2` (transitive, superset-proxy module)
- **Why it cannot be excluded:** `socket.io-client` calls `org.json` classes directly in its own
  source code. A Maven `<exclusion>` would remove the JAR from the classpath and cause
  `ClassNotFoundException` at runtime. There is no newer version of `socket.io-client` that
  replaces `org.json` with an alternative parser.
- **Why it cannot be replaced:** No drop-in Java Socket.IO client library is available that
  does not transitively require `org.json`. Replacing it would require rewriting the
  superset-proxy integration layer.
- **Decision:** Accepted as a known transitive exception. This project's own source code does
  not import or call any `org.json` class. The "Good, not Evil" clause applies only to use of
  `org.json` itself, and is widely considered legally unenforceable. This exception must be
  re-evaluated if `socket.io-client` is upgraded or replaced, or if a clean alternative
  becomes available.
- **Documented in:** `NOTICE.md`, `superset-proxy/NOTICE.md`
