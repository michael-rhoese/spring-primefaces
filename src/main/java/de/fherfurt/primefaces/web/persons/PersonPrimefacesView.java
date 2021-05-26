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
import de.fherfurt.primefaces.web.BasePrimefacesView;
import java.util.Objects;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.convert.Converter;
import javax.faces.view.ViewScoped;
import javax.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

@ManagedBean("personView")
@ViewScoped
public class PersonPrimefacesView extends BasePrimefacesView<Person> {

    private static final long serialVersionUID = 4093615052840371924L;

    private final AddressRepository addressRepository;

    @Autowired
    public PersonPrimefacesView(final PersonRepository repository, final AddressRepository addressRepository) {
        this.lazyDataModel = LazyPersonDataModel.of(repository, addressRepository);
        this.addressRepository = addressRepository;
    }

    @Getter
    private final LazyPersonDataModel lazyDataModel;

    @Transactional
    public void onClickCreateNewEntry() {
        this.editMode.set(false);
        this.lazyDataModel.setSelected(new Person());
    }

    @Transactional
    public void onClickDeleteEntry(Person entry) {
        if (Objects.isNull(entry)) {
            return;
        }

        this.lazyDataModel.delete(entry);
        this.renderMessage(FacesMessage.SEVERITY_ERROR, "Person deleted");
    }

    @Transactional
    public void onClickSaveEntry() {

        this.lazyDataModel.save();
        this.renderMessage(FacesMessage.SEVERITY_INFO, "Person " + (editMode.get() ? "updated" : "created"));
    }

    public Iterable<Address> getAddresses() {
        return addressRepository.findAll();
    }

    public Converter getConverter() {
        return new AddressSelectMenuConverter();
    }
}
