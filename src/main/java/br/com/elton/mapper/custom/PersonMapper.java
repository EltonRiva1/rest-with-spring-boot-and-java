package br.com.elton.mapper.custom;

import java.util.Date;

import org.springframework.stereotype.Service;

import br.com.elton.data.vo.v2.PersonVOV2;
import br.com.elton.model.Person;

@Service
public class PersonMapper {
	public PersonVOV2 convertEntityToVO(Person person) {
		PersonVOV2 personVOV2 = new PersonVOV2();
		personVOV2.setId(person.getId());
		personVOV2.setAddress(person.getAddress());
		personVOV2.setBirthDay(new Date());
		personVOV2.setFirstName(person.getFirstName());
		personVOV2.setLastName(person.getLastName());
		personVOV2.setGender(person.getGender());
		return personVOV2;
	}

	public Person convertVOToEntity(PersonVOV2 personVOV2) {
		Person person = new Person();
		person.setId(personVOV2.getId());
		person.setAddress(personVOV2.getAddress());
		person.setFirstName(personVOV2.getFirstName());
		person.setLastName(personVOV2.getLastName());
		person.setGender(personVOV2.getGender());
		return person;
	}
}
