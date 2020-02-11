package eu.seal.linking.model.db;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "LINK_REQUESTS")
public class Request
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String uid;

    @Column(name = "REQUESTER_ID")
    private String requesterId;

    @Column(name = "OWNER_ID")
    private String ownerId;

    @Column(name = "AGENT_ID")
    private String agentId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ENTRY_DATE")
    private Date entryDate;

    @Lob
    private String strRequest;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATE")
    private Date lastUpdate;

    private String status;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<RequestDomain> domains;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<RequestFile> files;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<RequestMessage> messages;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getRequesterId()
    {
        return requesterId;
    }

    public void setRequesterId(String requesterId)
    {
        this.requesterId = requesterId;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getAgentId()
    {
        return agentId;
    }

    public void setAgentId(String agentId)
    {
        this.agentId = agentId;
    }

    public Date getEntryDate()
    {
        return entryDate;
    }

    public void setEntryDate(Date entryDate)
    {
        this.entryDate = entryDate;
    }

    public String getStrRequest()
    {
        return strRequest;
    }

    public void setStrRequest(String strRequest)
    {
        this.strRequest = strRequest;
    }

    public Date getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public List<RequestDomain> getDomains()
    {
        return domains;
    }

    public void setDomains(List<RequestDomain> domains)
    {
        this.domains = domains;
    }

    public List<RequestFile> getFiles()
    {
        return files;
    }

    public void setFiles(List<RequestFile> files)
    {
        this.files = files;
    }

    public List<RequestMessage> getMessages()
    {
        return messages;
    }

    public void setMessages(List<RequestMessage> messages)
    {
        this.messages = messages;
    }
}
