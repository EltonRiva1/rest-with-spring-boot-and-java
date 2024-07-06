package br.com.elton.unittests.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.elton.data.vo.v1.PersonVO;
import br.com.elton.mapper.DozerMapper;
import br.com.elton.model.Person;
import br.com.elton.unittests.mapper.mocks.MockPerson;

public class DozerConverterTest {
	private MockPerson mockPerson;

	@BeforeEach
	public void setUp() {
		this.mockPerson = new MockPerson();
	}

	@Test
	public void parseEntityToVOTest() {
		PersonVO personVO = DozerMapper.parseObject(this.mockPerson.mockEntity(), PersonVO.class);
		assertEquals(Long.valueOf(0L), personVO.getKey());
		assertEquals("First Name Test0", personVO.getFirstName());
		assertEquals("Last Name Test0", personVO.getLastName());
		assertEquals("Address Test0", personVO.getAddress());
		assertEquals("Male", personVO.getGender());
	}

	@Test
	public void parseEntityListToVOListTest() {
		List<PersonVO> personVOs = DozerMapper.parseListObjects(this.mockPerson.mockEntityList(), PersonVO.class);
		PersonVO personVO = personVOs.get(0);
		assertEquals(Long.valueOf(0L), personVO.getKey());
		assertEquals("First Name Test0", personVO.getFirstName());
		assertEquals("Last Name Test0", personVO.getLastName());
		assertEquals("Address Test0", personVO.getAddress());
		assertEquals("Male", personVO.getGender());
		PersonVO outputSeven = personVOs.get(7);
		assertEquals(Long.valueOf(7L), outputSeven.getKey());
		assertEquals("First Name Test7", outputSeven.getFirstName());
		assertEquals("Last Name Test7", outputSeven.getLastName());
		assertEquals("Address Test7", outputSeven.getAddress());
		assertEquals("Female", outputSeven.getGender());
		PersonVO outputTwelve = personVOs.get(12);
		assertEquals(Long.valueOf(12L), outputTwelve.getKey());
		assertEquals("First Name Test12", outputTwelve.getFirstName());
		assertEquals("Last Name Test12", outputTwelve.getLastName());
		assertEquals("Address Test12", outputTwelve.getAddress());
		assertEquals("Male", outputTwelve.getGender());
	}

	@Test
	public void parseVOToEntityTest() {
		Person person = DozerMapper.parseObject(this.mockPerson.mockVO(), Person.class);
		assertEquals(Long.valueOf(0L), person.getId());
		assertEquals("First Name Test0", person.getFirstName());
		assertEquals("Last Name Test0", person.getLastName());
		assertEquals("Address Test0", person.getAddress());
		assertEquals("Male", person.getGender());
	}

	@Test
	public void parserVOListToEntityListTest() {
		List<Person> personVOs = DozerMapper.parseListObjects(this.mockPerson.mockVOList(), Person.class);
		Person person = personVOs.get(0);
		assertEquals(Long.valueOf(0L), person.getId());
		assertEquals("First Name Test0", person.getFirstName());
		assertEquals("Last Name Test0", person.getLastName());
		assertEquals("Address Test0", person.getAddress());
		assertEquals("Male", person.getGender());
		Person outputSeven = personVOs.get(7);
		assertEquals(Long.valueOf(7L), outputSeven.getId());
		assertEquals("First Name Test7", outputSeven.getFirstName());
		assertEquals("Last Name Test7", outputSeven.getLastName());
		assertEquals("Address Test7", outputSeven.getAddress());
		assertEquals("Female", outputSeven.getGender());
		Person outputTwelve = personVOs.get(12);
		assertEquals(Long.valueOf(12L), outputTwelve.getId());
		assertEquals("First Name Test12", outputTwelve.getFirstName());
		assertEquals("Last Name Test12", outputTwelve.getLastName());
		assertEquals("Address Test12", outputTwelve.getAddress());
		assertEquals("Male", outputTwelve.getGender());
	}
}
