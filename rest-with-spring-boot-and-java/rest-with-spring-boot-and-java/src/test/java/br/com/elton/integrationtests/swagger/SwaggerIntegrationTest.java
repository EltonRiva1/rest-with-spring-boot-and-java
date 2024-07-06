package br.com.elton.integrationtests.swagger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import br.com.elton.configs.TestConfigs;
import br.com.elton.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

	@Test
	void shouldDisplaySwaggerUiPage() {
		var content = RestAssured.given().basePath("/swagger-ui/index.html").port(TestConfigs.SERVER_PORT).when().get()
				.then().statusCode(200).extract().body().asString();
		Assertions.assertTrue(content.contains("Swagger UI"));
	}
}