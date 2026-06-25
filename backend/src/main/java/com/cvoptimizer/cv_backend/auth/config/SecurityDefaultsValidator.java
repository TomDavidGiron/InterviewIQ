package com.cvoptimizer.cv_backend.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Fails startup if placeholder secrets/credentials are still active in a "prod" profile.
 * Only warns (does not block) outside prod, so local development keeps working
 * without requiring every contributor to set env vars first.
 */
@Component
@Order(Integer.MIN_VALUE)
public class SecurityDefaultsValidator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SecurityDefaultsValidator.class);

    private static final String DEFAULT_JWT_SECRET =
            "dGhpcy1pcy1hLXNlY3JldC1rZXktZm9yLWRldi1vbmx5LXBsZWFzZS1jaGFuZ2UtaXQ=";
    private static final String DEFAULT_DB_PASSWORD = "postgres";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private final Environment environment;

    public SecurityDefaultsValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        boolean usingDefaultJwtSecret = DEFAULT_JWT_SECRET.equals(jwtSecret);
        boolean usingDefaultDbPassword = DEFAULT_DB_PASSWORD.equals(dbPassword);

        if (isProd && (usingDefaultJwtSecret || usingDefaultDbPassword)) {
            throw new IllegalStateException(
                    "Refusing to start with profile 'prod' while using placeholder credentials. "
                            + "Set JWT_SECRET to a unique 256-bit key and DB_PASSWORD to a real password before deploying.");
        }

        if (usingDefaultJwtSecret) {
            log.warn("Using the default development JWT secret. Set the JWT_SECRET environment variable "
                    + "before deploying this application anywhere outside local development.");
        }
        if (usingDefaultDbPassword) {
            log.warn("Using the default 'postgres' database password. Set DB_PASSWORD to a real password "
                    + "before deploying this application anywhere outside local development.");
        }
    }
}
