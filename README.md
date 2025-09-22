# 📄 ISYS3001 Assignment 2 – CI/CD ToDo Backend

## 1. Introduction
This project implements a **ToDo backend service** using **Java Spring Boot 3.x**, secured with **JWT authentication**, and deployed with a **CI/CD pipeline on GitHub Actions**.  
The goal was to demonstrate **feature branching, automated pipelines, semantic versioning, and containerized releases**.

---

## 2. Branching & Workflow
A **GitHub Flow branching strategy** was adopted:
- **Feature branches**:
    - `feature/authentication` → implemented user registration & login
    - `feature/todo-crud` → implemented core ToDo CRUD operations
- **Development branch (`dev`)**: integrated features via PRs with build/test checks
- **Main branch (`main`)**: used for production releases only

This ensured isolated development, integration testing on `dev`, and stability on `main`.

---

## 3. CI/CD Pipelines
GitHub Actions pipelines were configured as follows:

### 3.1 Pull Request Pipeline
- Triggered on **PRs to `dev`**
- Executes:
    - `mvn verify` (build + tests)
- Ensures **no code is merged with build/test failures**

### 3.2 Development Release
- Triggered on **merge into `dev`**
- Builds Docker image and pushes to **GitHub Container Registry (GHCR)**
- Tags: `dev-latest`, `dev-<commit-sha>`

### 3.3 Production Release
- Triggered on **merge into `main`** or **version tags (`vX.Y.Z`)**
- Executes:
    - Build & test
    - Build Docker image
    - Push image to GHCR with tags:
        - `prod-latest`
        - `prod-<commit-sha>`
        - `vX.Y.Z` (semantic version)
        - `X.Y` (minor version)

This setup ensures a clear **promotion pipeline** from feature → dev → prod.

---

## 4. Features Implemented
### v0.1.0
- User registration (`/api/auth/register`)
- User login (`/api/auth/login`)
- JWT authentication with secure password hashing
- Initial CI/CD pipelines (PR checks, GHCR release)

### v0.2.0
- Core ToDo CRUD:
    - Create, list, retrieve, update, delete
- Endpoints scoped to the authenticated user
- CI/CD release pipelines tested with semantic versioning
- Unit tests implemented for `TodoService`

---

## 5. Testing
Testing strategy included:
- **Unit tests (JUnit 5, Mockito)**:
    - Verified business logic in `TodoService`
    - Covered success and failure cases (create/list/get/update/delete, user scoping, error handling)
- **Manual testing**:
    - Postman collection (`TodoAPI.postman_collection.json`) with automated token + ID handling
    - Curl scripts for end-to-end API validation

Tests are integrated into the CI pipeline (`mvn test`) and must pass before merging.

---

## 6. Release Management
Semantic versioning was adopted:
- **v0.1.0** – Initial release with authentication
- **v0.2.0** – Added core ToDo features + tests

Each release produces Docker images tagged for traceability:
- `prod-latest`
- `prod-<sha>`
- Versioned (`X.Y.Z`, `X.Y`)

---

## 7. Evidence (Screenshots Provided)
- PR pipeline execution (build/test check)
- Dev release workflow (image pushed to GHCR)
- Prod release workflow (tagged release image)
- GitHub Releases page (v0.1.0, v0.2.0)
- GHCR package list with versioned images
- Postman API test runs
- Unit test results (`mvn test` green)

---

## 8. Reflection
This project highlights the importance of **automation and structured workflow** in modern software delivery:
- CI/CD pipelines provide confidence that code changes are safe and production-ready
- Semantic versioning helps trace releases
- Unit tests ensure reliability and guard against regressions
- Feature branching enables collaborative, incremental development

Overall, the assignment demonstrated **practical DevOps practices** that align with industry standards.

---

## 9. Conclusion
The ToDo backend successfully implements:
- **Authentication and secure APIs**
- **ToDo CRUD features**
- **Automated CI/CD pipelines with GHCR releases**
- **Semantic versioning (v0.1.0, v0.2.0)**
- **Automated unit tests**

This satisfies the assignment requirements for **features, pipelines, releases, testing, and documentation**.

---

✍️ **Author:**  
*Lahiru Radeeshan Gamaralalage*  
ISYS3001 Assignment 2
