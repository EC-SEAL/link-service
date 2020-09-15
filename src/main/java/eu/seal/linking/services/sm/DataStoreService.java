package eu.seal.linking.services.sm;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;

public interface DataStoreService
{
    public void addEntry(String sessionId, String objectId, String object)
            throws UnrecoverableKeyException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException;

    public void deleteEntry(String sessionId, String id)
            throws UnrecoverableKeyException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException;

    public void startSession(String sessionId)
            throws UnrecoverableKeyException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException;

    public String getEntry(String sessionId, String id)
            throws UnrecoverableKeyException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException;
}
