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

import com.fasterxml.jackson.core.JsonProcessingException;
import de.fherfurt.primefaces.domains.Address;
import de.fherfurt.primefaces.domains.Position;
import de.fherfurt.primefaces.repositories.addresses.AddressRepository;
import de.fherfurt.primefaces.repositories.persons.PersonRepository;
import de.fherfurt.primefaces.repositories.positions.PositionRepository;
import de.fherfurt.primefaces.services.GeocoderService;
import de.fherfurt.primefaces.web.BasePrimefacesView;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.transaction.Transactional;
import lombok.Getter;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;

@ManagedBean("addressView")
@ViewScoped
@Transactional
public class AddressPrimefacesView extends BasePrimefacesView<Address> {

    private static final long serialVersionUID = -8230990354527271228L;

    private final PositionRepository positionRepository;
    private final GeocoderService geocoderService;

    @Autowired
    public AddressPrimefacesView(final PersonRepository personRepository, final AddressRepository addressRepository, final PositionRepository positionRepository, final GeocoderService geocoderService) {
        this.lazyDataModel = LazyAddressDataModel.of(personRepository, addressRepository, positionRepository);
        this.positionRepository = positionRepository;
        this.geocoderService = geocoderService;
    }

    @Getter
    private final LazyAddressDataModel lazyDataModel;

    public void onClickCreateNewEntry() {
        this.editMode.set(false);
        this.lazyDataModel.setSelected(new Address());
    }

    public void onClickDeleteEntry(Address address) {
        if (Objects.isNull(address)) {
            return;
        }

        this.lazyDataModel.delete(address);
        this.renderMessage(FacesMessage.SEVERITY_ERROR, "Address deleted");
    }

    public void onClickSaveEntry() {
        if (Objects.nonNull(this.lazyDataModel.getSelected().getPosition())) {
            this.positionRepository.delete(this.lazyDataModel.getSelected().getPosition());
            this.lazyDataModel.getSelected().setPosition(null);
        }

        this.lazyDataModel.save();
        this.renderMessage(FacesMessage.SEVERITY_INFO, "Address " + (editMode.get() ? "updated" : "created"));
    }

    public void onClickResolveAddress() throws JsonProcessingException {
        this.geocoderService.getCoordinates(this.lazyDataModel.getSelected());
    }

    public void onClickCalculateCoordinates(Address address) {
        if (Objects.isNull(address)) {
            return;
        }
        try {
            Optional<Position> coordinates = geocoderService.getCoordinates(address);

            if (coordinates.isEmpty()) {
                this.renderMessage(FacesMessage.SEVERITY_ERROR, "Cannot calculate coordinates for address");
                return;
            }

            address.setPosition(coordinates.get());
            this.lazyDataModel.setSelected(address);
            this.lazyDataModel.save();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(AddressPrimefacesView.class.getName()).log(Level.SEVERE, null, ex);
            this.renderMessage(FacesMessage.SEVERITY_ERROR, "Cannot calculate coordinates for address");
        }

        if (Objects.nonNull(PrimeFaces.current())) {
            addInformationsToResponse(this.lazyDataModel.getSelected().getPosition());
        }
    }

    private void addInformationsToResponse(Position position) {
        PrimeFaces.current().ajax().addCallbackParam("renderingPossible", true);
        PrimeFaces.current().ajax().addCallbackParam("left", position.getBoundingBox().getLeftBound());
        PrimeFaces.current().ajax().addCallbackParam("top", position.getBoundingBox().getTopBound());
        PrimeFaces.current().ajax().addCallbackParam("description", position.getDescription());
        PrimeFaces.current().ajax().addCallbackParam("lat", position.getLatitude());
        PrimeFaces.current().ajax().addCallbackParam("lon", position.getLongitude());
    }

    public Position getPositionOfSelected() {
        if (Objects.isNull(this.lazyDataModel.getSelected()) || Objects.isNull(this.lazyDataModel.getSelected().getPosition())) {
            return null;
        }
        return this.lazyDataModel.getSelected().getPosition();
    }
}
