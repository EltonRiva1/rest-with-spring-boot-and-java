package br.com.elton.integrationtests.controller.withjson;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.elton.configs.TestConfigs;
import br.com.elton.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.elton.integrationtests.vo.AccountCredentialsVO;
import br.com.elton.integrationtests.vo.PersonVO;
import br.com.elton.integrationtests.vo.TokenVO;
import br.com.elton.integrationtests.vo.wrappers.WrapperPersonVO;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {
	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static PersonVO person;

	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		person = new PersonVO();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON).body(person)
				.when().post().then().statusCode(200).extract().body().asString();
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		Assertions.assertNotNull(persistedPerson);
		Assertions.assertNotNull(persistedPerson.getId());
		Assertions.assertNotNull(persistedPerson.getFirstName());
		Assertions.assertNotNull(persistedPerson.getLastName());
		Assertions.assertNotNull(persistedPerson.getAddress());
		Assertions.assertNotNull(persistedPerson.getGender());
		Assertions.assertTrue(persistedPerson.getEnabled());
		Assertions.assertTrue(persistedPerson.getId() > 0);
		Assertions.assertEquals("Nelson", persistedPerson.getFirstName());
		Assertions.assertEquals("Piquet", persistedPerson.getLastName());
		Assertions.assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		Assertions.assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ELTON).pathParam("id", person.getId())
				.when().get("{id}").then().statusCode(200).extract().body().asString();
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		Assertions.assertNotNull(persistedPerson);
		Assertions.assertNotNull(persistedPerson.getId());
		Assertions.assertNotNull(persistedPerson.getFirstName());
		Assertions.assertNotNull(persistedPerson.getLastName());
		Assertions.assertNotNull(persistedPerson.getAddress());
		Assertions.assertNotNull(persistedPerson.getGender());
		Assertions.assertFalse(persistedPerson.getEnabled());
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
		person.setEnabled(true);
	}

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO accountCredentialsVO = new AccountCredentialsVO("leandro", "admin123");
		var accessToken = RestAssured.given().basePath("/auth/signin").port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_JSON).body(accountCredentialsVO).when().post().then()
				.statusCode(200).extract().body().as(TokenVO.class).getAccessToken();
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
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON).body(person)
				.when().post().then().statusCode(200).extract().body().asString();
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		Assertions.assertNotNull(persistedPerson);
		Assertions.assertNotNull(persistedPerson.getId());
		Assertions.assertNotNull(persistedPerson.getFirstName());
		Assertions.assertNotNull(persistedPerson.getLastName());
		Assertions.assertNotNull(persistedPerson.getAddress());
		Assertions.assertNotNull(persistedPerson.getGender());
		Assertions.assertTrue(persistedPerson.getEnabled());
		Assertions.assertEquals(person.getId(), persistedPerson.getId());
		Assertions.assertEquals("Nelson", persistedPerson.getFirstName());
		Assertions.assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		Assertions.assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		Assertions.assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", person.getId()).when().delete("{id}").then().statusCode(204);
	}

	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.accept(TestConfigs.CONTENT_TYPE_JSON).queryParams("page", 3, "size", 10, "direction", "asc").when()
				.get().then().statusCode(200).extract().body().asString();
		WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
		var personVOs = wrapper.getEmbedded().getPersons();
		PersonVO foundPersonOne = personVOs.get(0);
		Assertions.assertNotNull(foundPersonOne.getId());
		Assertions.assertNotNull(foundPersonOne.getFirstName());
		Assertions.assertNotNull(foundPersonOne.getLastName());
		Assertions.assertNotNull(foundPersonOne.getAddress());
		Assertions.assertNotNull(foundPersonOne.getGender());
		Assertions.assertTrue(foundPersonOne.getEnabled());
		Assertions.assertEquals(677, foundPersonOne.getId());
		Assertions.assertEquals("Alic", foundPersonOne.getFirstName());
		Assertions.assertEquals("Terbrug", foundPersonOne.getLastName());
		Assertions.assertEquals("3 Eagle Crest Court", foundPersonOne.getAddress());
		Assertions.assertEquals("Male", foundPersonOne.getGender());
		PersonVO foundPersonSix = personVOs.get(5);
		Assertions.assertNotNull(foundPersonSix.getId());
		Assertions.assertNotNull(foundPersonSix.getFirstName());
		Assertions.assertNotNull(foundPersonSix.getLastName());
		Assertions.assertNotNull(foundPersonSix.getAddress());
		Assertions.assertNotNull(foundPersonSix.getGender());
		Assertions.assertTrue(foundPersonSix.getEnabled());
		Assertions.assertEquals(911, foundPersonSix.getId());
		Assertions.assertEquals("Allegra", foundPersonSix.getFirstName());
		Assertions.assertEquals("Dome", foundPersonSix.getLastName());
		Assertions.assertEquals("57 Roxbury Pass", foundPersonSix.getAddress());
		Assertions.assertEquals("Female", foundPersonSix.getGender());
	}

	@Test
	@Order(8)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder().setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT).addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL)).build();
		RestAssured.given().spec(specificationWithoutToken).contentType(TestConfigs.CONTENT_TYPE_JSON).when().get()
				.then().statusCode(403);
	}

	@Test
	@Order(3)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ELTON).pathParam("id", person.getId())
				.when().patch("{id}").then().statusCode(200).extract().body().asString();
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		Assertions.assertNotNull(persistedPerson);
		Assertions.assertNotNull(persistedPerson.getId());
		Assertions.assertNotNull(persistedPerson.getFirstName());
		Assertions.assertNotNull(persistedPerson.getLastName());
		Assertions.assertNotNull(persistedPerson.getAddress());
		Assertions.assertNotNull(persistedPerson.getGender());
		Assertions.assertFalse(persistedPerson.getEnabled());
		Assertions.assertEquals(person.getId(), persistedPerson.getId());
		Assertions.assertEquals("Nelson", persistedPerson.getFirstName());
		Assertions.assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		Assertions.assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		Assertions.assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(7)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.accept(TestConfigs.CONTENT_TYPE_JSON).pathParam("firstName", "ayr")
				.queryParams("page", 0, "size", 6, "direction", "asc").when().get("findPersonByName/{firstName}").then()
				.statusCode(200).extract().body().asString();
		WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
		var personVOs = wrapper.getEmbedded().getPersons();
		PersonVO foundPersonOne = personVOs.get(0);
		Assertions.assertNotNull(foundPersonOne.getId());
		Assertions.assertNotNull(foundPersonOne.getFirstName());
		Assertions.assertNotNull(foundPersonOne.getLastName());
		Assertions.assertNotNull(foundPersonOne.getAddress());
		Assertions.assertNotNull(foundPersonOne.getGender());
		Assertions.assertTrue(foundPersonOne.getEnabled());
		Assertions.assertEquals(1, foundPersonOne.getId());
		Assertions.assertEquals("Ayrton", foundPersonOne.getFirstName());
		Assertions.assertEquals("Senna", foundPersonOne.getLastName());
		Assertions.assertEquals("São Paulo", foundPersonOne.getAddress());
		Assertions.assertEquals("Male", foundPersonOne.getGender());
	}

	@Test
	@Order(9)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.accept(TestConfigs.CONTENT_TYPE_JSON).queryParams("page", 3, "size", 10, "direction", "asc").when()
				.get().then().statusCode(200).extract().body().asString();
		Assertions.assertTrue(
				content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/677\"}}}"));
		Assertions.assertTrue(
				content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/846\"}}}"));
		Assertions.assertTrue(
				content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/714\"}}}"));
		Assertions.assertTrue(content.contains(
				"{\"first\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\"}"));
		Assertions.assertTrue(content.contains(
				"\"prev\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\"}"));
		Assertions.assertTrue(content
				.contains("\"self\":{\"href\":\"http://localhost:8888/api/person/v1?page=3&size=10&direction=asc\"}"));
		Assertions.assertTrue(content.contains(
				"\"next\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\"}"));
		Assertions.assertTrue(content.contains(
				"\"last\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\"}}"));
		Assertions.assertTrue(
				content.contains("\"page\":{\"size\":10,\"totalElements\":1007,\"totalPages\":101,\"number\":3}}"));
	}
}
