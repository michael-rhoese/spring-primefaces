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

import de.fherfurt.primefaces.core.UserSessionContext;
import java.util.Optional;
import javax.annotation.ManagedBean;
import javax.faces.view.ViewScoped;
import org.springframework.beans.factory.annotation.Autowired;

@ManagedBean("indexView")
@ViewScoped
public class IndexPrimefacesView extends BasePrimefacesView {

    private static final long serialVersionUID = -3584769291129542280L;

    private final UserSessionContext context;

    private static final String CACHE_KEY_CURRENT_CONTENT = "currentContent";

    private ContentTypes currentContent;

    @Autowired
    public IndexPrimefacesView(final UserSessionContext context) {
        this.context = context;
        final Optional<ContentTypes> cachedCurrentContent = context.getCached(CACHE_KEY_CURRENT_CONTENT, ContentTypes.class);
        this.currentContent = cachedCurrentContent.orElse(ContentTypes.HOME);
    }

    public enum ContentTypes {
        HOME,
        PERSONS,
        ADDRESSES
    }

    public void load(String contentType) {
        this.currentContent = ContentTypes.valueOf(contentType);
        context.cache(CACHE_KEY_CURRENT_CONTENT, this.currentContent);
    }

    public String getCurrentContent() {
        return this.currentContent.name();
    }
}
