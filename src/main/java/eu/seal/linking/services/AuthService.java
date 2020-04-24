package eu.seal.linking.services;

import eu.seal.linking.model.domain.EntityMetadataList;
import eu.seal.linking.services.cm.ConfMngrConnService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService
{
    @Autowired
    ConfMngrConnService confMngrConnService;

    public EntityMetadataList getAuthSources()
    {
        return confMngrConnService.getEntityMetadataSet("AUTHSOURCE");
    }
}
