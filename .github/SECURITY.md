# Security Policy

## Supported Versions

Only the latest release on the `main` branch receives security fixes.

| Version | Supported |
|---------|-----------|
| Latest (main) | Yes |
| Older releases | No |

## Reporting a Vulnerability

**Please do not report security vulnerabilities through public GitHub issues, pull requests, or discussions.**

To report a vulnerability privately, use one of the following methods:

### Option 1 — GitHub Private Vulnerability Reporting (preferred)

Use GitHub's built-in [private vulnerability reporting](https://github.com/devgateway/data-viz-api/security/advisories/new) feature. Your report will be visible only to repository maintainers.

### Option 2 — Email

Send a description of the vulnerability to the Development Gateway maintainers at <info@developmentgateway.org>. Include:

- A description of the vulnerability and its potential impact
- Steps to reproduce or a proof-of-concept
- Affected versions or components (api-gateway, api-security, commons, registry, superset-proxy)
- Any suggested mitigations

## What to Expect

- **Acknowledgement:** within 5 business days of receipt
- **Status update:** within 15 business days
- **Fix timeline:** depends on severity; critical issues are prioritized
- **Credit:** reporters will be credited in the security advisory unless they request anonymity

## Scope

This policy covers vulnerabilities in code maintained in this repository:

- `api-gateway/`
- `api-security/`
- `commons/`
- `registry/`
- `superset-proxy/`

### Out of Scope

- Vulnerabilities in Apache Superset itself — report to the [Apache Security Team](https://www.apache.org/security/).
- PostgreSQL or Redis vulnerabilities — report to their respective projects.
- Vulnerabilities only exploitable with valid admin credentials to the running application.
- Docker base image vulnerabilities — report to the relevant upstream image maintainers.
