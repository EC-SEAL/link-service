package eu.seal.linking.model;

import java.util.HashMap;
import java.util.List;

public class Dataset
{
    private String id;

    private String type;

    private List<String> categories;

    private String issuerId;

    private String subjectId;

    private String loa;

    private String issued;

    private String expiration;

    private List<AttributeType> attributes;

    private HashMap<String,String> properties;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public List<String> getCategories()
    {
        return categories;
    }

    public void setCategories(List<String> categories)
    {
        this.categories = categories;
    }

    public String getIssuerId()
    {
        return issuerId;
    }

    public void setIssuerId(String issuerId)
    {
        this.issuerId = issuerId;
    }

    public String getSubjectId()
    {
        return subjectId;
    }

    public void setSubjectId(String subjectId)
    {
        this.subjectId = subjectId;
    }

    public String getLoa()
    {
        return loa;
    }

    public void setLoa(String loa)
    {
        this.loa = loa;
    }

    public String getIssued()
    {
        return issued;
    }

    public void setIssued(String issued)
    {
        this.issued = issued;
    }

    public String getExpiration()
    {
        return expiration;
    }

    public void setExpiration(String expiration)
    {
        this.expiration = expiration;
    }

    public List<AttributeType> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(List<AttributeType> attributes)
    {
        this.attributes = attributes;
    }

    public HashMap<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties)
    {
        this.properties = properties;
    }
}
