package br.com.elton.integrationtests.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.elton.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.elton.model.Person;
import br.com.elton.repositories.PersonRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {
	@Autowired
	private PersonRepository personRepository;
	private static Person person;

	@BeforeAll
	public static void setup() {
		person = new Person();
	}

	@Test
	@Order(0)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
		person = this.personRepository.findPersonsByName("ayr", pageable).getContent().get(0);
		Assertions.assertNotNull(person.getId());
		Assertions.assertNotNull(person.getFirstName());
		Assertions.assertNotNull(person.getLastName());
		Assertions.assertNotNull(person.getAddress());
		Assertions.assertNotNull(person.getGender());
		Assertions.assertTrue(person.getEnabled());
		Assertions.assertEquals(1, person.getId());
		Assertions.assertEquals("Ayrton", person.getFirstName());
		Assertions.assertEquals("Senna", person.getLastName());
		Assertions.assertEquals("São Paulo", person.getAddress());
		Assertions.assertEquals("Male", person.getGender());
	}

	@Test
	@Order(1)
	public void testDisablePerson() throws JsonMappingException, JsonProcessingException {
		this.personRepository.disablePerson(person.getId());
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
		person = this.personRepository.findPersonsByName("ayr", pageable).getContent().get(0);
		Assertions.assertNotNull(person.getId());
		Assertions.assertNotNull(person.getFirstName());
		Assertions.assertNotNull(person.getLastName());
		Assertions.assertNotNull(person.getAddress());
		Assertions.assertNotNull(person.getGender());
		Assertions.assertFalse(person.getEnabled());
		Assertions.assertEquals(1, person.getId());
		Assertions.assertEquals("Ayrton", person.getFirstName());
		Assertions.assertEquals("Senna", person.getLastName());
		Assertions.assertEquals("São Paulo", person.getAddress());
		Assertions.assertEquals("Male", person.getGender());
	}
}
