package eu.seal.linking.model.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "LINK_SERVICES_CACHE")
public class ServicesCache
{
    @Id
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATE")
    private Date lastUpddate;

    @Lob
    private String services;

    public ServicesCache()
    {
    }

    public ServicesCache(String id, String services)
    {
        this.id = id;
        this.lastUpddate = new Date();
        this.services = services;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Date getLastUpddate()
    {
        return lastUpddate;
    }

    public void setLastUpddate(Date lastUpddate)
    {
        this.lastUpddate = lastUpddate;
    }

    public String getServices()
    {
        return services;
    }

    public void setServices(String services)
    {
        this.services = services;
    }
}
