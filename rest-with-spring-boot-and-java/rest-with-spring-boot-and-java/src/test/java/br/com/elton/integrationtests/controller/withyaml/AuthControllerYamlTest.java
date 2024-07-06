package br.com.elton.integrationtests.controller.withyaml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.elton.configs.TestConfigs;
import br.com.elton.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.elton.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.elton.integrationtests.vo.AccountCredentialsVO;
import br.com.elton.integrationtests.vo.TokenVO;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerYamlTest extends AbstractIntegrationTest {
	private static TokenVO tokenVO;
	private static YMLMapper mapper;

	@BeforeAll
	public static void setup() {
		mapper = new YMLMapper();
	}

	@Test
	@Order(1)
	public void testSignin() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO accountCredentialsVO = new AccountCredentialsVO("leandro", "admin123");
		RequestSpecification specification = new RequestSpecBuilder().addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL)).build();
		tokenVO = RestAssured.given().spec(specification)
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML).basePath("/auth/signin").port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_YML).body(accountCredentialsVO, mapper).when().post().then()
				.statusCode(200).extract().body().as(TokenVO.class, mapper);
		Assertions.assertNotNull(tokenVO.getAccessToken());
		Assertions.assertNotNull(tokenVO.getRefreshToken());
	}

	@Test
	@Order(2)
	public void testRefresh() throws JsonMappingException, JsonProcessingException {
		var newTokenVO = RestAssured.given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML).basePath("/auth/signin").basePath("/auth/refresh")
				.port(TestConfigs.SERVER_PORT).contentType(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("username", tokenVO.getUsername())
				.header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken()).when()
				.put("{username}").then().statusCode(200).extract().body().as(TokenVO.class, mapper);
		Assertions.assertNotNull(newTokenVO.getAccessToken());
		Assertions.assertNotNull(newTokenVO.getRefreshToken());
	}
}
