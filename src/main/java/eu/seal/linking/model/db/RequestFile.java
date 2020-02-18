package eu.seal.linking.model.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "LINK_REQ_FILES")
public class RequestFile
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "REQUEST_ID", referencedColumnName = "ID", nullable = false)
    private Request request;

    private String name;

    @Lob
    private String content;

    @Column(name = "MIME_TYPE")
    private String mimeType;

    private Integer size;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPLOAD_DATE")
    private Date uploadDate;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Request getRequest()
    {
        return request;
    }

    public void setRequest(Request request)
    {
        this.request = request;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public Integer getSize()
    {
        return size;
    }

    public void setSize(Integer size)
    {
        this.size = size;
    }

    public Date getUploadDate()
    {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate)
    {
        this.uploadDate = uploadDate;
    }
}
