package eu.seal.linking.services;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.model.UserCM;
import eu.seal.linking.model.UsersCM;
import eu.seal.linking.services.commons.ResourceCommons;
import eu.seal.linking.services.network.NetworkService;
import eu.seal.linking.utils.CryptoUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UsersCMService
{
    private UsersCM usersCM;

    private Long lastUpdate;

    @Value("${linking.resources.users.path}")
    private String usersResourcePath;

    @Value("${linking.resources.users.cache}")
    private long usersResourceCache;

    @Autowired
    private NetworkService networkService;

    private final static Logger LOG = LoggerFactory.getLogger(UsersCMService.class);

    /*@PostConstruct*/
    private void setUsers()
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
        updateUsersFromCM();

        FileInputStream resource = new FileInputStream(usersResourcePath);
        ObjectMapper objectMapper = new ObjectMapper();

        List<UserCM> users = objectMapper.readValue(resource,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserCM.class));

        return users;
    }

    public UserCM getUserFromId(String userId) throws LinkApplicationException
    {
        if (updateUsersFromCM() || usersCM == null)
        {
            setUsers();
        }

        try
        {
            return usersCM.getUser(CryptoUtils.generateMd5(userId));
        }
        catch (Exception e)
        {
            throw new LinkInternalException();
        }
    }

    //TODO: get users from CM and have a cache
    private boolean updateUsersFromCM()
    {
        long lastModified = ResourceCommons.getFileLastUpdate(usersResourcePath);
        if (new Date().getTime() > lastModified + usersResourceCache)
        {
            try
            {
                String result = networkService.sendGet("http://localhost:8090", "cmtest/users", null, 2);

                if (result != null)
                {
                    ResourceCommons.writeFileContent(usersResourcePath, result);
                    return true;
                }
            }
            catch (Exception e)
            {
                LOG.error(e.getMessage(), e);
            }
        }

        return false;
    }
}
