/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.fherfurt.primefaces.repositories.persons;

import de.fherfurt.primefaces.domains.Person;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class PersonRepositoryImpl implements PersonRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Person> findByParameters(final int page, final int count, final Map<String, FilterMeta> filters, final Map<String, SortMeta> sorts) {

        final CriteriaBuilder builder = this.em.getCriteriaBuilder();
        final CriteriaQuery<Person> query = builder.createQuery(Person.class);

        final Root<Person> person = query.from(Person.class);

        final List<Predicate> predicates = convertToPredicates(builder, person, filters);
        query.where(predicates.toArray(new Predicate[0]));

        final List<Order> orderList = sorts.entrySet().stream()
                .map(sort -> sort.getValue().getOrder().isAscending() ? builder.asc(person.get(sort.getKey())) : builder.desc(person.get(sort.getKey())))
                .collect(Collectors.toList());
        query.orderBy(orderList);

        return this.em.createQuery(query).setFirstResult(page * count).setMaxResults(count).getResultList();
    }

    @Override
    public long countByParameters(final Map<String, FilterMeta> filters) {

        final CriteriaBuilder builder = this.em.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);

        final Root<Person> person = query.from(Person.class);
        query.select(builder.count(person));

        final List<Predicate> predicates = convertToPredicates(builder, person, filters);
        query.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getSingleResult();
    }

    private List<Predicate> convertToPredicates(final CriteriaBuilder builder, final Root<Person> person, final Map<String, FilterMeta> filters){
        return filters.values().stream()
                .map(parameter -> builder.equal(person.get(parameter.getField()), parameter.getFilterValue()))
                .collect(Collectors.toList());
    }
}
