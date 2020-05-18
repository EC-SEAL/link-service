package eu.seal.linking.services;

import eu.seal.linking.exceptions.AuthApiNotFoundException;
import eu.seal.linking.exceptions.AuthDeleteSessionException;
import eu.seal.linking.exceptions.AuthGenerateSessionException;
import eu.seal.linking.exceptions.AuthIdPNotFoundException;
import eu.seal.linking.exceptions.AuthLinkRequestException;
import eu.seal.linking.exceptions.AuthNotAuthenticatedException;
import eu.seal.linking.exceptions.AuthSetSessionVariableException;
import eu.seal.linking.exceptions.AuthSourceNotFoundException;
import eu.seal.linking.exceptions.AuthSourceServicesNotFoundException;
import eu.seal.linking.exceptions.AuthStartSessionException;
import eu.seal.linking.exceptions.AuthTokenNotValidatedException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.exceptions.UserNotAuthenticatedException;
import eu.seal.linking.model.AuthRequestData;
import eu.seal.linking.model.AuthSource;
import eu.seal.linking.model.common.ApiClassEnum;
import eu.seal.linking.model.common.AttributeSet;
import eu.seal.linking.model.common.AttributeType;
import eu.seal.linking.model.common.AttributeTypeList;
import eu.seal.linking.model.common.DataSet;
import eu.seal.linking.model.common.DataStore;
import eu.seal.linking.model.common.EntityMetadata;
import eu.seal.linking.model.common.EntityMetadataList;
import eu.seal.linking.model.common.MsMetadata;
import eu.seal.linking.model.common.MsMetadataList;
import eu.seal.linking.model.common.PublishedApiType;
import eu.seal.linking.services.cm.ConfMngrConnService;
import eu.seal.linking.services.commons.ResourceCommons;
import eu.seal.linking.services.sm.SessionManagerConnService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuthService
{
    @Autowired
    ConfMngrConnService confMngrConnService;

    @Autowired
    SessionManagerConnService sessionManagerConnService;

    @Value("${linking.resources.ms.path}")
    private String msResourcePath;

    @Value("${linking.resources.ms.cache}")
    private long msResourceCache;

    @Value("${linking.auth.callback}")
    private String authCallback;

    private final static Logger LOG = LoggerFactory.getLogger(AuthService.class);

    public List<AuthSource> getAuthSources()
    {
        EntityMetadataList entityMetadataList = confMngrConnService.getEntityMetadataSet("AUTHSOURCE");

        List<AuthSource> authSources = new ArrayList<AuthSource>();

        for (EntityMetadata entityMetadata : entityMetadataList)
        {
            authSources.add(getAuthSourceFrom(entityMetadata));
        }

        return authSources;
    }

    private AuthSource getAuthSourceFrom(EntityMetadata entityMetadata)
    {
        AuthSource authSource = new AuthSource();
        authSource.setId(entityMetadata.getEntityId());
        authSource.setDefaultDisplayName((entityMetadata.getDefaultDisplayName() != null) ?
                entityMetadata.getDefaultDisplayName() : entityMetadata.getEntityId());
        authSource.setLogo(entityMetadata.getLogo());

        return authSource;
    }

    public String startSession() throws AuthStartSessionException
    {
        try
        {
            return sessionManagerConnService.startSession();
        }
        catch (Exception e)
        {
            throw new AuthStartSessionException();
        }
    }

    public AuthRequestData generateAuthRequest(String sourceId, String sessionId) throws LinkAuthException
    {
        EntityMetadata entityMetadata = confMngrConnService.getEntityMetadata("AUTHSOURCE", sourceId);

        if (entityMetadata == null)
        {
            throw new AuthSourceNotFoundException("Auth source " + sourceId + " not found");
        }

        //MsMetadataList msMetadataList = confMngrConnService.getAllMicroservices();
        MsMetadataList msMetadataList = getAllMicroservices();
        MsMetadata service = selectService(entityMetadata, msMetadataList);
        PublishedApiType api = getAuthApi(sourceId, service);
        EntityMetadata idpMetadata = getIdP(sourceId, service.getMsId());
        AttributeTypeList attributes = getServiceAttributes(sourceId, idpMetadata);
        AttributeSet idpRequest = generateIdPRequest(idpMetadata, attributes);
        String msToken = generateSession(sessionId, idpMetadata, idpRequest, service.getMsId());

        AuthRequestData authRequestData = new AuthRequestData();
        authRequestData.setMsToken(msToken);
        authRequestData.setEndpoint(api.getApiEndpoint());
        authRequestData.setConnectionType(api.getApiConnectionType().toString());

        return authRequestData;
    }

    private MsMetadata selectService(EntityMetadata source, MsMetadataList microservices)
            throws AuthSourceServicesNotFoundException
    {
        MsMetadata service = null;

        for (String serviceId : source.getMicroservice())
        {
            for (MsMetadata msMetadata : microservices)
            {
                if (msMetadata.getMsId().equals(serviceId))
                {
                    service = msMetadata;
                    break;
                }
            }
            if (service != null)
            {
                break;
            }
        }

        if (service == null)
        {
            LOG.warn("Services " + String.join(", ", source.getMicroservice()) + " not found in microservices list");
            throw new AuthSourceServicesNotFoundException("Services for source " + source.getEntityId() + " not found");
        }

        return service;
    }

    private PublishedApiType getAuthApi(String sourceId, MsMetadata service) throws AuthApiNotFoundException
    {
        PublishedApiType api = null;
        for (PublishedApiType publishedApi : service.getPublishedAPI())
        {
            if (publishedApi.getApiClass().equals(ApiClassEnum.AS) && publishedApi.getApiCall().equals("authenticate"))
            {
                api = publishedApi;
            }
        }

        if (api == null)
        {
            LOG.warn("Auth api not found for service " + service.getMsId());
            throw new AuthApiNotFoundException("Auth Api for source " + sourceId + " not found");
        }

        return api;
    }

    private EntityMetadata getIdP(String sourceId, String service) throws AuthIdPNotFoundException
    {
        EntityMetadata idpMetadata = null;
        try
        {
            EntityMetadataList metadataList = confMngrConnService.getEntityMetadataSet(sourceId.toUpperCase());
            EntityMetadataList msMetadataList = metadataList.getMsEntities(service);
            idpMetadata = msMetadataList.get(0);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage());
            throw new AuthIdPNotFoundException("IdP for source " + service + " not found");
        }

        return idpMetadata;
    }

    private AttributeTypeList getServiceAttributes(String sourceId, EntityMetadata idpMetadata)
    {
        List<String> claims = idpMetadata.getClaims();

        AttributeTypeList attributes = new AttributeTypeList();

        switch (sourceId)
        {
            case "eIDAS":
                AttributeTypeList aux = confMngrConnService.getAttributeSetByProfile(sourceId);
                for (String claim : claims)
                {
                    Optional<AttributeType> foundAtt = null;
                    //foundAtt = aux.stream().filter(a ->a.getFriendlyName().equals(claim) ).findAny();
                    foundAtt = aux.stream().filter(a -> a.getName().contains(claim)).findAny();

                    if (foundAtt != null && foundAtt.isPresent())
                    {
                        attributes.add(foundAtt.get());
                        LOG.info("FoundAtt:" + foundAtt.get());

                    }
                    else
                    {
                        LOG.info("### NOT found: " + claim);
                    }
                }
                break;
            case "eduGAIN":
                AttributeTypeList aux1 = confMngrConnService.getAttributeSetByProfile("eduGain");
                AttributeTypeList aux2 = confMngrConnService.getAttributeSetByProfile("schac");
                for (String claim : claims)
                {
                    Optional<AttributeType> foundAtt = null;
                    foundAtt = aux1.stream().filter(a -> a.getFriendlyName().equals(claim)).findAny();

                    if (foundAtt != null && foundAtt.isPresent())
                    {
                        attributes.add(foundAtt.get());
                        LOG.info("FoundAtt in EDUPERSON:" + foundAtt.get());

                    }
                    else
                    { // Searching in SCHAC
                        Optional<AttributeType> foundAtt2 = null;
                        foundAtt2 = aux2.stream().filter(a -> a.getFriendlyName().equals(claim)).findAny();

                        if (foundAtt2 != null && foundAtt2.isPresent())
                        {
                            attributes.add(foundAtt2.get());
                            LOG.info("FoundAtt in SHAC:" + foundAtt2.get());

                        }
                        else
                        {
                            LOG.info("### NOT found: " + claim);
                        }
                    }
                }
                break;
        }

        return attributes;
    }

    private AttributeSet generateIdPRequest(EntityMetadata idpMetadata, AttributeTypeList attributes)
    {
        AttributeSet idpRequest = new AttributeSet();
        idpRequest.setId(UUID.randomUUID().toString());
        idpRequest.setType(AttributeSet.TypeEnum.REQUEST);
        idpRequest.setInResponseTo("inResponseTo"); //?
        idpRequest.setIssuer("spRequest.getIssuer()");//?
        idpRequest.setRecipient(idpMetadata.getEntityId());
        //idpRequest.setProperties( "spRequest.getProperties()"); //?
        idpRequest.setLoa("spRequest.getLoa()"); //?
        idpRequest.setAttributes(attributes);
        //idpRequest.setStatus("status"); //?
        idpRequest.setNotAfter("notAfter"); //?

        return idpRequest;
    }

    private String generateSession(String sessionId, EntityMetadata idpMetadata, AttributeSet idpRequest, String service)
            throws AuthGenerateSessionException
    {
        String msToken = null;

        try
        {
            // idpMetadata is creating with the eIDAS/eduGain info from the ConfMngr. Saving in the session.
            ObjectMapper objIdpMetadata = new ObjectMapper();
            sessionManagerConnService.updateVariable(sessionId, "idpMetadata", objIdpMetadata.writeValueAsString(idpMetadata));
            // idpRequest is creating. Saving in the session too.
            ObjectMapper objIdpRequest = new ObjectMapper();
            sessionManagerConnService.updateVariable(sessionId, "idpRequest", objIdpRequest.writeValueAsString(idpRequest));
            // Generate token for returning the session.
            msToken = sessionManagerConnService.generateToken(sessionId, service);

            sessionManagerConnService.updateVariable(sessionId, "ClientCallbackAddr", authCallback);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage());
            throw new AuthGenerateSessionException();
        }

        return msToken;
    }

    //TODO: change dataset for attributeset
    public DataSet getAuthenticationDataSet(String sessionId) throws UserNotAuthenticatedException
    {
        try
        {
            Object objDataSet = sessionManagerConnService.readVariable(sessionId, "authenticationSet");
            return (new ObjectMapper()).readValue(objDataSet.toString(), DataSet.class);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            throw new UserNotAuthenticatedException();
        }
    }

    public void logoutLinkService(String sessionId) throws AuthDeleteSessionException
    {
        try
        {
            sessionManagerConnService.deleteSession(sessionId);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            throw new AuthDeleteSessionException();
        }
    }

    public String validateToken(String msToken) throws LinkAuthException
    {
        String sessionId = null;
        try
        {
            sessionId = sessionManagerConnService.validateToken(msToken);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            throw new AuthTokenNotValidatedException(e.getLocalizedMessage());
        }

        if (sessionId.isEmpty())
        {
            throw new AuthNotAuthenticatedException();
        }

        return sessionId;
    }

    public String getLinkRequestFromSession(String sessionId) throws AuthLinkRequestException
    {
        String linkRequest = null;

        try
        {
            linkRequest = (String) sessionManagerConnService.readVariable(sessionId, "linkRequest");
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            throw new AuthLinkRequestException("Not able to read link request from session");
        }

        if (linkRequest.isEmpty())
        {
            throw new AuthLinkRequestException("Link request not found in session");
        }

        return linkRequest;
    }

    public void setVariableInSession(String sessionId, String variableName, String variable)
            throws AuthSetSessionVariableException
    {
        try
        {
            sessionManagerConnService.updateVariable(sessionId, variableName, variable);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            throw new AuthSetSessionVariableException("Variable " + variableName + "could not be set");
        }
    }

    private MsMetadataList getAllMicroservices()
    {
        MsMetadataList msMetadataList = null;

        long lastModified = ResourceCommons.getFileLastUpdate(msResourcePath);
        if (new Date().getTime() > lastModified + msResourceCache)
        {
            msMetadataList = confMngrConnService.getAllMicroservices();

            try
            {
                ObjectMapper objectMapper = new ObjectMapper();
                ResourceCommons.writeFileContent(msResourcePath, objectMapper.writeValueAsString(msMetadataList));
            } catch (IOException e)
            {
                LOG.warn("Error writing micorservices resource", e);
            }
        }

        if (msMetadataList == null)
        {
            try
            {
                FileInputStream resource = new FileInputStream(msResourcePath);
                ObjectMapper objectMapper = new ObjectMapper();

                ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                msMetadataList = mapper.readValue(resource, MsMetadataList.class);
            } catch (Exception e)
            {
                LOG.error("Error reading microservices from cache", e);
            }
        }

        return msMetadataList;
    }

    public void authenticationCallBack(String sessionId) throws Exception
    {
        Object objDataStore = sessionManagerConnService.readVariable(sessionId, "dataStore");
        DataStore dataStore = (new ObjectMapper()).readValue(objDataStore.toString(),DataStore.class);

        DataSet dataSet = dataStore.getClearData() != null ? dataStore.getClearData().get(0) : null;

        if (dataSet != null)
        {
            ObjectMapper objMapper = new ObjectMapper();
            AttributeSet authenticationSet = new AttributeSet ();
            authenticationSet.setId(UUID.randomUUID().toString());
            //authenticationSet.setType(AttributeSet.TypeEnum(myDataset.getType()));
            authenticationSet.setType(AttributeSet.TypeEnum.AUTHRESPONSE);
            authenticationSet.setIssuer(dataSet.getIssuerId());
            authenticationSet.setRecipient(confMngrConnService.getMicroservicesByApiClass("CL").get(0).getMsId()); // The unique client
            authenticationSet.setLoa(dataSet.getLoa());
            authenticationSet.setNotAfter(dataSet.getExpiration());
            authenticationSet.setNotBefore(null);
            authenticationSet.setAttributes(dataSet.getAttributes());

            sessionManagerConnService.updateVariable(sessionId, "authenticationSet",
                    objMapper.writeValueAsString(authenticationSet));
        }
    }
}
