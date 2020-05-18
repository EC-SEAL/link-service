package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.AuthStartSessionException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.AuthRequestData;
import eu.seal.linking.model.AuthSource;
import eu.seal.linking.services.AuthService;
import eu.seal.linking.services.commons.SessionCommons;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

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
        String sessionId = SessionCommons.getSessionId(session, authService);

        AuthRequestData authRequestData = authService.generateAuthRequest(sourceId, sessionId);

        //mockup
        //authRequestData.setEndpoint("http://localhost:8090/cmtest/auth");

        return authRequestData;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Response logout(HttpSession session) throws LinkAuthException
    {
        String sessionId = SessionCommons.getSessionId(session, authService);

        authService.logoutLinkService(sessionId);
        session.removeAttribute("sessionID");
        session.removeAttribute("user");

        return Response.ok().build();
    }

    @RequestMapping(value="callback")
    public void authCallBack(HttpSession session, HttpServletResponse response) throws AuthStartSessionException, Exception
    {
        authService.authenticationCallBack(SessionCommons.getSessionId(session, authService));

        response.sendRedirect("/validator/main");
    }
}
