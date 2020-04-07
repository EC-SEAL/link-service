package eu.seal.linking.model;

import java.util.HashMap;
import java.util.List;

public class LinkRequest
{
    private String id;

    private String issuer;

    private String lloa;

    private String issued;

    private String type;

    private String expiration;

    private Dataset datasetA;

    private Dataset datasetB;

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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getExpiration()
    {
        return expiration;
    }

    public void setExpiration(String expiration)
    {
        this.expiration = expiration;
    }

    public Dataset getDatasetA()
    {
        return datasetA;
    }

    public void setDatasetA(Dataset datasetA)
    {
        this.datasetA = datasetA;
    }

    public Dataset getDatasetB()
    {
        return datasetB;
    }

    public void setDatasetB(Dataset datasetB)
    {
        this.datasetB = datasetB;
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
