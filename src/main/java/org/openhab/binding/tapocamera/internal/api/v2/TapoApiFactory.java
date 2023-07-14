package org.openhab.binding.tapocamera.internal.api.v2;

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

@Component(service = TapoApiFactory.class)
public class TapoApiFactory {
    private final Logger logger = LoggerFactory.getLogger(TapoApiFactory.class);
    public HttpClient httpClient = new HttpClient(SslUtils.createSslContext());;

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
    @Activate
    public TapoApiFactory() {
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

    public TapoApi getApi() {
        return new TapoApiImpl(httpClient);
    }

}
