/*
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *  SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.tapocamera.internal.api;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.eclipse.jetty.client.HttpClient;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import org.openhab.binding.tapocamera.internal.api.ssl.SslUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Tapo camera api factory.
 */
@Component(service = TapoCameraApiFactory.class)
public class TapoCameraApiFactory {
    private final Logger logger = LoggerFactory.getLogger(TapoCameraApiFactory.class);
    /**
     * The Http client.
     */
    public HttpClient httpClient = new HttpClient(SslUtils.createSslContext());;

    /**
     * Ssl context ssl context.
     *
     * @param keyManagers   the key managers
     * @param trustManagers the trust managers
     * @return the ssl context
     */
    public static SSLContext sslContext(KeyManager[] keyManagers, TrustManager[] trustManagers) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers,
                    trustManagers,
                    null);
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Couldn't init TLS context", e);
        }
    }

    /**
     * Instantiates a new Tapo camera api factory.
     */
    @Activate
    public TapoCameraApiFactory() {
        try {
            httpClient.getSslContextFactory().setSslContext(SslUtils.initSslContext());
            httpClient.getSslContextFactory().setTrustAll(true);
            httpClient.setConnectTimeout(60 * 1000);
            httpClient.setAddressResolutionTimeout(60 * 1000);
            httpClient.start();
        } catch (Exception e) {
            logger.warn("Unable to start HttpClient!");
        }
    }

    /**
     * Gets api.
     *
     * @return the api
     */
    public TapoCameraApi getApi() {
        return new TapoCameraApiImpl(httpClient);
    }

    public TapoCameraCloudApi getCloudApi() {
        return new TapoCameraCloudApiImpl(httpClient);
    }
}
