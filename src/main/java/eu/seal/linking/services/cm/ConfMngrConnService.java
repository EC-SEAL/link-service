package eu.seal.linking.services.cm;

import eu.seal.linking.model.domain.AttributeTypeList;
import eu.seal.linking.model.domain.EntityMetadata;
import eu.seal.linking.model.domain.EntityMetadataList;
import eu.seal.linking.model.domain.MsMetadataList;

import java.util.List;

public interface ConfMngrConnService
{
    public List<String> getAttributeProfiles ();
    public AttributeTypeList getAttributeSetByProfile(String profileId);
    //getMappingList (String profileId);

    public List<String> getExternalEntities (); // returns available **collections**
    public EntityMetadataList getEntityMetadataSet (String collectionId);
    public EntityMetadata getEntityMetadata (String collectionId, String entityId);

    public MsMetadataList getAllMicroservices ();
    public MsMetadataList getMicroservicesByApiClass (String apiClasses); // input like "SP, IDP, AP, GW, ACM, SM, CM"

    public List<String> getInternalConfs (); // returns available internal configurations
    public EntityMetadata getConfiguration (String confId);
}
