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
package de.fherfurt.primefaces.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Position extends BaseEntity {

    private static final long serialVersionUID = 3599208542615440377L;

    @JsonProperty("display_name")
    private String description;
    @JsonProperty("lon")
    private double longitude;
    @JsonProperty("lat")
    private double latitude;

    @JsonIgnore
    @Embedded
    private BoundingBox boundingBox;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private PositionTypes type;

    public static Position of(final String description, final double longitude, final double latitude) {
        return new Position(description, longitude, latitude, null, null);
    }

    public enum PositionTypes {
        PRIVATE,
        BUSINESS
    }
}
