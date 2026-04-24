# Release Guide

This repository contains four deployable services, each versioned and tagged independently:

| Service | Project name | Docker image path |
|---|---|---|
| API Gateway | `api-gateway` | `{DOCKER_REGISTRY}/devgateway/data-viz/api-gateway` |
| API Security | `api-security` | `{DOCKER_REGISTRY}/devgateway/data-viz/api-security` |
| API Registry | `api-registry` | `{DOCKER_REGISTRY}/devgateway/data-viz/api-registry` |
| Superset Proxy | `superset-proxy` | `{DOCKER_REGISTRY}/devgateway/data-viz/superset-proxy` |

## Versioning

Tags follow the format `{project-name}@v{semver}`, e.g. `superset-proxy@v0.0.1`.

Version bumps are determined automatically from [Conventional Commit](https://www.conventionalcommits.org/) messages:

| Commit prefix | Bump |
|---|---|
| `fix:` | patch |
| `feat:` | minor |
| `BREAKING CHANGE:` | major |

If no commit carries a conventional prefix, the configured `default_bump` (`patch`) is applied.

---

## Pre-releases

Pre-releases are built and pushed automatically and can also be triggered manually.

### Automatic (on push to `main`)

Each service has its own workflow that watches for changes in its subdirectory:

| Service | Workflow | Trigger path |
|---|---|---|
| API Gateway | `build_and_pre_release_api_gateway.yml` | `api-gateway/**` |
| API Security | `build_and_pre_release_api_security.yml` | `api-security/**` |
| API Registry | `build_and_pre_release_registry.yml` | `registry/**` |
| Superset Proxy | `build_and_pre_release_superset_proxy.yml` | `superset-proxy/**` |

When a PR is merged to `main` and it touches files under a service's directory, that service's pre-release workflow triggers automatically.

### Manual

1. Go to **Actions** → select the workflow for the service (e.g. *Build And Pre Release Superset Proxy*).
2. Click **Run workflow** → select branch `main` → **Run workflow**.

### What it produces

- **Git tag**: `{project-name}@v{next-version}-rc.0` (e.g. `superset-proxy@v0.0.1-rc.0`)
- **GitHub release**: marked as pre-release, no changelog body
- **Docker image tags**:
  - `{image}:{version}-rc.0` (e.g. `superset-proxy:0.0.1-rc.0`)
  - `{image}:dev`

> **Note:** The pre-release version is computed from the latest `{project-name}@v*` tag in the repository. If the latest release is `superset-proxy@v0.0.0`, the next pre-release will be `superset-proxy@v0.0.1-rc.0`.

---

## Releases

Full releases are always triggered manually. All four services are released together in a single run.

### Steps

1. Go to **Actions** → *Build and release All Projects*.
2. Click **Run workflow**.
3. Select the bump type:
   - `patch` — backwards-compatible bug fixes (default)
   - `minor` — new backwards-compatible functionality
   - `major` — breaking changes
4. Click **Run workflow**.

### What it produces (per service)

- **Git tag**: `{project-name}@v{next-version}` (e.g. `superset-proxy@v0.0.1`)
- **GitHub release**: marked as latest, with a generated changelog scoped to that service's directory
- **Docker image tags**:
  - `{image}:{version}` (e.g. `superset-proxy:0.0.1`)
  - `{image}:latest`
  - `{image}:dev`

The `commons` library is also built and deployed to Artifactory as part of the same workflow run.

---

## Tag reference

| Example tag | Meaning |
|---|---|
| `superset-proxy@v0.0.1` | Full release of superset-proxy at version 0.0.1 |
| `superset-proxy@v0.0.1-rc.0` | Pre-release (release candidate) of superset-proxy |
| `api-gateway@v0.0.1` | Full release of api-gateway at version 0.0.1 |

Each service's version history is fully independent. A release of `superset-proxy` does not affect the version of `api-gateway`.
