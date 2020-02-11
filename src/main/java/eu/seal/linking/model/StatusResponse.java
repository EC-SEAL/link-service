package eu.seal.linking.model;

public class StatusResponse
{
    private String primaryCode;

    private String secondaryCode;

    private String message;

    public String getPrimaryCode()
    {
        return primaryCode;
    }

    public void setPrimaryCode(String primaryCode)
    {
        this.primaryCode = primaryCode;
    }

    public String getSecondaryCode()
    {
        return secondaryCode;
    }

    public void setSecondaryCode(String secondaryCode)
    {
        this.secondaryCode = secondaryCode;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public static StatusResponse build(String primaryCode)
    {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setPrimaryCode(primaryCode);
        return statusResponse;
    }
}
