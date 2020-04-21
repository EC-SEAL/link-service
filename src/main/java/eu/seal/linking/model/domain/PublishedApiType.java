package eu.seal.linking.model.domain;

public class PublishedApiType
{
    private ApiClassEnum apiClass = null;

    private String apiCall = null;

    private ApiConnectionType apiConnectionType = null;

    private String apiEndpoint = null;

    public ApiClassEnum getApiClass()
    {
        return apiClass;
    }

    public void setApiClass(ApiClassEnum apiClass)
    {
        this.apiClass = apiClass;
    }

    public String getApiCall()
    {
        return apiCall;
    }

    public void setApiCall(String apiCall)
    {
        this.apiCall = apiCall;
    }

    public ApiConnectionType getApiConnectionType()
    {
        return apiConnectionType;
    }

    public void setApiConnectionType(ApiConnectionType apiConnectionType)
    {
        this.apiConnectionType = apiConnectionType;
    }

    public String getApiEndpoint()
    {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint)
    {
        this.apiEndpoint = apiEndpoint;
    }
}
