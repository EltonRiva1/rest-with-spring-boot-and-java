package br.com.elton.integrationtests.controller.withjson;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.elton.configs.TestConfigs;
import br.com.elton.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.elton.integrationtests.vo.PersonVO;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootApplication
@SpringBootTest(classes = PersonControllerJsonTest.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
class PersonControllerJsonTest extends AbstractIntegrationTest {
	@LocalServerPort
	private String port;
	private static RequestSpecification requestSpecification;
	private static ObjectMapper mapper;
	private static PersonVO personVO;

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
	@Order(1)
	void testCreate() throws JsonMappingException, JsonProcessingException {
		this.mockPersonVO();
		requestSpecification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ELTON).setBasePath("/api/person/v1")
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		var content = RestAssured.given().spec(requestSpecification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.body(personVO).post().then().statusCode(200).extract().body().asString();
		PersonVO persistedPersonVO = mapper.readValue(content, PersonVO.class);
		personVO = persistedPersonVO;
		Assertions.assertNotNull(persistedPersonVO);
		Assertions.assertNotNull(persistedPersonVO.getId());
		Assertions.assertNotNull(persistedPersonVO.getFirstName());
		Assertions.assertNotNull(persistedPersonVO.getLastName());
		Assertions.assertNotNull(persistedPersonVO.getAddress());
		Assertions.assertNotNull(persistedPersonVO.getGender());
		Assertions.assertTrue(persistedPersonVO.getId() > 0);
		Assertions.assertEquals("Richard", persistedPersonVO.getFirstName());
		Assertions.assertEquals("Stallman", persistedPersonVO.getLastName());
		Assertions.assertEquals("New York City, New York, US", persistedPersonVO.getAddress());
		Assertions.assertEquals("Male", persistedPersonVO.getGender());
	}

	@BeforeAll
	public static void setup() {
		mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		personVO = new PersonVO();
	}

	private void mockPersonVO() {
		// TODO Auto-generated method stub
		personVO.setFirstName("Richard");
		personVO.setLastName("Stallman");
		personVO.setAddress("New York City, New York, US");
		personVO.setGender("Male");
	}

	@Test
	@Order(2)
	void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		this.mockPersonVO();
		requestSpecification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU).setBasePath("/api/person/v1")
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		var content = RestAssured.given().spec(requestSpecification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.body(personVO).post().then().statusCode(403).extract().body().asString();
		Assertions.assertNotNull(content);
		Assertions.assertEquals("Invalid CORS request", content);
	}

	@Test
	@Order(3)
	void testFindById() throws JsonMappingException, JsonProcessingException {
		this.mockPersonVO();
		requestSpecification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ELTON).setBasePath("/api/person/v1")
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		var content = RestAssured.given().spec(requestSpecification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", personVO.getId()).get("{id}").then().statusCode(200).extract().body().asString();
		PersonVO persistedPersonVO = mapper.readValue(content, PersonVO.class);
		personVO = persistedPersonVO;
		Assertions.assertNotNull(persistedPersonVO);
		Assertions.assertNotNull(persistedPersonVO.getId());
		Assertions.assertNotNull(persistedPersonVO.getFirstName());
		Assertions.assertNotNull(persistedPersonVO.getLastName());
		Assertions.assertNotNull(persistedPersonVO.getAddress());
		Assertions.assertNotNull(persistedPersonVO.getGender());
		Assertions.assertTrue(persistedPersonVO.getId() > 0);
		Assertions.assertEquals("Richard", persistedPersonVO.getFirstName());
		Assertions.assertEquals("Stallman", persistedPersonVO.getLastName());
		Assertions.assertEquals("New York City, New York, US", persistedPersonVO.getAddress());
		Assertions.assertEquals("Male", persistedPersonVO.getGender());
	}

	@Test
	@Order(4)
	void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		this.mockPersonVO();
		requestSpecification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU).setBasePath("/api/person/v1")
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		var content = RestAssured.given().spec(requestSpecification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", personVO.getId()).get("{id}").then().statusCode(403).extract().body().asString();
		Assertions.assertNotNull(content);
		Assertions.assertEquals("Invalid CORS request", content);
	}
}
