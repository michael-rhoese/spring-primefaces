package de.fherfurt.primefaces.repositories.persons;

import de.fherfurt.primefaces.domains.Person;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person, Long>, PersonRepositoryCustom {

    List<Person> findAllByAddressId(Long id);

    void deletePersonById(Long id);
}
