package eu.seal.linking.services.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nimbusds.jose.JWSAlgorithm;


@Service
public class KeyStoreServiceImpl implements KeyStoreService {


    private final String keyPass;
    //private final String jwtKeyAlias;
    private final String httpSigKeyAlias;
    //private final String jweKeyAlias;
    private final String httpSigAttempts;
    private final String asyncSignature;
    private final String secretKey;


    private KeyStore keystore;

    //private ParameterService paramServ;

    private final static Logger LOG = LoggerFactory.getLogger(KeyStoreServiceImpl.class);


    @Autowired
    @Inject
    public KeyStoreServiceImpl(@Value("${linking.keystore.path}") String certPath, @Value("${linking.keystore.key.pass}")  String keyPass,
                               @Value("${linking.keystore.pass}") String storePass,
                               @Value("${linking.keystore.httpsig.cert.alias}") String httpSigKeyAlias,
                               @Value("${linking.keystore.httpsig.attempts}") String httpSigAttempts,
                               @Value("${linking.keystore.async.signature}") String asyncSignature,
                               @Value("${linking.keystore.signing.secret}") String secretKey)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {

        this.keyPass = keyPass;
        this.httpSigKeyAlias = httpSigKeyAlias;
        this.asyncSignature = asyncSignature;
        this.secretKey = secretKey;

        this.httpSigAttempts = (StringUtils.isEmpty(httpSigAttempts))?"2":httpSigAttempts;

        LOG.info ("certPath: " + certPath);
//        LOG.info ("jwtKeyAlias: " + jwtKeyAlias);
        LOG.info ("httpSigKeyAlias: " + httpSigKeyAlias);
//        LOG.info ("jweKeyAlias: " + jweKeyAlias);

        keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.parseBoolean(asyncSignature))
        {
            //LOG.info ("ASYNC_SIGNATURE: true");
            File jwtCertFile = new File(certPath);
            InputStream certIS = new FileInputStream(jwtCertFile);
            keystore.load(certIS, storePass.toCharArray());
            //LOG.info ("keystore: loaded");
        } else {
            //init an empty keystore otherwise an exception is thrown
            keystore.load(null, null);
        }

    }

    public int getNumAttempts() {
        return Integer.parseInt(httpSigAttempts);
    }

    public Key getHttpSigningKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException {
        //"httpsigkey"
        //return keystore.getKey(keyAlias, "keypassword".toCharArray());
        //String asyncSignature = paramServ.getParam("ASYNC_SIGNATURE");
        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.valueOf(asyncSignature)) {
            //LOG.info ("GettingHttpSigningKey....");
            return keystore.getKey(httpSigKeyAlias, keyPass.toCharArray());
        }
        //String secretKey = paramServ.getParam("SIGNING_SECRET");
        return new SecretKeySpec(secretKey.getBytes("UTF-8"), 0, secretKey.length(), "HmacSHA256");
    }

    public Key getHttpSigPublicKey() throws KeyStoreException, UnsupportedEncodingException {
        //"httpSignaturesAlias"
        Certificate cert = keystore.getCertificate(httpSigKeyAlias);
        return cert.getPublicKey();

    }

    public KeyStore getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStore keystore) {
        this.keystore = keystore;
    }

    @Override
    public JWSAlgorithm getAlgorithm() {
        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.parseBoolean(asyncSignature)) {
            return JWSAlgorithm.RS256;
        }
        return JWSAlgorithm.HS256;
    }

}
