package org.openhab.binding.tapocamera.internal.api.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.eclipse.jetty.util.ssl.SslContextFactory;

public class SslUtils {
    public static SslContextFactory createSslContext() {
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

    public static SSLContext initSslContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(trustAllKeys, trustAllCerts, null);
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Couldn't init TLS context", e);
        }
    }

    private static final KeyManager[] trustAllKeys = new KeyManager[] {
            new KeyManager() {}
    };

    private static final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
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

    private static final HostnameVerifier allowAllHostNames() {
        return (hostname, sslSession) -> true;
    }

}
