package br.com.elton.integrationtests.controller.withyaml;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import br.com.elton.configs.TestConfigs;
import br.com.elton.data.vo.v1.security.TokenVO;
import br.com.elton.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.elton.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.elton.integrationtests.vo.AccountCredentialsVO;
import br.com.elton.integrationtests.vo.PersonVO;
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
public class PersonControllerYamlTest extends AbstractIntegrationTest {
	private static RequestSpecification specification;
	private static YMLMapper mapper;
	private static PersonVO person;

	@BeforeAll
	public static void setup() {
		mapper = new YMLMapper();
		person = new PersonVO();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		var persistedPerson = RestAssured.given().spec(specification)
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML).body(person, mapper)
				.when().post().then().statusCode(200).extract().body().as(PersonVO.class, mapper);
		person = persistedPerson;
		Assertions.assertNotNull(persistedPerson);
		Assertions.assertNotNull(persistedPerson.getId());
		Assertions.assertNotNull(persistedPerson.getFirstName());
		Assertions.assertNotNull(persistedPerson.getLastName());
		Assertions.assertNotNull(persistedPerson.getAddress());
		Assertions.assertNotNull(persistedPerson.getGender());
		Assertions.assertTrue(persistedPerson.getId() > 0);
		Assertions.assertEquals("Nelson", persistedPerson.getFirstName());
		Assertions.assertEquals("Piquet", persistedPerson.getLastName());
		Assertions.assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		Assertions.assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		var persistedPerson = RestAssured.given().spec(specification)
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ELTON).pathParam("id", person.getId())
				.when().get("{id}").then().statusCode(200).extract().body().as(PersonVO.class, mapper);
		person = persistedPerson;
		Assertions.assertNotNull(persistedPerson);
		Assertions.assertNotNull(persistedPerson.getId());
		Assertions.assertNotNull(persistedPerson.getFirstName());
		Assertions.assertNotNull(persistedPerson.getLastName());
		Assertions.assertNotNull(persistedPerson.getAddress());
		Assertions.assertNotNull(persistedPerson.getGender());
		Assertions.assertEquals(person.getId(), persistedPerson.getId());
		Assertions.assertEquals("Nelson", persistedPerson.getFirstName());
		Assertions.assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		Assertions.assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		Assertions.assertEquals("Male", persistedPerson.getGender());
	}

	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasília - DF - Brasil");
		person.setGender("Male");
	}

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO accountCredentialsVO = new AccountCredentialsVO("leandro", "admin123");
		var accessToken = RestAssured.given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.basePath("/auth/signin").port(TestConfigs.SERVER_PORT).contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML).body(accountCredentialsVO, mapper).when().post().then()
				.statusCode(200).extract().body().as(TokenVO.class, mapper).getAccessToken();
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1").setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		person.setLastName("Piquet Souto Maior");
		var persistedPerson = RestAssured.given().spec(specification)
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML).body(person, mapper)
				.when().post().then().statusCode(200).extract().body().as(PersonVO.class, mapper);
		person = persistedPerson;
		Assertions.assertNotNull(persistedPerson);
		Assertions.assertNotNull(persistedPerson.getId());
		Assertions.assertNotNull(persistedPerson.getFirstName());
		Assertions.assertNotNull(persistedPerson.getLastName());
		Assertions.assertNotNull(persistedPerson.getAddress());
		Assertions.assertNotNull(persistedPerson.getGender());
		Assertions.assertEquals(person.getId(), persistedPerson.getId());
		Assertions.assertEquals("Nelson", persistedPerson.getFirstName());
		Assertions.assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		Assertions.assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		Assertions.assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(4)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		RestAssured.given().spec(specification)
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", person.getId()).when().delete("{id}").then().statusCode(204);
	}

	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		var content = RestAssured.given().spec(specification)
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML).when().get().then()
				.statusCode(200).extract().body().as(PersonVO[].class, mapper);
		List<PersonVO> personVOs = Arrays.asList(content);
		PersonVO foundPersonOne = personVOs.get(0);
		Assertions.assertNotNull(foundPersonOne.getId());
		Assertions.assertNotNull(foundPersonOne.getFirstName());
		Assertions.assertNotNull(foundPersonOne.getLastName());
		Assertions.assertNotNull(foundPersonOne.getAddress());
		Assertions.assertNotNull(foundPersonOne.getGender());
		Assertions.assertEquals(1, foundPersonOne.getId());
		Assertions.assertEquals("Ayrton", foundPersonOne.getFirstName());
		Assertions.assertEquals("Senna", foundPersonOne.getLastName());
		Assertions.assertEquals("São Paulo", foundPersonOne.getAddress());
		Assertions.assertEquals("Male", foundPersonOne.getGender());
		PersonVO foundPersonSix = personVOs.get(5);
		Assertions.assertNotNull(foundPersonSix.getId());
		Assertions.assertNotNull(foundPersonSix.getFirstName());
		Assertions.assertNotNull(foundPersonSix.getLastName());
		Assertions.assertNotNull(foundPersonSix.getAddress());
		Assertions.assertNotNull(foundPersonSix.getGender());
		Assertions.assertEquals(9, foundPersonSix.getId());
		Assertions.assertEquals("Nelson", foundPersonSix.getFirstName());
		Assertions.assertEquals("Mvezo", foundPersonSix.getLastName());
		Assertions.assertEquals("Mvezo – South Africa", foundPersonSix.getAddress());
		Assertions.assertEquals("Male", foundPersonSix.getGender());
	}

	@Test
	@Order(6)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder().setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT).addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL)).build();
		RestAssured.given().spec(specificationWithoutToken).contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML).when().get().then().statusCode(403);
	}
}
