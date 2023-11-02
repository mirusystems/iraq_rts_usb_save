package com.mirusystems.usbsave.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.security.auth.x500.X500Principal;

/**
 * Class with utilities to access and storage certificates X509.
 *
 * @version 1.0
 */
public final class CertificateUtils {

    /**
     * Class with OID's to manage certificates.
     *
     * @version 1.0
     */
    public static final class OID {

        /**
         * OID the certificates extensions.
         */
        public static final String CERTIFICATE_EXTENSION_OID = "2.5.29";

        /**
         * OID the SubjectKeyIdentifier certificate extension.
         */
        public static final String SUBJECT_KEY_IDENTIFIER_OID = CERTIFICATE_EXTENSION_OID + ".14";
    }

    /**
     * Selector of autosign certificates.
     *
     * @version 1.0
     */
    private static class SelfSignedX509CertSelector extends X509CertSelector {

        /**
         * Check that certificate's issuing is equals to certificate's subject.
         *
         * @param cert The certificate.
         * @return <code>true</code> if certificate is correct.
         * @see CertSelector#match(Certificate)
         */
        @Override
        public boolean match(final Certificate cert) {
            boolean match = false;
            if (cert instanceof X509Certificate) {
                X500Principal issuer, subject;
                issuer = ((X509Certificate) cert).getIssuerX500Principal();
                subject = ((X509Certificate) cert).getSubjectX500Principal();
                match = (issuer == null && subject == null) || subject.equals(issuer);
            }
            return match;
        }
    } // class SelfSignedX509CertSelector

    /**
     * Security provider to use.
     */
    private static Provider provider = null;

    /**
     * SHA-1 with RSA signature algorithm name.
     */
    public static final String SHA1_WITH_RSA = "SHA1withRSA";

    /**
     * SHA-256 with RSA signature algorithm name.
     */
    public static final String SHA256_WITH_RSA = "SHA256withRSA";

    /**
     * SHA-512 with RSA signature algorithm name.
     */
    public static final String SHA512_WITH_RSA = "SHA512withRSA";

    /**
     * Keystore name.
     */
    public static final String STORE_RESOURCE = "id.store";

    /**
     * Minimum version of X509 certificate.
     */
    public static final int X509_MIN_VERSION = 3;

    /**
     * Keystore PKCS12 type.
     */
    public static final String KS_TYPE_P12 = "PKCS12";

    /**
     * Keystore JKS type (Java KeyStore).
     */
    public static final String KS_TYPE_JKS = "JKS";

    /**
     * Keystore BKS type (BouncyCastle KeyStore).
     */
    public static final String KS_TYPE_BKS = "BKS";

    /**
     * Keystore JCEKS type(Java Cryptography Extension KeyStore).
     */
    public static final String KS_TYPE_JCEKS = "JCEKS";

    /**
     * Id x509 certificates types.
     */
    public static final String X_509_CERT_TYPE = "X.509";

    /**
     * Id pki type based in X.509 certificates.
     */
    public static final String PKIX_TYPE = "PKIX";

    /**
     * Security provider SUN.
     */
    public static final String SUN_PROVIDER = "SunJCE";

    /**
     * Security provider BouncyCastle.
     */
    public static final String BC_PROVIDER = "BC";

    /**
     * Security provider SpongyCastle.
     */
    public static final String SC_PROVIDER = "SC";

    /**
     * Selector of certificates autosign.
     */
    public static final CertSelector SELF_SIGNED_X509_CERT_SELECTOR = new SelfSignedX509CertSelector();

    /**
     * Get certificate of keystore selected.
     *
     * @param keyStore Keystore where extract the certificate.
     * @param provider Security provider that manage the keystore. If is null use the default provider.
     * @return Keystore with the certificates selected.
     * @throws GeneralSecurityException If don't extract the certificates.
     */
    public static CertStore getCertStore(final KeyStore keyStore, final Provider provider)
            throws GeneralSecurityException {
        CertStore certStore;
        if (provider == null) {
            certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(
                    keystore2CertificateCollection(keyStore)));
        } else {
            certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(
                    keystore2CertificateCollection(keyStore)), provider);
        }
        return certStore;
    }

    /**
     * Return the value of <code>provider</code>.
     *
     * @return Value of <code>provider</code>.
     */
    public static Provider getProvider() {
        if (provider == null) {
            provider = Security.getProvider(CertificateUtils.SC_PROVIDER);
            if (provider == null) {
                try {
                    provider = (Provider) Class.forName("org.spongycastle.jce.provider.BouncyCastleProvider").newInstance();
                    Security.addProvider(provider);
                } catch (Exception ex) {
                    provider = null;
                }
            }
            if (provider == null) {
                provider = Security.getProvider(CertificateUtils.BC_PROVIDER);
            }
            if (provider == null) {
                try {
                    provider = (Provider) Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider").newInstance();
                    Security.addProvider(provider);
                } catch (Exception ex) {
                    provider = null;
                }
            }
            Security.removeProvider(provider.getName());
            Security.insertProviderAt(provider, 1);
        }
        return provider;
    }

    /**
     * Convert keystore to certificates collection.
     *
     * @param keyStore Keystore
     * @return Certificates collection.
     * @throws KeyStoreException Error extracting certificates.
     */
    private static Collection<Certificate> keystore2CertificateCollection(final KeyStore keyStore)
            throws KeyStoreException {
        Enumeration<String> aliases = keyStore.aliases();
        List<Certificate> list = new ArrayList<Certificate>();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            list.add(keyStore.getCertificate(alias));
        }
        return list;
    }

    /**
     * Get certificates from flow selected.
     *
     * @param storeType     Type of keystore to get.
     * @param storeStream   Flow to input.
     * @param storePassword Password of keystore.
     * @param provider      Security provider that manage the keystore. If is null use the default provider.
     * @return Keystore with certificates selected.
     * @throws GeneralSecurityException If don't extract the certificates.
     * @throws IOException              Error extracting certificates.
     */
    public static CertStore loadCertStore(final String storeType, final InputStream storeStream,
                                          final char[] storePassword, final Provider provider) throws GeneralSecurityException, IOException {
        KeyStore keyStore;
        keyStore = loadKeyStore(storeType, storeStream, storePassword, provider);
        return getCertStore(keyStore, provider);
    }

    /**
     * Get keystore from flow selected.
     *
     * @param storeType     Type of keystore to get.
     * @param storeStream   Flow to input.
     * @param storePassword Password of keystore.
     * @param provider      Security provider that manage the keystore. If is null use the default provider.
     * @return Keystore with certificates selected.
     * @throws GeneralSecurityException If don't extract the certificates.
     * @throws IOException              Error extracting certificates.
     */
    public static KeyStore loadKeyStore(final String storeType, final InputStream storeStream,
                                        final char[] storePassword, final Provider provider) throws GeneralSecurityException, IOException {
        KeyStore store;
        if (provider == null) {
            store = KeyStore.getInstance(storeType);
        } else {
            store = KeyStore.getInstance(storeType, provider);
        }
        if (storeStream == null) {
            store.load(null);
        } else {
            store.load(storeStream, storePassword);
        }
        return store;
    }

    /**
     * Constructor.
     *
     * @throws InstantiationException Exception if constructor is instantiazed.
     */
    private CertificateUtils() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden.");
    }
} // final class CertificateUtils
