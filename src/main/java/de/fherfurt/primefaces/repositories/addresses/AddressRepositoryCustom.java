package de.fherfurt.primefaces.repositories.addresses;

import de.fherfurt.primefaces.domains.Address;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

public interface AddressRepositoryCustom {

    List<Address> findByParameters(int page, int count, Map<String, FilterMeta> filters, Map<String, SortMeta> sorts);

    long countByParameters(Map<String, FilterMeta> filters);
}
