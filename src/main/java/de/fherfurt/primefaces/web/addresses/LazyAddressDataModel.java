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
package de.fherfurt.primefaces.web.addresses;

import de.fherfurt.primefaces.domains.Address;
import de.fherfurt.primefaces.domains.Person;
import de.fherfurt.primefaces.domains.Position;
import de.fherfurt.primefaces.repositories.addresses.AddressRepository;
import de.fherfurt.primefaces.repositories.persons.PersonRepository;
import de.fherfurt.primefaces.repositories.positions.PositionRepository;

import java.util.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

@RequiredArgsConstructor(staticName = "of")
public class LazyAddressDataModel extends LazyDataModel<Address> {

    private static final long serialVersionUID = -6547554770297801689L;
    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final PositionRepository positionRepository;

    private final List<Address> cache = new ArrayList<>();

    @Getter
    @Setter
    private Address selected;

    @Override
    public List<Address> load(int page, int size, Map<String, SortMeta> sorts, Map<String, FilterMeta> filters) {

        cache.clear();

        cache.addAll(addressRepository.findByParameters(page, size, filters, sorts));

        setRowCount((int) addressRepository.countByParameters(filters));

        return cache;
    }

    @Override
    public Address getRowData(String rowKey) {
        return cache.stream().filter(entry -> Objects.equals(String.valueOf(entry.getId()), rowKey)).findFirst().orElse(null);
    }

    @Override
    public String getRowKey(Address object) {
        return String.valueOf(object.getId());
    }

    public void save() {
        if (Objects.isNull(this.getSelected())) {
            return;
        }

        if (Objects.nonNull(this.getSelected().getPosition()) && Objects.nonNull(this.getSelected().getPosition().getId())) {
            Position loaded = positionRepository.findById(this.getSelected().getPosition().getId()).orElse(null);
            this.getSelected().setPosition(loaded);
        }

        this.addressRepository.save(this.getSelected());
    }

    public void delete(final Address address) {
        final List<Person> personsWithAddress = this.personRepository.findAllByAddressId(address.getId());
        personsWithAddress.forEach(person -> person.setAddress(null));
        this.personRepository.saveAll(personsWithAddress);

        this.addressRepository.deleteAddressById(address.getId());
    }
}
