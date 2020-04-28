package eu.seal.linking.services;

import eu.seal.linking.exceptions.AuthApiNotFoundException;
import eu.seal.linking.exceptions.AuthSourceNotFoundException;
import eu.seal.linking.exceptions.AuthSourceServicesNotFoundException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.AuthSource;
import eu.seal.linking.model.domain.ApiClassEnum;
import eu.seal.linking.model.domain.EntityMetadata;
import eu.seal.linking.model.domain.EntityMetadataList;
import eu.seal.linking.model.domain.MsMetadata;
import eu.seal.linking.model.domain.MsMetadataList;
import eu.seal.linking.model.domain.PublishedApiType;
import eu.seal.linking.services.cm.ConfMngrConnService;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService
{
    @Autowired
    ConfMngrConnService confMngrConnService;

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
        authSource.setDefaultDisplayName((entityMetadata.getDefaultDisplayName() != null)?
                entityMetadata.getDefaultDisplayName():entityMetadata.getEntityId());
        authSource.setLogo(entityMetadata.getLogo());

        return  authSource;
    }

    public PublishedApiType getAuthApiBySource(String sourceId) throws LinkAuthException
    {
        EntityMetadata entityMetadata = confMngrConnService.getEntityMetadata("AUTHSOURCE", sourceId);

        if (entityMetadata == null)
        {
            throw new AuthSourceNotFoundException("Auth source " + sourceId + "not found");
        }


        MsMetadataList msMetadataList = confMngrConnService.getAllMicroservices();

        MsMetadata service = null;

        for (String serviceId : entityMetadata.getMicroservice())
        {
            for (MsMetadata msMetadata : msMetadataList)
            {
                if (msMetadata.getMsId().equals(serviceId))
                {
                    service = msMetadata;
                    break;
                }
            }
        }

        if (service == null)
        {
            LOG.warn("Services " + String.join(", ", entityMetadata.getMicroservice()) + " not found in microservices list");
            throw new AuthSourceServicesNotFoundException("Services for source " + sourceId + " not found");
        }

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
}
