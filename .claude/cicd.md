# CI/CD Pipeline - Commit Stage

## Overview
The commit stage is the first pipeline in the Budget OK backend CI/CD process. It runs on every push to `main` and on pull requests, ensuring code quality and creating deployable Docker images.

## Workflow: `commit-stage-backend.yml`

The workflow is defined in `.github/workflows/commit-stage-backend.yml` and consists of two sequential jobs:

### Triggers
The pipeline runs when:
- **Push to main**: Any changes pushed directly to the `main` branch
- **Pull Request**: Any PR targeting the `main` branch
- **Workflow Dispatch**: Manual trigger via GitHub Actions UI

**Path Filter**: Both triggers only activate when changes affect:
- `backend/**` (any backend code changes)
- `.github/workflows/commit-stage-backend.yml` (workflow file itself)

### Concurrency Control
```yaml
concurrency:
  group: commit-stage-backend-${{ github.ref }}
  cancel-in-progress: true
```
- Only one workflow runs per branch at a time
- New pushes automatically cancel previous in-progress runs
- Prevents duplicate builds and resource waste

---

## Job Details

### Job 1: Build
**Purpose**: Compile code, run tests, and create Docker image

#### Steps:

1. **Checkout Repository**
   - Uses GitHub's standard checkout action (v4)
   - Clones the repository code

2. **Setup Java**
   - Java version: 25 (Temurin distribution)
   - Enables Maven caching for faster builds
   - Caches downloaded dependencies

3. **Make Maven Wrapper Executable**
   - Grants execute permissions to `./mvnw` script (Unix-like systems)
   - Required for the Maven wrapper to run

4. **Build with Maven**
   ```bash
   ./mvnw clean package
   ```
   - **clean**: Removes previous build artifacts
   - **package**: Compiles code, runs tests, creates JAR artifact
   - Runs all JUnit 5 tests with Testcontainers (PostgreSQL, Kafka)
   - Produces: `backend/target/budgetok-backend-*.jar`

5. **Publish Docker Image**
   - Action: `optivem/publish-docker-image-action@v1`
   - Registry: GitHub Container Registry (`ghcr.io`)
   - Creates two image tags:
     - `latest`: Points to most recent build on `main`
     - Commit SHA: Unique identifier for specific commit
   - Uses `backend/Dockerfile` for image configuration
   - Authenticates with `GITHUB_TOKEN` secret

#### Outputs
The build job produces outputs used by the summary job:
- `image-latest-url`: Full URL to the `latest` tagged image
- `image-digest-url`: URL to the commit SHA tagged image

**Permissions**:
- `contents: read` - Read repository code
- `packages: write` - Push Docker images to GitHub Container Registry

---

### Job 2: Summary
**Purpose**: Report pipeline results and provide feedback

**Dependency**: Runs after the `build` job completes (uses `needs: build`)

**Condition**: `if: always()` - Runs regardless of build success/failure

#### Steps:

1. **Commit Stage Summary**
   - Action: `optivem/summarize-commit-stage-action@v1`
   - **Inputs**:
     - `stage-result`: Build job result (success/failure/skipped)
     - `success-artifact-url`: Docker image URL on success
   - **Purpose**: Creates a summary comment on PRs or job summary on pushes
   - Provides visibility into the build result and deployed image

---

## Pipeline Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                    GitHub Event Trigger                      │
│  • Push to main                                              │
│  • Pull Request to main                                      │
│  • Manual workflow dispatch                                  │
│  (Path filter: backend/** or workflow file)                 │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
         ┌───────────────────────────────┐
         │  Concurrency Control          │
         │  (Cancel previous runs)        │
         └────────────┬──────────────────┘
                      │
                      ▼
         ┌───────────────────────────────┐
         │      BUILD JOB                │
         │  (ubuntu-latest)              │
         └───────────┬─────────────────────┘
                     │
         ┌───────────▼──────────────┐
         │ 1. Checkout Code         │
         └───────────┬──────────────┘
                     │
         ┌───────────▼──────────────┐
         │ 2. Setup Java 25         │
         │    (with Maven cache)    │
         └───────────┬──────────────┘
                     │
         ┌───────────▼──────────────┐
         │ 3. Make mvnw Executable  │
         └───────────┬──────────────┘
                     │
         ┌───────────▼──────────────────────────┐
         │ 4. Build: ./mvnw clean package      │
         │    • Compile                        │
         │    • Run Tests (JUnit 5)            │
         │    • Create JAR                     │
         │    • Testcontainers (DB/Kafka)      │
         └───────────┬────────────────────────┘
                     │
         ┌───────────▼──────────────────────────┐
         │ 5. Publish Docker Image to GHCR      │
         │    • Image: ghcr.io/[repo]/backend  │
         │    • Tags: latest, [commit-sha]     │
         │    • Auth: GITHUB_TOKEN             │
         └──┬──────────────────────────────────┘
            │
            └─────────────────────────┐
                                      │
                                  Output:
                                  • image-latest-url
                                  • image-digest-url
                                      │
                    ┌─────────────────▼──────────────┐
                    │    SUMMARY JOB                 │
                    │  (always runs, depends: build) │
                    └──────────────┬─────────────────┘
                                   │
                    ┌──────────────▼────────────────┐
                    │ Create Stage Summary          │
                    │ • Report Build Result         │
                    │ • Include Docker Image URL    │
                    │ • Post to PR or Job Summary   │
                    └──────────────┬────────────────┘
                                   │
                                   ▼
                    ┌─────────────────────────────┐
                    │  Pipeline Complete          │
                    │  Ready for Deployment       │
                    └─────────────────────────────┘
```

---

## Key Features

### 1. **Fast Feedback**
- Maven caching reduces build time
- Concurrent jobs run in parallel where possible
- Quick notification via summary action

### 2. **Automation**
- Fully automated compilation, testing, and image building
- No manual steps required
- Container registry integration for easy deployment

### 3. **Quality Assurance**
- All tests run automatically (JUnit 5 + Testcontainers)
- Database and message queue tests included
- Prevents broken code from reaching main

### 4. **Docker Integration**
- Automatic Docker image creation on every build
- Multi-tag strategy (latest + commit SHA)
- Ready for container orchestration (Kubernetes, Docker Compose)

### 5. **Pull Request Feedback**
- Summary action comments on PRs with results
- Clear visibility before code is merged
- Easy access to built Docker image URL

---

## Environment & Permissions

- **Runtime**: Ubuntu Latest (GitHub-hosted runner)
- **Java**: Version 25 (Temurin)
- **Build Tool**: Maven with wrapper
- **Container Registry**: GitHub Container Registry (GHCR)
- **Authentication**: GITHUB_TOKEN (automatic, no manual setup needed)

---

## Artifacts & Outputs

| Artifact | Location | Purpose |
|----------|----------|---------|
| JAR File | `backend/target/budgetok-backend-*.jar` | Compiled application |
| Docker Image (latest) | `ghcr.io/[org]/budget-ok-backend/backend:latest` | Latest build |
| Docker Image (SHA) | `ghcr.io/[org]/budget-ok-backend/backend:[commit-sha]` | Specific commit |

---

## Next Steps in the Pipeline

After the commit stage completes successfully:
1. Manual or automatic deployment to staging/production environments
2. Integration tests against live services
3. Performance testing
4. Security scanning
5. Production deployment (if applicable)

See additional workflow files in `.github/workflows/` for complete pipeline stages.
