package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.UserNotAuthenticatedException;
import eu.seal.linking.model.User;
import eu.seal.linking.model.UserCM;
import eu.seal.linking.model.common.AttributeType;
import eu.seal.linking.model.common.DataSet;
import eu.seal.linking.services.AuthService;
import eu.seal.linking.services.SessionUsersService;
import eu.seal.linking.services.UsersCMService;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController
{
    @Autowired
    protected AuthService authService;

    @Autowired
    protected SessionUsersService sessionUsersService;

    @Autowired
    protected UsersCMService usersCMService;

    protected final static String SEAL_COOKIE = "SEALSessionID";

    protected User getUserFrom(String sessionId) throws LinkApplicationException
    {
        DataSet authentication = authService.getAuthenticationDataSet(sessionId);

        String userId = getUserIdFrom(authentication);
        UserCM userCM = usersCMService.getUserFromId(userId);

        User user = new User();
        user.setId(userId);

        for (AttributeType attributeType : authentication.getAttributes())
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

    protected String getUserIdFrom(String sessionToken) throws UserNotAuthenticatedException
    {
        DataSet dataSet = authService.getAuthenticationDataSet(sessionToken);

        return getUserIdFrom(dataSet);
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

    protected Cookie createCookie(String id, int expiration)
    {
        Cookie cookie = new Cookie(SEAL_COOKIE, id);
        cookie.setPath("/");
        cookie.setMaxAge(expiration);

        return cookie;
    }

}
