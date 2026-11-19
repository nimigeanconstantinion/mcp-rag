package mycode.onlineshopspring.integration;

import mycode.onlineshopspring.auth.dto.AuthResponse;
import mycode.onlineshopspring.auth.dto.LoginRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class McpTokenIntegrationTest {

    private static final String MCP_TOKEN = "test-mcp-token";
    private static final String INITIALIZE = """
            {"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-06-18","capabilities":{},"clientInfo":{"name":"test","version":"0.1"}}}""";

    private static ResponseEntity<String> postInitialize(TestRestTemplate restTemplate, String bearer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM));
        if (bearer != null) {
            headers.setBearerAuth(bearer);
        }
        return restTemplate.exchange("/mcp", HttpMethod.POST, new HttpEntity<>(INITIALIZE, headers), String.class);
    }

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
            properties = "app.mcp.api-token=" + MCP_TOKEN)
    @ActiveProfiles("test")
    class WithToken {

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        void mcp_withoutHeader_returns401WithJsonHint() {
            // ACT
            ResponseEntity<Map> response = restTemplate.postForEntity("/mcp", INITIALIZE, Map.class);

            // ASSERT
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().get("message").toString().contains("Authorization: Bearer"));
        }

        @Test
        void mcp_withWrongToken_returns401() {
            assertEquals(HttpStatus.UNAUTHORIZED, postInitialize(restTemplate, "wrong-token").getStatusCode());
        }

        @Test
        void mcp_withCorrectToken_returns200() {
            assertEquals(HttpStatus.OK, postInitialize(restTemplate, MCP_TOKEN).getStatusCode());
        }

        @Test
        void api_withMcpToken_staysOnJwtAndReturns401() {
            // ARRANGE
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(MCP_TOKEN);

            // ACT
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/products", HttpMethod.GET, new HttpEntity<>(headers), String.class);

            // ASSERT
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

        @Test
        void api_withJwt_isNotBlockedByMcpToken() {
            // ARRANGE
            AuthResponse auth = restTemplate.postForObject(
                    "/api/auth/login", new LoginRequest("admin@shop.com", "admin1234"), AuthResponse.class);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(auth.accessToken());

            // ACT
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/products", HttpMethod.GET, new HttpEntity<>(headers), String.class);

            // ASSERT
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
            properties = "app.mcp.api-token=")
    @ActiveProfiles("test")
    class WithoutToken {

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        void mcp_withoutHeader_staysOpen() {
            assertEquals(HttpStatus.OK, postInitialize(restTemplate, null).getStatusCode());
        }
    }
}
