package eu.seal.linking.model;

import eu.seal.linking.exceptions.BuildUriRepresentationException;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.model.common.DataSet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private String uri;

    private DataSet datasetA;

    private DataSet datasetB;

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

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public DataSet getDatasetA()
    {
        return datasetA;
    }

    public void setDatasetA(DataSet datasetA)
    {
        this.datasetA = datasetA;
    }

    public DataSet getDatasetB()
    {
        return datasetB;
    }

    public void setDatasetB(DataSet datasetB)
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

    public void buildUriRepresentation(String linkIssuerId)
            throws LinkApplicationException
    {
        if (lloa == null)
        {
           throw new BuildUriRepresentationException("No LLoA provided");
        }
        else if (linkIssuerId == null)
        {
            throw new BuildUriRepresentationException("No link issuer ID provided");
        }
        else if (datasetA == null || datasetA.getSubjectId() == null)
        {
            throw new BuildUriRepresentationException("No subject A id provided");
        }
        else if (datasetA.getIssuerId() == null)
        {
            throw new BuildUriRepresentationException("No issuer A id provided");
        }
        else if (datasetB == null || datasetB.getSubjectId() == null)
        {
            throw new BuildUriRepresentationException("No subject B id provided");
        }
        else if (datasetB.getIssuerId() == null)
        {
            throw new BuildUriRepresentationException("No issuer B id provided");
        }

        try
        {
            String identityA = datasetA.getSubjectId() + ":" + datasetA.getIssuerId();
            String identityB = datasetB.getSubjectId() + ":" + datasetB.getIssuerId();

            String firstIdentity = null;
            String secondIdentity = null;

            if (identityA.compareTo(identityB) <= 0)
            {
                firstIdentity = identityA;
                secondIdentity = identityB;
            }
            else
            {
                firstIdentity = identityB;
                secondIdentity = identityA;
            }

            uri = "urn:mace:project-seal.eu:link:" + URLEncoder.encode(linkIssuerId, StandardCharsets.UTF_8.toString()) +
                    ":" + URLEncoder.encode(lloa, StandardCharsets.UTF_8.toString()) + ":" + firstIdentity + ":" +
                    secondIdentity;
        } catch (UnsupportedEncodingException e)
        {
            throw new LinkInternalException(e.getMessage());
        }
    }
}
