package br.com.elton.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.elton.exceptions.ResourceNotFoundException;
import br.com.elton.model.Person;
import br.com.elton.repositories.PersonRepository;

@Service
public class PersonServices {
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	@Autowired
	private PersonRepository personRepository;

	public Person findById(Long id) {
		this.logger.info("Finding one person!");
		return this.personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
	}

	public List<Person> findAll() {
		this.logger.info("Finding all people!");
		return this.personRepository.findAll();
	}

	public Person create(Person person) {
		this.logger.info("Creating one person!");
		return this.personRepository.save(person);
	}

	public Person update(Person person) {
		this.logger.info("Updating one person!");
		var entity = this.personRepository.findById(person.getId())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		return this.personRepository.save(entity);
	}

	public void delete(Long id) {
		this.logger.info("Deleting one person!");
		var entity = this.personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		this.personRepository.delete(entity);
	}
}
