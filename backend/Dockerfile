# ── Build stage ──────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies separately from source code
COPY pom.xml ./
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q
RUN mvn dependency:copy-dependencies -DoutputDirectory=target/dependency -DincludeScope=runtime -q

# ── Run stage ─────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Tesseract for OCR (optional, app works without it). Ubuntu jammy retired the
# `chromium` apt package (transitional stub -> snap, unusable in a container),
# so SeleniumScraper no longer gets a system browser here — it fails its
# ChromeDriver init gracefully and the router falls through to other scrapers.
RUN apt-get update \
 && apt-get install -y --no-install-recommends tesseract-ocr tesseract-ocr-eng \
 && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/dependency ./dependency

# Playwright's default browser set plus OS libraries. Only Chromium is used by
# the scraper code, but Playwright validates all three at runtime and silently
# downloads whichever are missing on first use — pre-installing all of them
# here avoids a multi-minute download happening inside a live request.
RUN java -cp "dependency/*" com.microsoft.playwright.CLI install --with-deps \
 && rm -rf dependency

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
