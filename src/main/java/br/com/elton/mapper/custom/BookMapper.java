package br.com.elton.mapper.custom;

import java.util.Date;

import org.springframework.stereotype.Service;

import br.com.elton.data.vo.v2.BookVOV2;
import br.com.elton.model.Book;

@Service
public class BookMapper {
	public BookVOV2 convertEntityToVO(Book book) {
		BookVOV2 bookVOV2 = new BookVOV2();
		bookVOV2.setId(book.getId());
		bookVOV2.setAuthor(book.getAuthor());
		bookVOV2.setLaunchDate(new Date());
		bookVOV2.setPrice(book.getPrice());
		bookVOV2.setTitle(book.getTitle());
		return bookVOV2;
	}

	public Book convertVOToEntity(BookVOV2 bookVOV2) {
		Book book = new Book();
		book.setId(bookVOV2.getId());
		book.setAuthor(bookVOV2.getAuthor());
		book.setLaunchDate(new Date());
		book.setPrice(bookVOV2.getPrice());
		book.setTitle(bookVOV2.getTitle());
		return book;
	}
}
