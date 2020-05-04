package eu.seal.linking.model;

import eu.seal.linking.model.domain.PublishedApiType;

public class AuthRequestData
{
    private String msToken;

    private String endpoint;

    private String connectionType;

    public String getMsToken()
    {
        return msToken;
    }

    public void setMsToken(String msToken)
    {
        this.msToken = msToken;
    }

    public String getEndpoint()
    {
        return endpoint;
    }

    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }

    public String getConnectionType()
    {
        return connectionType;
    }

    public void setConnectionType(String connectionType)
    {
        this.connectionType = connectionType;
    }
}
