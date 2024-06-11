package br.com.elton.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.elton.controllers.PersonController;
import br.com.elton.data.vo.v1.PersonVO;
import br.com.elton.exceptions.RequiredObjectIsNullException;
import br.com.elton.exceptions.ResourceNotFoundException;
import br.com.elton.mapper.DozerMapper;
import br.com.elton.model.Person;
import br.com.elton.repositories.PersonRepository;

@Service
public class PersonServices {
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	@Autowired
	private PersonRepository personRepository;

	public PersonVO findById(Long id) {
		this.logger.info("Finding one person!");
		var personVO = DozerMapper.parseObject(this.personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!")), PersonVO.class);
		return personVO.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
	}

	public List<PersonVO> findAll() {
		this.logger.info("Finding all people!");
		var personVOs = DozerMapper.parseListObjects(this.personRepository.findAll(), PersonVO.class);
		personVOs.stream()
				.forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		return personVOs;
	}

	public PersonVO create(PersonVO personVO) {
		if (personVO == null)
			throw new RequiredObjectIsNullException();
		this.logger.info("Creating one person!");
		var vo = DozerMapper.parseObject(this.personRepository.save(DozerMapper.parseObject(personVO, Person.class)),
				PersonVO.class);
		return vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
	}

	public PersonVO update(PersonVO personVO) {
		if (personVO == null)
			throw new RequiredObjectIsNullException();
		this.logger.info("Updating one person!");
		var entity = this.personRepository.findById(personVO.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		entity.setFirstName(personVO.getFirstName());
		entity.setLastName(personVO.getLastName());
		entity.setAddress(personVO.getAddress());
		entity.setGender(personVO.getGender());
		var vo = DozerMapper.parseObject(this.personRepository.save(entity), PersonVO.class);
		return vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
	}

	public void delete(Long id) {
		this.logger.info("Deleting one person!");
		this.personRepository.delete(this.personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!")));
	}
}
