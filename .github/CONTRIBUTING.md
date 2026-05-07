# Contributing to data-viz-api

Thank you for your interest in contributing. This project is maintained by [Development Gateway](https://www.developmentgateway.org/) and welcomes contributions from the community.

## Table of Contents

- [Development Setup](#development-setup)
- [Branching Model](#branching-model)
  - [Long-running project branches](#long-running-project-branches)
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
- Create a branch off `main` for every change, using a prefix that matches the Conventional Commits type:
  - `feat/short-description`
  - `fix/short-description`
  - `chore/short-description`
  - `docs/short-description`
  - `refactor/short-description`
- For long-running project integrations use `project/short-description` (see below).
- Do not push directly to `main`.

### Long-running project branches

Projects that contribute work incrementally over time can maintain a `project/` branch and merge into `main` via PR when ready. Use a generic description with no client name or internal identifier — e.g. `project/superset-embedded-charts`, `project/multilingual-api`. All commits on a `project/` branch must follow the same conventions as any other branch. Only generic, reusable code belongs here — client-specific customisations must stay in the project's own private repository.

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

This project follows [Conventional Commits](https://www.conventionalcommits.org/). Use a prefix that reflects the nature of the change:

| Prefix | When to use |
|--------|-------------|
| `feat:` | New endpoint, service, or user-facing feature |
| `fix:` | Bug fix |
| `chore:` | Dependency update, tooling, config |
| `docs:` | Documentation only |
| `refactor:` | Code restructure with no behaviour change |
| `ci:` | CI/CD workflow changes |

For breaking changes append `!` to the prefix, and add a `BREAKING CHANGE:` footer in the commit body:

```
feat(api-gateway)!: rename dataset endpoint path

BREAKING CHANGE: /api/datasets is now /api/v2/datasets
```

Examples:
```
feat(superset-proxy): add cache warm-up endpoint
fix(api-gateway): correct routing for dataset endpoints
docs: update getting started guide
chore: upgrade spring-boot to 3.4.1
ci: add checkstyle step to test-pr workflow
```

---

## Opening a Pull Request

**External contributors:** fork the repo, create a branch off `main` on your fork, then open a PR.

**Organisation members:** create a branch directly in this repo off `main` — forking is not required.

1. Create a branch off `main` (see [Branching Model](#branching-model))
2. Make your changes and ensure all tests pass
3. Ensure `mvn checkstyle:check` passes locally — CI runs the same check
4. Open a PR against `main` with a clear description of what changed and why
5. At least one maintainer approval is required before merging
6. All CI checks must pass

Do not introduce hardcoded credentials, internal URLs, client-specific identifiers, or PII. Ensure any new dependency has an Apache-2.0-compatible license.

---

## License

By contributing to this project, you agree that your contributions will be licensed under the same license as the project: **Apache-2.0**.

---

## Security

Please do not report security vulnerabilities through public GitHub issues. See [SECURITY.md](SECURITY.md) for the responsible disclosure process.
