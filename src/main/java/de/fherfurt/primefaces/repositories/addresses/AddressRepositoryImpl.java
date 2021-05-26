package de.fherfurt.primefaces.repositories.addresses;

import de.fherfurt.primefaces.domains.Address;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.springframework.stereotype.Repository;

@Repository
public class AddressRepositoryImpl implements AddressRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Address> findByParameters(final int page, final int count, final Map<String, FilterMeta> filters, final Map<String, SortMeta> sorts) {

        final CriteriaBuilder builder = this.em.getCriteriaBuilder();
        final CriteriaQuery<Address> query = builder.createQuery(Address.class);

        final Root<Address> address = query.from(Address.class);

        final List<Predicate> predicates = convertToPredicates(builder, address, filters);
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        final List<Order> orderList = sorts.entrySet().stream()
                .map(sort -> sort.getValue().getOrder().isAscending() ? builder.asc(address.get(sort.getKey())) : builder.desc(address.get(sort.getKey())))
                .collect(Collectors.toList());
        query.orderBy(orderList);

        return this.em.createQuery(query).setFirstResult(page * count).setMaxResults(count).getResultList();
    }

    @Override
    public long countByParameters(final Map<String, FilterMeta> filters) {

        final CriteriaBuilder builder = this.em.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);

        final Root<Address> address = query.from(Address.class);
        query.select(builder.count(address));

        final List<Predicate> predicates = convertToPredicates(builder, address, filters);
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        return em.createQuery(query).getSingleResult();
    }

    private List<Predicate> convertToPredicates(final CriteriaBuilder builder, final Root<Address> address, final Map<String, FilterMeta> filters) {
        return filters.values().stream()
                .map(parameter -> builder.equal(address.get(parameter.getField()), parameter.getFilterValue()))
                .collect(Collectors.toList());
    }
}
