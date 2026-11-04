# Modernization Plan: Upgrade Java 17 to Java 25 LTS

**Project**: AgendaPro

---

## Technical Framework

- **Language**: Java 17 (`java.version` = 17 in pom.xml)
- **Framework**: Spring Boot 4.1.2 (Spring MVC, Spring Data JPA, Thymeleaf, Validation)
- **Build Tool**: Maven (Maven Wrapper `mvnw`)
- **Database**: H2 (runtime)
- **Key Dependencies**: spring-boot-starter-webmvc, -thymeleaf, -data-jpa, -validation, devtools

---

## Overview

> This modernization upgrades the application's Java version from 17 to Java 25, the latest LTS as of 2026-11-04. The application currently targets Java 17, which is not the latest LTS. After the upgrade:
>
> - The build and runtime target Java 25 for newer language features, performance and extended support.
> - Maven and runtime configuration are updated consistently.
> - Java-version documentation (README) is corrected to match.
>
> Only the JDK/Java upgrade is in scope. Spring Boot 4.1.2 already supports Java 25, so no framework upgrade is needed. Cloud-readiness and Dockerfile findings are excluded.

---

## Migration Impact Summary

| Application | Original Service | New Service | Authentication | Comments |
|-------------|------------------|-------------|----------------|----------|
| AgendaPro   | Java 17          | Java 25 LTS | N/A            | Build/test validated with JDK 25 |

---

## Scope Notes

- Addresses assessment issue: "Java Version is not the latest LTS".
- Excluded: cloud-readiness and Dockerfile findings; no Spring Boot/Framework/Jakarta changes.
- Security/CVE remediation task not added, because the request is limited to the Java Version Upgrade only.
- Headless auto-execution: no clarification questions were required.
