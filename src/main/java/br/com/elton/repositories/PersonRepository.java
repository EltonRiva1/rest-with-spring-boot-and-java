package br.com.elton.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.elton.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
