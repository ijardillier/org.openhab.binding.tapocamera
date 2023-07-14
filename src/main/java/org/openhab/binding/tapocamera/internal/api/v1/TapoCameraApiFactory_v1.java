/*
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 *  See the NOTICE file(s) distributed with this work for additional
 *  information.
 *
 * This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.tapocamera.internal.api.v1;

import org.eclipse.jetty.client.HttpClient;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.openhab.binding.tapocamera.internal.api.ApiException;
import org.openhab.binding.tapocamera.internal.api.ssl.SslUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TapoCameraApiFactory_v1} is describing api factory.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@Component(service = TapoCameraApiFactory_v1.class)
// @NonNullByDefault
public class TapoCameraApiFactory_v1 {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HttpClient httpClient = new HttpClient(SslUtils.createSslContext());

    /**
     * Instantiates a new Yandex api factory.
     *
     * @param httpClientFactory the http client factory
     */
    @Activate
    public TapoCameraApiFactory_v1(@Reference HttpClientFactory httpClientFactory) {
        try {
            httpClient.getSslContextFactory().setSslContext(SslUtils.initSslContext());
            httpClient.getSslContextFactory().setTrustAll(true);
            this.httpClient.start();
        } catch (Exception e) {
            logger.warn("Unable to start HttpClient!");
        }
    }







    /**
     * Gets api.
     *
     * @return the api
     * @throws ApiException the api exception
     */
    public TapoCameraApi_v1 getApi() throws ApiException {
        return new TapoCameraApiV1Impl(httpClient);
    }
}
