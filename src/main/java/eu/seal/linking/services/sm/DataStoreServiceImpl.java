package eu.seal.linking.services.sm;

import eu.seal.linking.model.RequestParameters;
import eu.seal.linking.model.common.EntityMetadata;
import eu.seal.linking.model.common.NewUpdateDataRequest;
import eu.seal.linking.model.common.SessionMngrResponse;
import eu.seal.linking.services.cm.ConfMngrConnService;
import eu.seal.linking.services.keystore.KeyStoreService;
import eu.seal.linking.services.network.NetworkServiceImpl;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DataStoreServiceImpl implements DataStoreService
{
    @Value("${linking.sm.url}")
    private String hostURL;

    //TODO
    private String sender = null;

    private static final Logger log = LoggerFactory.getLogger(DataStoreServiceImpl.class);

    //private HttpSignatureServiceImpl httpSigService = null;
    private NetworkServiceImpl network = null;

    @Autowired
    private KeyStoreService keyStoreService;

    @Override
    public void addEntry(String sessionId, String objectId, String object)
            throws UnrecoverableKeyException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException
    {
        String service = "/sm/new/add";

        if (network == null)
        {
            network = new NetworkServiceImpl(keyStoreService);
        }

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new NameValuePair("sessionId",sessionId));

        NewUpdateDataRequest updateDR = new NewUpdateDataRequest();
        updateDR.setSessionId(sessionId);
        updateDR.setId(objectId);
        updateDR.setType("linkRequest");
        updateDR.setData(object);

        String contentType="application/json";

        String response = network.sendPostBody(hostURL, service, updateDR, contentType, 1);
    }

    @Override
    public void deleteEntry(String sessionId, String id)
            throws UnrecoverableKeyException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException
    {
        String service = "/sm/new/delete";

        if (network == null)
        {
            network = new NetworkServiceImpl(keyStoreService);
        }

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new NameValuePair("sessionId",sessionId));

        NewUpdateDataRequest updateDR = new NewUpdateDataRequest();
        updateDR.setSessionId(sessionId);
        updateDR.setId(id);
        updateDR.setType("linkRequest");

        String contentType="application/json";

        String response = network.sendPostBody(hostURL, service, updateDR, contentType, 1);
    }

    @Override
    public void startSession(String sessionId)
            throws UnrecoverableKeyException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException
    {
        String service = "/sm/new/startSession";

        if (network == null)
        {
            network = new NetworkServiceImpl(keyStoreService);
        }

        RequestParameters requestParameters = new RequestParameters();
        requestParameters.setSessionId(sessionId);

        String contentType="application/json";

        String response = network.sendPostBody(hostURL, service, requestParameters, contentType, 1);

        /*System.out.println("SMresponse(startSession):" +smResponse.toString());
        System.out.println("sessionID:"+smResponse.getSessionData().getSessionId());*/

        System.out.println(response);
    }

    @Override
    public String getEntry(String sessionId, String id)
            throws UnrecoverableKeyException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException
    {
        String service = "/sm/new/get";

        if (network == null)
        {
            network = new NetworkServiceImpl(keyStoreService);
        }

        RequestParameters requestParameters = new RequestParameters();
        requestParameters.setSessionId(sessionId);
        requestParameters.setId(id);

        /*List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new NameValuePair("sessionId",sessionId));
        urlParameters.add(new NameValuePair("id",id));

        SessionMngrResponse smResponse = network.sendGetSMResponse(hostURL, service, urlParameters, 1);

        String data = smResponse.getAdditionalData();

        ObjectMapper objMapper = new ObjectMapper();
        NewUpdateDataRequest newUpdateDataRequest = objMapper.readValue(data, NewUpdateDataRequest.class);

        return newUpdateDataRequest.getData();*/

        String contentType="application/json";

        String response = network.sendPostBody(hostURL, service, requestParameters, contentType, 1);

        ObjectMapper objMapper = new ObjectMapper();
        SessionMngrResponse sessionMngrResponse = objMapper.readValue(response, SessionMngrResponse.class);
        NewUpdateDataRequest newUpdateDataRequest = objMapper.readValue(sessionMngrResponse.getAdditionalData(),
                NewUpdateDataRequest.class);

        return newUpdateDataRequest.getData();
    }

}
