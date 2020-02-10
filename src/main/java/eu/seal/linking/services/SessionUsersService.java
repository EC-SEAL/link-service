package eu.seal.linking.services;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.exceptions.UserNotAuthenticatedException;
import eu.seal.linking.model.AttributeType;
import eu.seal.linking.model.DataSet;
import eu.seal.linking.model.User;
import eu.seal.linking.model.UserCM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SessionUsersService
{
    @Autowired
    private UsersCMService usersCMService;

    // For tests purposes
    public User getTestUser() throws LinkApplicationException
    {
        try
        {
            DataSet dataSet = getDataSetFromLocalFile("USER");
            if (dataSet == null)
            {
                throw new UserNotAuthenticatedException();
            }

            User user = getUser(dataSet);
            return user;
        } catch (IOException e) {
            throw new LinkInternalException();
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
            if (attributeType.getName().equals(idAttribute))
            {
                id = attributeType.getValues().get(0);
            }
        }

        return id;
    }

    private User getUser(DataSet dataSet) throws LinkApplicationException
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
}
