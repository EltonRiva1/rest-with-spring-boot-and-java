package br.com.elton.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.elton.data.vo.v1.PersonVO;
import br.com.elton.exceptions.RequiredObjectIsNullException;
import br.com.elton.model.Person;
import br.com.elton.repositories.PersonRepository;
import br.com.elton.services.PersonServices;
import br.com.elton.unittests.mapper.mocks.MockPerson;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServicesTest {
	private MockPerson mockPerson;
	@InjectMocks
	private PersonServices personServices;
	@Mock
	private PersonRepository personRepository;

	@BeforeEach
	public void setUp() throws Exception {
		this.mockPerson = new MockPerson();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testFindById() {
		Person person = this.mockPerson.mockEntity(1);
		person.setId(1L);
		when(this.personRepository.findById(1L)).thenReturn(Optional.of(person));
		var personVO = this.personServices.findById(1L);
		assertNotNull(personVO);
		assertNotNull(personVO.getKey());
		assertNotNull(personVO.getLinks());
		assertTrue(personVO.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
		assertEquals("Address Test1", personVO.getAddress());
		assertEquals("First Name Test1", personVO.getFirstName());
		assertEquals("Last Name Test1", personVO.getLastName());
		assertEquals("Female", personVO.getGender());
	}

	@Test
	public void testCreate() {
		Person person = this.mockPerson.mockEntity(1);
		Person persisted = person;
		persisted.setId(1L);
		PersonVO personVO = this.mockPerson.mockVO(1);
		personVO.setKey(1L);
		when(this.personRepository.save(person)).thenReturn(persisted);
		var result = this.personServices.create(personVO);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
		assertEquals("Address Test1", result.getAddress());
		assertEquals("First Name Test1", result.getFirstName());
		assertEquals("Last Name Test1", result.getLastName());
		assertEquals("Female", result.getGender());
	}

	@Test
	public void testUpdate() {
		Person person = this.mockPerson.mockEntity(1);
		person.setId(1L);
		Person persisted = person;
		persisted.setId(1L);
		PersonVO personVO = this.mockPerson.mockVO(1);
		personVO.setKey(1L);
		when(this.personRepository.findById(1L)).thenReturn(Optional.of(person));
		when(this.personRepository.save(person)).thenReturn(persisted);
		var result = this.personServices.update(personVO);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
		assertEquals("Address Test1", result.getAddress());
		assertEquals("First Name Test1", result.getFirstName());
		assertEquals("Last Name Test1", result.getLastName());
		assertEquals("Female", result.getGender());
	}

	@Test
	public void testDelete() {
		Person person = this.mockPerson.mockEntity(1);
		person.setId(1L);
		when(this.personRepository.findById(1L)).thenReturn(Optional.of(person));
		this.personServices.delete(1L);
	}

	@Test
	public void testCreateWithNullPerson() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			this.personServices.create(null);
		});
		String expectedMessage = "It's now allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	public void testUpdateWithNullPerson() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			this.personServices.update(null);
		});
		String expectedMessage = "It's now allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}
}
