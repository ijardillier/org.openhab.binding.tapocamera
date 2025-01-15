/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tapocamera.internal.api;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Tapo camera api factory.
 *
 * @author "Ingrid JARDILLIER (ijardillier)"
 */
@Component(service = TapoCameraApiFactory.class)
public class TapoCameraApiFactory {

    private final Logger logger = LoggerFactory.getLogger(TapoCameraApiFactory.class);

    /**
     * The Http client.
     */
    public HttpClient httpClient;

    /**
     * Initializes a new Tapo camera api factory.
     */
    @Activate
    public TapoCameraApiFactory() {
        try {
            SslContextFactory.Client sslContextFactory = new SslContextFactory.Client(true);
            sslContextFactory.addExcludeProtocols("SSL", "SSLv2", "SSLv2Hello", "SSLv3", "TLSv1.3");
            sslContextFactory.setExcludeCipherSuites("^.*_(MD5|SHA1)$");
            httpClient = new HttpClient(sslContextFactory);
            httpClient.setConnectTimeout(60 * 1000);
            httpClient.setAddressResolutionTimeout(60 * 1000);
            httpClient.start();
            httpClient.dump(System.out);
        } catch (Exception e) {
            logger.warn("Unable to start HttpClient!");
        }
    }

    /**
     * Gets api.
     *
     * @return The api
     */
    public TapoCameraApi getApi(String tag) {
        return new TapoCameraApiImpl(httpClient, tag);
    }

    /**
     * Gets cloud api.
     *
     * @return The cloud api
     */
    public TapoCameraCloudApi getCloudApi() {
        return new TapoCameraCloudApiImpl(httpClient);
    }
}
