package eu.seal.linking.model;

import java.util.List;

public class AttributeType
{
    private String name;

    private String friendlyName;

    private String encoding;

    private String language;

    private Boolean mandatory;

    private List<String> values;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public Boolean isMandatory()
    {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory)
    {
        mandatory = mandatory;
    }

    public List<String> getValues()
    {
        return values;
    }

    public void setValues(List<String> values)
    {
        this.values = values;
    }
}
