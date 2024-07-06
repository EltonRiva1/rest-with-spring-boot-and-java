package br.com.elton.integrationtests.controller.withxml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.elton.configs.TestConfigs;
import br.com.elton.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.elton.integrationtests.vo.AccountCredentialsVO;
import br.com.elton.integrationtests.vo.TokenVO;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerXmlTest extends AbstractIntegrationTest {
	private static TokenVO tokenVO;

	@Test
	@Order(1)
	public void testSignin() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO accountCredentialsVO = new AccountCredentialsVO("leandro", "admin123");
		tokenVO = RestAssured.given().basePath("/auth/signin").port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_XML).body(accountCredentialsVO).when().post().then()
				.statusCode(200).extract().body().as(TokenVO.class);
		Assertions.assertNotNull(tokenVO.getAccessToken());
		Assertions.assertNotNull(tokenVO.getRefreshToken());
	}

	@Test
	@Order(2)
	public void testRefresh() throws JsonMappingException, JsonProcessingException {
		var newTokenVO = RestAssured.given().basePath("/auth/refresh").port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_XML).pathParam("username", tokenVO.getUsername())
				.header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken()).when()
				.put("{username}").then().statusCode(200).extract().body().as(TokenVO.class);
		Assertions.assertNotNull(newTokenVO.getAccessToken());
		Assertions.assertNotNull(newTokenVO.getRefreshToken());
	}
}
