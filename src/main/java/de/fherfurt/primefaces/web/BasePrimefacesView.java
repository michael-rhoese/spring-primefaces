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
package de.fherfurt.primefaces.web;

import de.fherfurt.primefaces.domains.BaseEntity;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;

public abstract class BasePrimefacesView<E extends BaseEntity> implements Serializable {

    private static final long serialVersionUID = 4121972906010438269L;

    protected final AtomicBoolean editMode = new AtomicBoolean(false);

    @PostConstruct
    public void init() {

    }

    public void onRowSelect(SelectEvent<BaseEntity> event) {
        this.editMode.set(true);
    }

    public boolean isEditMode() {
        return this.editMode.get();
    }

    /**
     * Renders a notification for the performed operation.
     *
     * @param severity Level of the shown message
     * @param message  Messaeg to show
     */
    protected void renderMessage(FacesMessage.Severity severity, String message) {

        FacesMessage msg = new FacesMessage(
                severity,
                message,
                null
        );

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
