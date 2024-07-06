package br.com.elton.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import org.springframework.stereotype.Service;

import br.com.elton.controllers.PersonController;
import br.com.elton.data.vo.v1.PersonVO;
import br.com.elton.exceptions.RequiredObjectIsNullException;
import br.com.elton.exceptions.ResourceNotFoundException;
import br.com.elton.mapper.DozerMapper;
import br.com.elton.model.Person;
import br.com.elton.repositories.PersonRepository;
import jakarta.transaction.Transactional;

@Service
public class PersonServices {
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private PagedResourcesAssembler<PersonVO> assembler;

	public PersonVO findById(Long id) {
		this.logger.info("Finding one person!");
		var personVO = DozerMapper.parseObject(this.personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!")), PersonVO.class);
		return personVO.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findById(id))
				.withSelfRel());
	}

	public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
		this.logger.info("Finding all people!");
		var personPage = this.personRepository.findAll(pageable);
		var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		personVosPage.map(p -> p.add(WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		Link link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class)
				.findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
		return this.assembler.toModel(personVosPage, link);
	}

	public PersonVO create(PersonVO personVO) {
		if (personVO == null)
			throw new RequiredObjectIsNullException();
		this.logger.info("Creating one person!");
		var vo = DozerMapper.parseObject(this.personRepository.save(DozerMapper.parseObject(personVO, Person.class)),
				PersonVO.class);
		return vo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findById(vo.getKey()))
				.withSelfRel());
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
		return vo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findById(vo.getKey()))
				.withSelfRel());
	}

	public void delete(Long id) {
		this.logger.info("Deleting one person!");
		this.personRepository.delete(this.personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!")));
	}

	@Transactional
	public PersonVO disablePerson(Long id) {
		this.logger.info("Disabling one person!");
		this.personRepository.disablePerson(id);
		var personVO = DozerMapper.parseObject(this.personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!")), PersonVO.class);
		return personVO.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findById(id))
				.withSelfRel());
	}

	public PagedModel<EntityModel<PersonVO>> findPersonByName(String firstName, Pageable pageable) {
		this.logger.info("Finding all people!");
		var personPage = this.personRepository.findPersonsByName(firstName, pageable);
		var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		personVosPage.map(p -> p.add(WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		Link link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class)
				.findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
		return this.assembler.toModel(personVosPage, link);
	}
}
