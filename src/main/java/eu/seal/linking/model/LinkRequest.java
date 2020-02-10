package eu.seal.linking.model;

import java.util.HashMap;
import java.util.List;

public class LinkRequest
{
    private String id;

    private String issuer;

    private String aSubjectId;

    private String aSubjectIssuer;

    private String bSubjectId;

    private String bSubjectIssuer;

    private String lloa;

    private String issued;

    private String expiration;

    private List<AttributeType> aAttributes;

    private HashMap<String,String> aProperties;

    private List<AttributeType> bAttributes;

    private HashMap<String,String> bProperties;

    private List<FileObject> evidence;

    private List<Message> conversation;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getIssuer()
    {
        return issuer;
    }

    public void setIssuer(String issuer)
    {
        this.issuer = issuer;
    }

    public String getaSubjectId()
    {
        return aSubjectId;
    }

    public void setaSubjectId(String aSubjectId)
    {
        this.aSubjectId = aSubjectId;
    }

    public String getaSubjectIssuer()
    {
        return aSubjectIssuer;
    }

    public void setaSubjectIssuer(String aSubjectIssuer)
    {
        this.aSubjectIssuer = aSubjectIssuer;
    }

    public String getbSubjectId()
    {
        return bSubjectId;
    }

    public void setbSubjectId(String bSubjectId)
    {
        this.bSubjectId = bSubjectId;
    }

    public String getbSubjectIssuer()
    {
        return bSubjectIssuer;
    }

    public void setbSubjectIssuer(String bSubjectIssuer)
    {
        this.bSubjectIssuer = bSubjectIssuer;
    }

    public String getLloa()
    {
        return lloa;
    }

    public void setLloa(String lloa)
    {
        this.lloa = lloa;
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

    public List<AttributeType> getaAttributes()
    {
        return aAttributes;
    }

    public void setaAttributes(List<AttributeType> aAttributes)
    {
        this.aAttributes = aAttributes;
    }

    public HashMap<String, String> getaProperties()
    {
        return aProperties;
    }

    public void setaProperties(HashMap<String, String> aProperties)
    {
        this.aProperties = aProperties;
    }

    public List<AttributeType> getbAttributes()
    {
        return bAttributes;
    }

    public void setbAttributes(List<AttributeType> bAttributes)
    {
        this.bAttributes = bAttributes;
    }

    public HashMap<String,String> getbProperties()
    {
        return bProperties;
    }

    public void setbProperties(HashMap<String,String> bProperties)
    {
        this.bProperties = bProperties;
    }

    public List<FileObject> getEvidence()
    {
        return evidence;
    }

    public void setEvidence(List<FileObject> evidence)
    {
        this.evidence = evidence;
    }

    public List<Message> getConversation()
    {
        return conversation;
    }

    public void setConversation(List<Message> conversation)
    {
        this.conversation = conversation;
    }
}
