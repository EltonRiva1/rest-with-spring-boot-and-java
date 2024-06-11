package br.com.elton.integrationtests.swagger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import br.com.elton.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.RestAssured;

@SpringBootApplication
@SpringBootTest(classes = SwaggerIntegrationTest.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SwaggerIntegrationTest extends AbstractIntegrationTest {
	@LocalServerPort
	private String port;

	@BeforeEach
	void setUp() {
		RestAssured.port = Integer.parseInt(port);
	}

	@Test
	void connectionEstablished() {
		Assertions.assertTrue(Initializer.mysql.isCreated());
		Assertions.assertTrue(Initializer.mysql.isRunning());
	}

	@Test
	void shouldDisplaySwaggerUiPage() {
		var content = RestAssured.given().basePath("/swagger-ui/index.html").get().then().statusCode(200).extract()
				.body().asString();
		Assertions.assertTrue(content.contains("Swagger UI"));
	}

}
