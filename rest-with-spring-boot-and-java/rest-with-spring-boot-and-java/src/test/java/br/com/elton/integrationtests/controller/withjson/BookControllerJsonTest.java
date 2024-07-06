package br.com.elton.integrationtests.controller.withjson;

import java.util.Date;
import java.util.List;

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
import br.com.elton.data.vo.v1.security.AccountCredentialsVO;
import br.com.elton.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.elton.integrationtests.vo.BookVO;
import br.com.elton.integrationtests.vo.TokenVO;
import br.com.elton.integrationtests.vo.wrappers.WrapperBookVO;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {
	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static BookVO book;

	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		book = new BookVO();
	}

	@Test
	@Order(1)
	public void authorization() {
		AccountCredentialsVO user = new AccountCredentialsVO();
		user.setUsername("leandro");
		user.setPassword("admin123");
		var token = RestAssured.given().basePath("/auth/signin").port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_JSON).body(user).when().post().then().statusCode(200).extract()
				.body().as(TokenVO.class).getAccessToken();
		specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token)
				.setBasePath("/api/book/v1").setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(2)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockBook();
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON).body(book)
				.when().post().then().statusCode(200).extract().body().asString();
		book = objectMapper.readValue(content, BookVO.class);
		Assertions.assertNotNull(book.getId());
		Assertions.assertNotNull(book.getTitle());
		Assertions.assertNotNull(book.getAuthor());
		Assertions.assertNotNull(book.getPrice());
		Assertions.assertTrue(book.getId() > 0);
		Assertions.assertEquals("Docker Deep Dive", book.getTitle());
		Assertions.assertEquals("Nigel Poulton", book.getAuthor());
		Assertions.assertEquals(55.99, book.getPrice());
	}

	@Test
	@Order(3)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		book.setTitle("Docker Deep Dive - Updated");
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON).body(book)
				.when().put().then().statusCode(200).extract().body().asString();
		BookVO bookUpdated = objectMapper.readValue(content, BookVO.class);
		Assertions.assertNotNull(bookUpdated.getId());
		Assertions.assertNotNull(bookUpdated.getTitle());
		Assertions.assertNotNull(bookUpdated.getAuthor());
		Assertions.assertNotNull(bookUpdated.getPrice());
		Assertions.assertEquals(bookUpdated.getId(), book.getId());
		Assertions.assertEquals("Docker Deep Dive - Updated", bookUpdated.getTitle());
		Assertions.assertEquals("Nigel Poulton", bookUpdated.getAuthor());
		Assertions.assertEquals(55.99, bookUpdated.getPrice());
	}

	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", book.getId()).when().get("{id}").then().statusCode(200).extract().body().asString();
		BookVO foundBook = objectMapper.readValue(content, BookVO.class);
		Assertions.assertNotNull(foundBook.getId());
		Assertions.assertNotNull(foundBook.getTitle());
		Assertions.assertNotNull(foundBook.getAuthor());
		Assertions.assertNotNull(foundBook.getPrice());
		Assertions.assertEquals(foundBook.getId(), book.getId());
		Assertions.assertEquals("Docker Deep Dive - Updated", foundBook.getTitle());
		Assertions.assertEquals("Nigel Poulton", foundBook.getAuthor());
		Assertions.assertEquals(55.99, foundBook.getPrice());
	}

	@Test
	@Order(5)
	public void testDelete() {
		RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON).pathParam("id", book.getId())
				.when().delete("{id}").then().statusCode(204);
	}

	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page", 0, "limit", 12, "direction", "asc").when().get().then().statusCode(200).extract()
				.body().asString();
		WrapperBookVO wrapper = objectMapper.readValue(content, WrapperBookVO.class);
		List<BookVO> books = wrapper.getEmbedded().getBooks();
		BookVO foundBookOne = books.get(0);
		Assertions.assertNotNull(foundBookOne.getId());
		Assertions.assertNotNull(foundBookOne.getTitle());
		Assertions.assertNotNull(foundBookOne.getAuthor());
		Assertions.assertNotNull(foundBookOne.getPrice());
		Assertions.assertTrue(foundBookOne.getId() > 0);
		Assertions.assertEquals(
				"Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana",
				foundBookOne.getTitle());
		Assertions.assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", foundBookOne.getAuthor());
		Assertions.assertEquals(54.00, foundBookOne.getPrice());
		BookVO foundBookFive = books.get(4);
		Assertions.assertNotNull(foundBookFive.getId());
		Assertions.assertNotNull(foundBookFive.getTitle());
		Assertions.assertNotNull(foundBookFive.getAuthor());
		Assertions.assertNotNull(foundBookFive.getPrice());
		Assertions.assertTrue(foundBookFive.getId() > 0);
		Assertions.assertEquals("Domain Driven Design", foundBookFive.getTitle());
		Assertions.assertEquals("Eric Evans", foundBookFive.getAuthor());
		Assertions.assertEquals(92.00, foundBookFive.getPrice());
	}

	@Test
	@Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		var content = RestAssured.given().spec(specification).contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page", 0, "size", 12, "direction", "asc").when().get().then().statusCode(200).extract()
				.body().asString();
		Assertions.assertTrue(
				content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/3\"}}}"));
		Assertions.assertTrue(
				content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/5\"}}}"));
		Assertions.assertTrue(
				content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/7\"}}}"));
		Assertions.assertTrue(content.contains(
				"{\"first\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=0&size=12&sort=title,asc\"}"));
		Assertions.assertTrue(content
				.contains("\"self\":{\"href\":\"http://localhost:8888/api/book/v1?page=0&size=12&direction=asc\"}"));
		Assertions.assertTrue(content.contains(
				"\"next\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\"}"));
		Assertions.assertTrue(content.contains(
				"\"last\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\"}}"));
		Assertions.assertTrue(
				content.contains("\"page\":{\"size\":12,\"totalElements\":15,\"totalPages\":2,\"number\":0}}"));
	}

	private void mockBook() {
		book.setTitle("Docker Deep Dive");
		book.setAuthor("Nigel Poulton");
		book.setPrice(Double.valueOf(55.99));
		book.setLaunchDate(new Date());
	}
}