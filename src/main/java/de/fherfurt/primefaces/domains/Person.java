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

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person extends BaseEntity {

    private static final long serialVersionUID = -3990512965359939849L;

    private String firstName;
    private String lastName;

    private LocalDate dayOfBirth;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Address address;

    public Integer getAge() {
        if (Objects.isNull(this.dayOfBirth)) {
            return null;
        }

        return Period.between(this.dayOfBirth, LocalDate.now()).getYears();
    }
}
