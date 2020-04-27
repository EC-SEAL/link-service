package eu.seal.linking.services;

import eu.seal.linking.model.AuthSource;
import eu.seal.linking.model.domain.EntityMetadata;
import eu.seal.linking.model.domain.EntityMetadataList;
import eu.seal.linking.services.cm.ConfMngrConnService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService
{
    @Autowired
    ConfMngrConnService confMngrConnService;

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
}
