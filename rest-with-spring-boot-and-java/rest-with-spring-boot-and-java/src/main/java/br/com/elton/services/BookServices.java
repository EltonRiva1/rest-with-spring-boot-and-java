package br.com.elton.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.elton.controllers.BookController;
import br.com.elton.data.vo.v1.BookVO;
import br.com.elton.exceptions.RequiredObjectIsNullException;
import br.com.elton.exceptions.ResourceNotFoundException;
import br.com.elton.mapper.DozerMapper;
import br.com.elton.model.Book;
import br.com.elton.repositories.BookRepository;

@Service
public class BookServices {
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private PagedResourcesAssembler<BookVO> assembler;

	public BookVO findById(Long id) {
		this.logger.info("Finding one book!");
		var bookVO = DozerMapper.parseObject(this.bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!")), BookVO.class);
		return bookVO.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
	}

	public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {
		logger.info("Finding all books!");
		var booksPage = this.bookRepository.findAll(pageable);
		var booksVOs = booksPage.map(p -> DozerMapper.parseObject(p, BookVO.class));
		booksVOs.map(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
		Link findAllLink = linkTo(
				methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
				.withSelfRel();
		return this.assembler.toModel(booksVOs, findAllLink);
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
}
