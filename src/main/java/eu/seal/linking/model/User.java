package eu.seal.linking.model;

import java.util.List;

public class User
{
    private String id;

    private String name;

    private String surname;

    private List<String> entitlements;

    private String photoID;

    private String email;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public List<String> getEntitlements()
    {
        return entitlements;
    }

    public void setEntitlements(List<String> entitlements)
    {
        this.entitlements = entitlements;
    }

    public String getPhotoID()
    {
        return photoID;
    }

    public void setPhotoID(String photoID)
    {
        this.photoID = photoID;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
