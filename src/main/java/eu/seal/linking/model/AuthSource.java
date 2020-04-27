package eu.seal.linking.model;

// entityMetadata definition
public class AuthSource
{
    private String id;

    private String defaultDisplayName;

    private String logo;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getDefaultDisplayName()
    {
        return defaultDisplayName;
    }

    public void setDefaultDisplayName(String defaultDisplayName)
    {
        this.defaultDisplayName = defaultDisplayName;
    }

    public String getLogo()
    {
        return logo;
    }

    public void setLogo(String logo)
    {
        this.logo = logo;
    }
}
