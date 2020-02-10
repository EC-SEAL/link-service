package eu.seal.linking.model;

import java.util.List;

public class UserCM
{
    private String hashID;

    private String nameAttr;

    private String surnameAttr;

    private List<String> entitlements;

    private String photoID;

    private String email;

    public String getHashID()
    {
        return hashID;
    }

    public void setHashID(String hashID)
    {
        this.hashID = hashID;
    }

    public String getNameAttr()
    {
        return nameAttr;
    }

    public void setNameAttr(String nameAttr)
    {
        this.nameAttr = nameAttr;
    }

    public String getSurnameAttr()
    {
        return surnameAttr;
    }

    public void setSurnameAttr(String surnameAttr)
    {
        this.surnameAttr = surnameAttr;
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
