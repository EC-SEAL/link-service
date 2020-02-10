package eu.seal.linking.model;

import eu.seal.linking.exceptions.UserNotFoundException;

import java.util.HashMap;

public class UsersCM
{
    private HashMap<String, UserCM> users;

    public UsersCM()
    {
        users = new HashMap<String, UserCM>();
    }

    public UserCM getUser(String hashID) throws UserNotFoundException
    {
        UserCM user = users.get(hashID);

        if (user == null)
        {
            throw new UserNotFoundException();
        }

        return user;
    }

    public void setUser(UserCM user)
    {
        users.put(user.getHashID(), user);
    }
}
