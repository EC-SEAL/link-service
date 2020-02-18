package eu.seal.linking.model;

public class FileObject
{
    private String filename;

    private String fileID;

    private String contentType;

    private Integer fileSize;

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

    public Integer getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Integer fileSize)
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
