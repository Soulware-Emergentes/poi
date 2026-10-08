package lat.soulware.poi.shared.interfaces.rest.security;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Admits a request only with a bearer token staff issued for poi: signed with one of the keys staff
 * publishes, naming staff as its issuer, and addressed to poi. The issuer and the audience come from
 * {@code spring.security.oauth2.resourceserver.jwt.*}, which Boot turns into the decoder checking them.
 *
 * <p>Two kinds of token are admitted, told apart by {@code token_use}:
 *
 * <ul>
 *   <li>A service's, for the task endpoints other systems of the program call, such as sgt: those
 *       under {@code /api/v1/tasks/}. Reads need the {@code tasks.read} scope and writes
 *       {@code tasks.write}, so a service granted only reads cannot change a task.</li>
 *   <li>A person's, for every other endpoint, {@code /api/v1/tasks} itself included, where people
 *       define and list tasks. Which role it needs is checked where the endpoint turns the
 *       {@link Caller} into the actor it records.</li>
 * </ul>
 *
 * <p>A request without a usable token is answered by {@link UnauthenticatedEntryPoint}. Stateless,
 * so there is no session to forge a request against and no CSRF token either.
 *
 * <p>The OpenAPI document and the Swagger UI are the one exception, answered without a token, and
 * only served at all where {@code API_DOCS_ENABLED} is on.
 */
@Configuration
public class TokenSecurityConfiguration {

    private static final String TOKEN_USE_CLAIM = "token_use";
    private static final String PERSON_TOKEN_USE = "user";
    private static final String SERVICE_TOKEN_USE = "service";
    private static final String TASKS = "/api/v1/tasks";
    private static final String TASK_ENDPOINTS = "/api/v1/tasks/**";

    @Bean
    SecurityFilterChain tokenSecurity(HttpSecurity http, UnauthenticatedEntryPoint entryPoint) throws Exception {
        return http.csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(TASKS).access(TokenSecurityConfiguration.personTokens())
                .requestMatchers(HttpMethod.GET, TASK_ENDPOINTS).hasAuthority("SCOPE_tasks.read")
                .requestMatchers(TASK_ENDPOINTS).hasAuthority("SCOPE_tasks.write")
                .anyRequest().access(TokenSecurityConfiguration.personTokens())
            )
            .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults())
                .authenticationEntryPoint(entryPoint)
            )
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(entryPoint))
            .build();
    }

    /** Refuses an access token issued neither to a person nor to a service, which Boot adds to the decoder it builds. */
    @Bean
    OAuth2TokenValidator<Jwt> personAndServiceTokensOnly() {
        Set<String> admitted = Set.of(PERSON_TOKEN_USE, SERVICE_TOKEN_USE);

        return new JwtClaimValidator<String>(TOKEN_USE_CLAIM, admitted::contains);
    }

    /** Lets the frontend call poi from the browser. */
    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${poi.web.origin}") String webOrigin) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(webOrigin));
        cors.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cors);
        return source;
    }

    /** Grants a request carrying a person's token, and refuses one carrying a service's. */
    private static AuthorizationManager<RequestAuthorizationContext> personTokens() {
        return (Supplier<? extends Authentication> authentication, RequestAuthorizationContext context) -> {
            boolean carriesAPersonToken = authentication.get().getPrincipal() instanceof Jwt token
                && PERSON_TOKEN_USE.equals(token.getClaimAsString(TOKEN_USE_CLAIM));

            return new AuthorizationDecision(carriesAPersonToken);
        };
    }
}
