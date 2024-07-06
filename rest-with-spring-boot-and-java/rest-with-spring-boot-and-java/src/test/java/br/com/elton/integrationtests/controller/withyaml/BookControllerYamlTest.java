package br.com.elton.integrationtests.controller.withyaml;

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
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.elton.configs.TestConfigs;
import br.com.elton.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.elton.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.elton.integrationtests.vo.AccountCredentialsVO;
import br.com.elton.integrationtests.vo.BookVO;
import br.com.elton.integrationtests.vo.TokenVO;
import br.com.elton.integrationtests.vo.pagedmodels.PagedModelBook;
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
public class BookControllerYamlTest extends AbstractIntegrationTest {
	private static RequestSpecification specification;
	private static YMLMapper mapper;
	private static BookVO book;

	@BeforeAll
	public static void setup() {
		mapper = new YMLMapper();
		book = new BookVO();
	}

	@Test
	@Order(0)
	public void authorization() {
		AccountCredentialsVO user = new AccountCredentialsVO();
		user.setUsername("leandro");
		user.setPassword("admin123");
		var token = RestAssured.given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.basePath("/auth/signin").port(TestConfigs.SERVER_PORT).contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML).body(user, mapper).when().post().then().statusCode(200).extract()
				.body().as(TokenVO.class, mapper).getAccessToken();
		specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token)
				.setBasePath("/api/book/v1").setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockBook();
		book = RestAssured.given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.spec(specification).contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML)
				.body(book, mapper).when().post().then().statusCode(200).extract().body().as(BookVO.class, mapper);
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
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		book.setTitle("Docker Deep Dive - Updated");
		BookVO bookUpdated = RestAssured.given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.spec(specification).contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML)
				.body(book, mapper).when().put().then().statusCode(200).extract().body().as(BookVO.class, mapper);
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
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		var foundBook = RestAssured.given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.spec(specification).contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", book.getId()).when().get("{id}").then().statusCode(200).extract().body()
				.as(BookVO.class, mapper);
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
	@Order(4)
	public void testDelete() {
		RestAssured.given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.spec(specification).contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", book.getId()).when().delete("{id}").then().statusCode(204);
	}

	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		var response = RestAssured.given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.spec(specification).contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML)
				.queryParams("page", 0, "limit", 12, "direction", "asc").when().get().then().statusCode(200).extract()
				.body().as(PagedModelBook.class, mapper);
		List<BookVO> content = response.getContent();
		BookVO foundBookOne = content.get(0);
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
		BookVO foundBookFive = content.get(4);
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
	@Order(6)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		var unthreatedContent = RestAssured.given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.spec(specification).contentType(TestConfigs.CONTENT_TYPE_YML).accept(TestConfigs.CONTENT_TYPE_YML)
				.queryParams("page", 0, "size", 12, "direction", "asc").when().get().then().statusCode(200).extract()
				.body().asString();
		var content = unthreatedContent.replace("\n", "").replace("\r", "");
		Assertions.assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/book/v1/3\""));
		Assertions.assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/book/v1/5\""));
		Assertions.assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/book/v1/7\""));
		Assertions.assertTrue(content.contains(
				"rel: \"first\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=0&size=12&sort=title,asc\""));
		Assertions.assertTrue(content
				.contains("rel: \"self\"  href: \"http://localhost:8888/api/book/v1?page=0&size=12&direction=asc\""));
		Assertions.assertTrue(content.contains(
				"rel: \"next\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\""));
		Assertions.assertTrue(content.contains(
				"rel: \"last\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\""));
		Assertions.assertTrue(content.contains("page:  size: 12  totalElements: 15  totalPages: 2  number: 0"));
	}

	private void mockBook() {
		book.setTitle("Docker Deep Dive");
		book.setAuthor("Nigel Poulton");
		book.setPrice(Double.valueOf(55.99));
		book.setLaunchDate(new Date());
	}
}