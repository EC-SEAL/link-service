package eu.seal.linking.model.module;

import eu.seal.linking.model.AuthSource;
import eu.seal.linking.model.User;
import eu.seal.linking.utils.CryptoUtils;

public class UserAuthData
{
    private String userId;

    private String userName;

    private String userPhoto;

    private String authName;

    public UserAuthData()
    {
    }

    public UserAuthData(User user, AuthSource authSource)
    {
        try
        {
            this.userId = CryptoUtils.generateMd5(user.getId());
        } catch (Exception e)
        {
        }

        this.userName = (user.getName() + " " + user.getSurname()).trim();
        this.userPhoto = user.getPhotoID();
        this.authName = authSource.getDefaultDisplayName();
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserPhoto()
    {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto)
    {
        this.userPhoto = userPhoto;
    }

    public String getAuthName()
    {
        return authName;
    }

    public void setAuthName(String authName)
    {
        this.authName = authName;
    }
}
