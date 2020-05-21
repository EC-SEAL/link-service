package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.AuthStartSessionException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.AuthRequestData;
import eu.seal.linking.model.AuthSource;
import eu.seal.linking.services.AuthService;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("link/auth")
public class AuthController extends BaseController
{
    @Autowired
    AuthService authService;

    @RequestMapping(value = "/sources", method = RequestMethod.GET)
    public List<AuthSource> getAuthSources()
    {
        return authService.getAuthSources();
    }

    @RequestMapping(value = "/service/{sourceId}")
    public AuthRequestData getAuthService(@PathVariable("sourceId") String sourceId, HttpServletResponse response)
            throws LinkAuthException
    {
        String sessionId = authService.startSession();

        AuthRequestData authRequestData = authService.generateAuthRequest(sourceId, sessionId);

        //mockup
        //authRequestData.setEndpoint("http://localhost:8090/cmtest/auth");

        response.addCookie(createCookie(sessionId, 3600));

        return authRequestData;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Response logout(@CookieValue(name = SEAL_COOKIE, defaultValue = "") String sessionId, HttpServletResponse response) throws LinkAuthException
    {
        authService.logoutLinkService(sessionId);

        response.addCookie(createCookie(sessionId, 0));

        return Response.ok().build();
    }

    @RequestMapping(value="callback")
    public void authCallBack(@CookieValue(name = SEAL_COOKIE, defaultValue = "") String sessionId, HttpServletResponse response) throws AuthStartSessionException, Exception
    {
        authService.authenticationCallBack(sessionId);

        response.sendRedirect("/validator/main");
    }
}
