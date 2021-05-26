package de.fherfurt.primefaces.repositories.persons;

import de.fherfurt.primefaces.domains.Person;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

import javax.persistence.criteria.Order;
import java.util.List;
import java.util.Map;

public interface PersonRepositoryCustom {

    List<Person> findByParameters(int page, int count, Map<String, FilterMeta> filters, Map<String, SortMeta> sorts);

    long countByParameters(Map<String, FilterMeta> filters);
}
