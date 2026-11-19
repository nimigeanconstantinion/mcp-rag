package mycode.onlineshopspring.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Guards /mcp with a static API token. When the token is blank, /mcp stays open.
 * Not a @Component, so it runs only inside the security chain, not as a servlet filter.
 */
public class McpTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final byte[] expectedToken;
    private final ObjectMapper objectMapper;

    public McpTokenFilter(String apiToken, ObjectMapper objectMapper) {
        this.expectedToken = apiToken == null ? new byte[0] : apiToken.trim().getBytes(StandardCharsets.UTF_8);
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (expectedToken.length == 0 || HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !(path.equals("/mcp") || path.startsWith("/mcp/"));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            byte[] presented = authHeader.substring(BEARER_PREFIX.length()).trim().getBytes(StandardCharsets.UTF_8);
            if (MessageDigest.isEqual(presented, expectedToken)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", "MCP API token required. Send the header 'Authorization: Bearer <MCP_API_TOKEN>'.");
        body.put("path", request.getRequestURI());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
