package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.AuthStartSessionException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.AuthRequestData;
import eu.seal.linking.model.AuthSource;
import eu.seal.linking.model.domain.PublishedApiType;
import eu.seal.linking.services.AuthService;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("link/auth")
public class AuthController
{
    @Autowired
    AuthService authService;

    @RequestMapping(value = "/sources", method = RequestMethod.GET)
    public List<AuthSource> getAuthSources()
    {
        return authService.getAuthSources();
    }

    @RequestMapping(value = "/service/{sourceId}")
    public AuthRequestData getAuthService(@PathVariable("sourceId") String sourceId, HttpSession session)
            throws LinkAuthException
    {
        //String sessionId = getSessionId(session);
        String sessionId = authService.startSession();

        AuthRequestData authRequestData = authService.generateAuthRequest(sourceId, sessionId);

        return authRequestData;
    }

    private String getSessionId(HttpSession session) throws AuthStartSessionException
    {
        if (session.getAttribute("sessionID") == null)
        {
            session.setAttribute("sessionID", authService.startSession());
        }

        return (String) session.getAttribute("sessionID");
    }
}
