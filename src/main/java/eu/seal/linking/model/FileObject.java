package eu.seal.linking.model;

public class FileObject
{
    private String filename;

    private String fileID;

    private String contentType;

    private Long fileSize;

    private String content;

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getFileID()
    {
        return fileID;
    }

    public void setFileID(String fileID)
    {
        this.fileID = fileID;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
