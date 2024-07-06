package br.com.elton.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.elton.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
