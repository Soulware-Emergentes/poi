package lat.soulware.poi.support;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

/**
 * Base class for tests that need a real Postgres with this project's two roles.
 *
 * <p>One container is started for the whole test suite and torn down by Ryuk when the JVM exits.
 * It runs {@code scripts/db/setup-roles.sql} verbatim, so the script that provisions a real
 * deployment is the same one every test runs against - if it breaks, the build breaks.
 *
 * <p>The datasource properties in {@code application.properties} have no defaults on purpose:
 * a missing {@code DB_URL} in production must fail loudly. Tests therefore supply real values
 * for a real database rather than weakening that config.
 */
public abstract class PostgresIntegrationTest {

    // Thse have to match the setup-roles.sql script
    protected static final String DATABASE_NAME = "poi";
    protected static final String MIGRATOR_USERNAME = "poi_migrator";
    protected static final String APP_USERNAME = "poi_app";

    // WARNING: DO NOT let these match your actual .env passwords
    private static final String MIGRATOR_PASSWORD = "MIGRATOR_RANDOM_PASSWORD";
    private static final String APP_PASSWORD = "APP_RANDOM_PASSWORD";


    private static final Path SETUP_ROLES_SCRIPT = Path.of("scripts", "db", "setup-roles.sql");
    private static final String SCRIPT_PATH_IN_CONTAINER = "/tmp/setup-roles.sql";

    private static final PostgreSQLContainer POSTGRES = startProvisionedContainer();

    /**
     * Starts the container and provisions its roles before any subclass runs. Assigned to a static
     * field so one container is shared across every integration test rather than started per class.
     */
    private static PostgreSQLContainer startProvisionedContainer() {
        MountableFile setupScript = MountableFile.forHostPath(SETUP_ROLES_SCRIPT.toAbsolutePath());

        PostgreSQLContainer container = new PostgreSQLContainer("postgres:17-alpine");
        container.withCopyFileToContainer(setupScript, SCRIPT_PATH_IN_CONTAINER);

        container.start();
        createRoles(container);

        return container;
    }

    /**
     * Runs setup-roles.sql through psql inside the container. It has to be psql rather than JDBC
     * because the script uses meta-commands ({@code \set}, {@code \connect}) that only psql
     * understands, and {@code CREATE DATABASE} cannot run inside a transaction block.
     */
    private static void createRoles(PostgreSQLContainer container) {
        String psqlCommand = """
            POI_MIGRATOR_PASSWORD='%s' POI_APP_PASSWORD='%s' \
            psql -v ON_ERROR_STOP=1 -U %s -d %s -f %s"""
            .formatted(
                MIGRATOR_PASSWORD,
                APP_PASSWORD,
                container.getUsername(),
                container.getDatabaseName(),
                SCRIPT_PATH_IN_CONTAINER
            );

        try {
            Container.ExecResult result = container.execInContainer("sh", "-c", psqlCommand);
            boolean scriptFailed = result.getExitCode() != 0;

            if (scriptFailed) {
                throw new IllegalStateException(
                    "setup-roles.sql failed with exit code %d:%n%s"
                        .formatted(result.getExitCode(), result.getStderr())
                );
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not run setup-roles.sql in the container", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while running setup-roles.sql", e);
        }
    }

    /** JDBC URL of the provisioned {@code poi} database, whichever role connects to it. */
    protected static String jdbcUrl() {
        Integer mappedPort = POSTGRES.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);

        return "jdbc:postgresql://%s:%d/%s".formatted(POSTGRES.getHost(), mappedPort, DATABASE_NAME);
    }

    @DynamicPropertySource
    /**
     * Points Spring at the container after it has been assigned its port. The two roles are handed
     * out separately: Flyway migrates as the migrator, the application connects as the app role,
     * which is what makes the privilege separation testable at all.
     */
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.url", PostgresIntegrationTest::jdbcUrl);
        registry.add("spring.flyway.user", () -> MIGRATOR_USERNAME);
        registry.add("spring.flyway.password", () -> MIGRATOR_PASSWORD);

        registry.add("spring.datasource.url", PostgresIntegrationTest::jdbcUrl);
        registry.add("spring.datasource.username", () -> APP_USERNAME);
        registry.add("spring.datasource.password", () -> APP_PASSWORD);
        registry.add("API_DOCS_ENABLED", () -> "true");
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", TestTokens::issuer);
        registry.add("spring.security.oauth2.resourceserver.jwt.audiences", () -> TestTokens.AUDIENCE);
        registry.add("POI_WEB_ORIGIN", () -> "http://localhost:5174");
    }
}
