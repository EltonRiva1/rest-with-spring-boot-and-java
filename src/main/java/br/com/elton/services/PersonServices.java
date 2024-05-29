package br.com.elton.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.elton.data.vo.v1.PersonVO;
import br.com.elton.data.vo.v2.PersonVOV2;
import br.com.elton.exceptions.ResourceNotFoundException;
import br.com.elton.mapper.DozerMapper;
import br.com.elton.mapper.custom.PersonMapper;
import br.com.elton.model.Person;
import br.com.elton.repositories.PersonRepository;

@Service
public class PersonServices {
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private PersonMapper mapper;

	public PersonVO findById(Long id) {
		this.logger.info("Finding one person!");
		return DozerMapper.parseObject(this.personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!")), PersonVO.class);
	}

	public List<PersonVO> findAll() {
		this.logger.info("Finding all people!");
		return DozerMapper.parseListObjects(this.personRepository.findAll(), PersonVO.class);
	}

	public PersonVO create(PersonVO personVO) {
		this.logger.info("Creating one person!");
		return DozerMapper.parseObject(this.personRepository.save(DozerMapper.parseObject(personVO, Person.class)),
				PersonVO.class);
	}

	public PersonVO update(PersonVO personVO) {
		this.logger.info("Updating one person!");
		var entity = this.personRepository.findById(personVO.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		entity.setFirstName(personVO.getFirstName());
		entity.setLastName(personVO.getLastName());
		entity.setAddress(personVO.getAddress());
		entity.setGender(personVO.getGender());
		return DozerMapper.parseObject(this.personRepository.save(entity), PersonVO.class);
	}

	public void delete(Long id) {
		this.logger.info("Deleting one person!");
		var entity = this.personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		this.personRepository.delete(entity);
	}

	public PersonVOV2 createV2(PersonVOV2 personVOV2) {
		// TODO Auto-generated method stub
		this.logger.info("Creating one person with V2!");
		return this.mapper.convertEntityToVO(this.personRepository.save(this.mapper.convertVOToEntity(personVOV2)));
	}
}
