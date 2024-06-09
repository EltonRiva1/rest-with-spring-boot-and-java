package br.com.elton.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.elton.controllers.BookController;
import br.com.elton.data.vo.v1.BookVO;
import br.com.elton.data.vo.v2.BookVOV2;
import br.com.elton.exceptions.RequiredObjectIsNullException;
import br.com.elton.exceptions.ResourceNotFoundException;
import br.com.elton.mapper.DozerMapper;
import br.com.elton.mapper.custom.BookMapper;
import br.com.elton.model.Book;
import br.com.elton.repositories.BookRepository;

@Service
public class BookServices {
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private BookMapper mapper;

	public BookVO findById(Long id) {
		this.logger.info("Finding one book!");
		var bookVO = DozerMapper.parseObject(this.bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!")), BookVO.class);
		return bookVO.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
	}

	public List<BookVO> findAll() {
		this.logger.info("Finding all book!");
		var bookVOs = DozerMapper.parseListObjects(this.bookRepository.findAll(), BookVO.class);
		bookVOs.stream().forEach(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
		return bookVOs;
	}

	public BookVO create(BookVO bookVO) {
		if (bookVO == null)
			throw new RequiredObjectIsNullException();
		this.logger.info("Creating one book!");
		var vo = DozerMapper.parseObject(this.bookRepository.save(DozerMapper.parseObject(bookVO, Book.class)),
				BookVO.class);
		return vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
	}

	public BookVO update(BookVO bookVO) {
		if (bookVO == null)
			throw new RequiredObjectIsNullException();
		this.logger.info("Updating one book!");
		var entity = this.bookRepository.findById(bookVO.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		entity.setAuthor(bookVO.getAuthor());
		entity.setLaunchDate(bookVO.getLaunchDate());
		entity.setPrice(bookVO.getPrice());
		entity.setTitle(bookVO.getTitle());
		var vo = DozerMapper.parseObject(this.bookRepository.save(entity), BookVO.class);
		return vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
	}

	public void delete(Long id) {
		this.logger.info("Deleting one book!");
		this.bookRepository.delete(this.bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!")));
	}

	public BookVOV2 createV2(BookVOV2 bookVOV2) {
		// TODO Auto-generated method stub
		this.logger.info("Creating one book with V2!");
		return this.mapper.convertEntityToVO(this.bookRepository.save(this.mapper.convertVOToEntity(bookVOV2)));
	}
}
