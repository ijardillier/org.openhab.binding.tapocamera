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

package org.openhab.binding.tapocamera.internal.api;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TapoCameraApiFactory} is describing api factory.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@Component(service = TapoCameraApiFactory.class)
// @NonNullByDefault
public class TapoCameraApiFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HttpClient httpClient = new HttpClient(createSslContext());

    /**
     * Instantiates a new Yandex api factory.
     *
     * @param httpClientFactory the http client factory
     */
    @Activate
    public TapoCameraApiFactory(@Reference HttpClientFactory httpClientFactory) {
        try {
            SSLContext ssl = initSslContext(trustAllKeys, trustAllCerts);
            httpClient.getSslContextFactory().setSslContext(ssl);
            httpClient.getSslContextFactory().setTrustAll(true);

            this.httpClient.start();
            // this.httpClient = httpClientFactory.getCommonHttpClient();

        } catch (Exception e) {
            logger.warn("Unable to start HttpClient!");
        }
    }

    private static SslContextFactory createSslContext() {
        SslContextFactory ssl = new SslContextFactory();
        String[] excludedCiphersWithoutTlsRsaExclusion = Arrays.stream(ssl.getExcludeCipherSuites())
                .filter(cipher -> !cipher.equals("^TLS_RSA_.*$")).toArray(String[]::new);
        ssl.setExcludeCipherSuites(excludedCiphersWithoutTlsRsaExclusion);

        excludedCiphersWithoutTlsRsaExclusion = Arrays.stream(ssl.getExcludeCipherSuites())
                .filter(cipher -> !cipher.equals("^SSL_.*$")).toArray(String[]::new);
        ssl.setExcludeCipherSuites(excludedCiphersWithoutTlsRsaExclusion);

        excludedCiphersWithoutTlsRsaExclusion = Arrays.stream(ssl.getExcludeCipherSuites())
                .filter(cipher -> !cipher.equals("^.*_(MD5|SHA|SHA1)$")).toArray(String[]::new);
        ssl.setExcludeCipherSuites(excludedCiphersWithoutTlsRsaExclusion);

        String[] excludeProtocols = Arrays.stream(ssl.getExcludeProtocols()).filter(proto -> !proto.startsWith("SSL"))
                .toArray(String[]::new);
        ssl.setExcludeProtocols(excludeProtocols);

        ssl.setIncludeProtocols("TLSv1.3", "TLSv1.2", "TLSv1.1", "SSL", "SSLv2", "SSLv2Hello", "SSLv3");

        return ssl;
    }

    private SSLContext initSslContext(KeyManager[] keyManagers, TrustManager[] trustManagers) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Couldn't init TLS context", e);
        }
    }

    private final KeyManager[] trustAllKeys = new KeyManager[] { new KeyManager() {

    } };

    private final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    } };

    /**
     * Gets api.
     *
     * @return the api
     * @throws ApiException the api exception
     */
    public TapoCameraApi getApi() throws ApiException {
        return new TapoCameraApiImpl(httpClient);
    }
}
