package de.fherfurt.primefaces.repositories.addresses;

import de.fherfurt.primefaces.domains.Address;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address, Long>, AddressRepositoryCustom {

    void deleteAddressById(Long id);
}
