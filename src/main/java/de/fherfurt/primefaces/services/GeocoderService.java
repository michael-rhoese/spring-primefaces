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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.fherfurt.primefaces.domains.Address;
import de.fherfurt.primefaces.domains.BoundingBox;
import de.fherfurt.primefaces.domains.Position;
import java.io.IOException;
import java.io.Serializable;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import javax.net.ssl.HttpsURLConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeocoderService implements Serializable {

    private static final long serialVersionUID = -3491096454339897979L;

    private static final String API_URL = "https://nominatim.openstreetmap.org/search?q=";
    private static final String URL_PARAMS = "&format=json&addressdetails=1";

    private final ObjectMapper mapper = new ObjectMapper();

    public Optional<Position> getCoordinates(final Address address) throws JsonProcessingException {
        String errorMsgQuery = "";
        try {
            final StringBuilder queryBuilder = new StringBuilder(API_URL);

            if (StringUtils.isAnyEmpty(address.getStreet(), address.getCity(), address.getZipcode(), address.getCountry())) {
                return Optional.empty();
            }

            final String query = queryBuilder.append(address.getStreet())
                    .append("+")
                    .append(address.getCity())
                    .append("+")
                    .append(address.getZipcode())
                    .append("+")
                    .append(address.getCountry())
                    .append(URL_PARAMS).toString();

            errorMsgQuery = query;

            log.debug("Query:" + query);

            final Optional<String> queryResult = getRequest(query);

            if (queryResult.isEmpty()) {
                return Optional.empty();
            }

            JsonMapper jsonMapper = new JsonMapper();

            final JsonNode responseJsonNode = mapper.readTree(queryResult.get());
            final Position res = jsonMapper.convertValue(responseJsonNode.get(0), Position.class);

            final Double[] boundingBoxArray = jsonMapper.convertValue(responseJsonNode.get(0).get("boundingbox"), Double[].class);
            res.setBoundingBox(BoundingBox.of(boundingBoxArray));
            return Optional.of(res);
        } catch (IOException e) {
            log.error("Error when trying to get data with the following query " + errorMsgQuery);
        }

        return Optional.empty();
    }

    private Optional<String> getRequest(String url) throws IOException {
        final HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        if (!Objects.equals(con.getResponseCode(), 200)) {
            return Optional.empty();
        }

        final String content = IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8.name());
        con.disconnect();

        return Optional.of(content);
    }
}
