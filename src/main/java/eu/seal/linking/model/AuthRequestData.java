package eu.seal.linking.model;

import eu.seal.linking.model.domain.PublishedApiType;

public class AuthRequestData
{
    private String msToken;

    private PublishedApiType publishedApi;

    public String getMsToken()
    {
        return msToken;
    }

    public void setMsToken(String msToken)
    {
        this.msToken = msToken;
    }

    public PublishedApiType getPublishedApi()
    {
        return publishedApi;
    }

    public void setPublishedApi(PublishedApiType publishedApi)
    {
        this.publishedApi = publishedApi;
    }
}
