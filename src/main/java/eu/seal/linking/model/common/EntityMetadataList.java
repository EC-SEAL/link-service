package eu.seal.linking.model.common;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.validation.annotation.Validated;

/**
 * EntityMetadataList
 */
@Validated

public class EntityMetadataList extends ArrayList<EntityMetadata>  {

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EntityMetadataList {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    //Get the entity with a given entityId
    public EntityMetadata getEntityById (String entityId) {

        EntityMetadata theEntity = null;

        EntityMetadata anEntity;
        Iterator<EntityMetadata> entityMetadataIterator = this.iterator();
        while (entityMetadataIterator.hasNext()) {
            anEntity = entityMetadataIterator.next();
            if (anEntity.getEntityId().equals(entityId)) {
                theEntity = anEntity;
                break;
            }
        }

        return theEntity;
    }

    //Get the list of entities with a given ms
    public EntityMetadataList getMsEntities (String ms) {

        EntityMetadataList msEntities = new EntityMetadataList();

        EntityMetadata em;
        Iterator<EntityMetadata> msMetadataIterator = this.iterator();
        while (msMetadataIterator.hasNext()) {
            em = msMetadataIterator.next();
            if (!em.getMicroservice().isEmpty()) {
                if (em.getMicroservice().contains(ms))
                    msEntities.add (em);
            }
        }

        return msEntities;
    }
}
