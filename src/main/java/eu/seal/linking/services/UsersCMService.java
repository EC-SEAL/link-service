package eu.seal.linking.services;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.model.UserCM;
import eu.seal.linking.model.UsersCM;
import eu.seal.linking.utils.CryptoUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UsersCMService
{
    private UsersCM usersCM;

    private Long lastUpdate;

    @PostConstruct
    public void initializeUsers()
    {
        usersCM = new UsersCM();

        try
        {
            List<UserCM> users = getUsersFromLocalFile();

            for (UserCM user : users)
            {
                usersCM.setUser(user);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        lastUpdate = new Date().getTime();
    }

    private List<UserCM> getUsersFromLocalFile() throws IOException
    {
        ClassPathResource resource = new ClassPathResource("users-cm.json");
        ObjectMapper objectMapper = new ObjectMapper();

        List<UserCM> users = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserCM.class));

        return users;
    }

    public UserCM getUserFromId(String userId) throws LinkApplicationException
    {
        try
        {
            return usersCM.getUser(CryptoUtils.generateMd5(userId));
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new LinkInternalException();
        }
    }

    //TODO: get users from CM and have a cache
}
