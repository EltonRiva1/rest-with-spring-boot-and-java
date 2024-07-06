package br.com.elton.unittests.mapper.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.elton.data.vo.v1.PersonVO;
import br.com.elton.model.Person;

public class MockPerson {
	public Person mockEntity() {
		return this.mockEntity(0);
	}

	public PersonVO mockVO() {
		return this.mockVO(0);
	}

	public List<Person> mockEntityList() {
		List<Person> persons = new ArrayList<>();
		for (int i = 0; i < 14; i++) {
			persons.add(this.mockEntity(i));
		}
		return persons;
	}

	public List<PersonVO> mockVOList() {
		List<PersonVO> personVOs = new ArrayList<>();
		for (int i = 0; i < 14; i++) {
			personVOs.add(this.mockVO(i));
		}
		return personVOs;
	}

	public Person mockEntity(Integer number) {
		Person person = new Person();
		person.setAddress("Address Test" + number);
		person.setFirstName("First Name Test" + number);
		person.setGender(((number % 2) == 0) ? "Male" : "Female");
		person.setId(number.longValue());
		person.setLastName("Last Name Test" + number);
		return person;
	}

	public PersonVO mockVO(Integer number) {
		PersonVO personVO = new PersonVO();
		personVO.setAddress("Address Test" + number);
		personVO.setFirstName("First Name Test" + number);
		personVO.setGender(((number % 2) == 0) ? "Male" : "Female");
		personVO.setKey(number.longValue());
		personVO.setLastName("Last Name Test" + number);
		return personVO;
	}
}
