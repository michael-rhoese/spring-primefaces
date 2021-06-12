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
package de.fherfurt.primefaces.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.fherfurt.primefaces.domains.Address;
import de.fherfurt.primefaces.domains.BoundingBox;
import de.fherfurt.primefaces.domains.Position;
import fr.dudie.nominatim.client.JsonNominatimClient;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeocoderService implements Serializable {

    private static final long serialVersionUID = -3491096454339897979L;

    private final JsonNominatimClient client;

    @Autowired
    public GeocoderService(final JsonNominatimClient nominatimClient) {
        this.client = nominatimClient;
    }

    public Optional<Position> getCoordinates(final Address address) throws JsonProcessingException {

        try {
            final List<fr.dudie.nominatim.model.Address> foundAddresses = client.search(address.getStreet() + ", " + address.getCity() + ", " + address.getZipcode() + ", " + address.getCountry());

            if (foundAddresses.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(map(foundAddresses.get(0)));
        } catch (IOException e) {
            log.error("Error when trying to get data for " + address, e);
        }

        return Optional.empty();
    }

    private Position map(fr.dudie.nominatim.model.Address address) {
        Position res = Position.of(address.getDisplayName(), address.getLongitude(), address.getLatitude());
        fr.dudie.nominatim.model.BoundingBox boundingBox = address.getBoundingBox();
        res.setBoundingBox(
                BoundingBox.of(
                        boundingBox.getWest(),
                        boundingBox.getSouth(),
                        boundingBox.getEast(),
                        boundingBox.getNorth()
                )
        );

        return res;
    }
}
