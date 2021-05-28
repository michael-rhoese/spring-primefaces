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
package de.fherfurt.primefaces.core;

import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.client.NominatimOptions;
import java.util.Locale;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenStreetViewConfiguration {

    @Bean
    public JsonNominatimClient nominatimClient(@Value("nominatim.signing.mail") String nominatimSigningMail) {
        final CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setSSLSocketFactory(SSLConnectionSocketFactory.getSocketFactory())
                .build();

        final NominatimOptions options = new NominatimOptions();
        options.setAcceptLanguage(Locale.getDefault());
        options.setBounded(true);
        return new JsonNominatimClient(httpClient, nominatimSigningMail, options);
    }
}
