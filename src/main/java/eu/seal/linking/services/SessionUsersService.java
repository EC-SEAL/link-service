package eu.seal.linking.services;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.exceptions.UserNotAuthenticatedException;
import eu.seal.linking.model.AuthSource;
import eu.seal.linking.model.User;
import eu.seal.linking.model.UserCM;
import eu.seal.linking.model.common.AttributeType;
import eu.seal.linking.model.common.DataSet;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SessionUsersService
{
    @Autowired
    private UsersCMService usersCMService;

    // For tests purposesse
    public User getTestUser(String userType) throws LinkApplicationException
    {
        try
        {
            DataSet dataSet = getDataSetFromLocalFile(userType);
            if (dataSet == null)
            {
                throw new UserNotAuthenticatedException();
            }

            User user = getUser(dataSet);
            return user;
        } catch (IOException e) {
            throw new LinkInternalException(e.getMessage());
        }
    }

    public DataSet getDataSetFromLocalFile(String userType) throws IOException
    {
        String file;
        if (userType.equals("USER"))
        {
            file = "user.json";
        }
        else
        {
            file = "admin.json";
        }

        ClassPathResource resource = new ClassPathResource(file);
        ObjectMapper objectMapper = new ObjectMapper();

        DataSet dataSet = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructType(DataSet.class));

        return dataSet;
    }

    private String getUserIdFrom(DataSet dataSet)
    {
        String idAttribute = dataSet.getSubjectId();
        String id = null;

        for (AttributeType attributeType : dataSet.getAttributes())
        {
            if (attributeType.getName().equals(idAttribute) || attributeType.getFriendlyName().equals(idAttribute))
            {
                id = attributeType.getValues().get(0);
            }
        }

        return id;
    }

    public User getUser(DataSet dataSet) throws LinkApplicationException
    {
        String userId = getUserIdFrom(dataSet);
        UserCM userCM = usersCMService.getUserFromId(userId);

        User user = new User();
        user.setId(userId);

        for (AttributeType attributeType : dataSet.getAttributes())
        {
            if (attributeType.getName().equals(userCM.getNameAttr()))
            {
                user.setName(String.join(" ", attributeType.getValues()));
            }
            else if (attributeType.getName().equals(userCM.getSurnameAttr()))
            {
                user.setSurname(String.join(" ", attributeType.getValues()));
            }
        }

        user.setEntitlements(new ArrayList<String>(userCM.getEntitlements()));
        user.setPhotoID(userCM.getPhotoID());
        user.setEmail(userCM.getEmail());

        return user;
    }

    public AuthSource getTestAuthSource() throws LinkInternalException
    {
        ClassPathResource resource = new ClassPathResource("authsource.json");
        ObjectMapper objectMapper = new ObjectMapper();

        AuthSource authSource = null;
        try
        {
            authSource = objectMapper.readValue(resource.getInputStream(),
                    objectMapper.getTypeFactory().constructType(AuthSource.class));
        }
        catch (IOException e)
        {
            throw new LinkInternalException(e.getMessage());
        }

        return  authSource;
    }
}
