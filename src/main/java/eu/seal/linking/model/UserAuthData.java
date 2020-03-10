package eu.seal.linking.model;

public class UserAuthData
{
    private String userName;

    private String userPhoto;

    private String authName;

    public UserAuthData()
    {

    }

    public UserAuthData(User user, AuthSource authSource)
    {
        this.userName = (user.getName() + " " + user.getSurname()).trim();
        this.userPhoto = user.getPhotoID();
        this.authName = authSource.getDefaultDisplayName();
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
