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
package de.fherfurt.primefaces.web.persons;

import de.fherfurt.primefaces.domains.Address;
import de.fherfurt.primefaces.domains.Person;
import de.fherfurt.primefaces.repositories.addresses.AddressRepository;
import de.fherfurt.primefaces.repositories.persons.PersonRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

@RequiredArgsConstructor(staticName = "of")
public class LazyPersonDataModel extends LazyDataModel<Person> {

    private static final long serialVersionUID = 3843316090759353348L;

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;

    private final List<Person> cache = new ArrayList<>();

    @Getter
    @Setter
    private Person selected;

    @Override
    public List<Person> load(int page, int size, Map<String, SortMeta> sorts, Map<String, FilterMeta> filters) {

        cache.clear();

        cache.addAll(personRepository.findByParameters(page, size, filters, sorts));

        setRowCount((int) personRepository.countByParameters(filters));

        return cache;
    }

    @Override
    public Person getRowData(String rowKey) {
        return cache.stream().filter(entry -> Objects.equals(String.valueOf(entry.getId()), rowKey)).findFirst().orElse(null);
    }

    @Override
    public String getRowKey(Person object) {
        return String.valueOf(object.getId());
    }

    public void save() {
        if (Objects.isNull(this.getSelected())) {
            return;
        }

        if (Objects.nonNull(this.getSelected().getAddress()) && Objects.nonNull(this.getSelected().getAddress().getId())) {
            Address loaded = addressRepository.findById(this.getSelected().getAddress().getId()).orElse(null);
            this.getSelected().setAddress(loaded);
        }

        this.personRepository.save(this.getSelected());
    }

    public void delete(Person person) {
        this.personRepository.deletePersonById(person.getId());
    }
}
