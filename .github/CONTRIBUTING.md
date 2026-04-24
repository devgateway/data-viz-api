# Contributing to data-viz-api

Thank you for your interest in contributing. This project is maintained by [Development Gateway](https://www.developmentgateway.org/) and welcomes contributions from the community.

## Table of Contents

- [Development Setup](#development-setup)
- [Branching Model](#branching-model)
- [Making Changes](#making-changes)
- [Code Style](#code-style)
- [Commit Messages](#commit-messages)
- [Opening a Pull Request](#opening-a-pull-request)
- [License](#license)
- [Security](#security)

---

## Development Setup

### Prerequisites

- **Java 21+** (JDK) — [Download](https://adoptium.net/)
- **Maven 3.8.9+** — [Download](https://maven.apache.org/download.cgi)
- **PostgreSQL 12+**
- **Redis 6+** (production profile)
- **Docker** (optional, for running the full stack)

### Install and build

```bash
git clone git@github.com:devgateway/data-viz-api.git
cd data-viz-api
mvn clean install
```

### Run with Docker Compose

```bash
docker compose up -d
```

### Start services manually (in order)

```bash
# 1. Eureka Registry
cd registry && mvn spring-boot:run

# 2. API Security
cd api-security && mvn spring-boot:run

# 3. Superset Proxy
cd superset-proxy && mvn spring-boot:run

# 4. API Gateway
cd api-gateway && mvn spring-boot:run
```

Access the Eureka dashboard at http://localhost:8761 and the API at http://localhost:8080.

### Secrets scanning (pre-commit hook)

This project uses [Gitleaks](https://github.com/gitleaks/gitleaks) to prevent secrets from being accidentally committed. Install the hook after cloning:

```bash
pip install pre-commit
pre-commit install
```

---

## Branching Model

- `main` is the stable branch and the base for all pull requests.
- Create a feature branch off `main` for every change: `task/short-description` or `fix/short-description`.
- Do not push directly to `main`.

---

## Making Changes

1. Fork the repository and create a branch off `main`.
2. Make your changes and ensure all tests pass.
3. Follow the [commit message convention](#commit-messages).
4. Open a pull request against `main`.

---

## Code Style

Code quality is enforced with the Maven Checkstyle Plugin:

```bash
mvn checkstyle:check
```

Please ensure your changes pass Checkstyle before opening a PR.

---

## Commit Messages

This project follows [Conventional Commits](https://www.conventionalcommits.org/). Version bumps are derived automatically from commit prefixes:

| Prefix | Version bump |
|--------|-------------|
| `fix:` | patch |
| `feat:` | minor |
| `BREAKING CHANGE:` | major |

Examples:
```
feat(superset-proxy): add cache warm-up endpoint
fix(api-gateway): correct routing for dataset endpoints
docs: update getting started guide
```

---

## Opening a Pull Request

- Target branch: `main`
- At least one reviewer approval is required before merging.
- All CI checks must pass.
- Use a conventional commit prefix in the PR title — it determines the version bump.
- Do not introduce hardcoded credentials, internal URLs, client-specific identifiers, or PII.
- Ensure any new dependency has an Apache-2.0-compatible license.

---

## License

By contributing to this project, you agree that your contributions will be licensed under the same license as the project: **Apache-2.0**.

---

## Security

Please do not report security vulnerabilities through public GitHub issues. See [SECURITY.md](SECURITY.md) for the responsible disclosure process.
