package eu.seal.linking.services.commons;

import eu.seal.linking.exceptions.AuthStartSessionException;
import eu.seal.linking.services.AuthService;

import javax.servlet.http.HttpSession;

public class SessionCommons
{
    public static String getSessionId(HttpSession session, AuthService authService) throws AuthStartSessionException
    {
        if (session.getAttribute("sessionID") == null)
        {
            session.setAttribute("sessionID", authService.startSession());
        }

        return (String) session.getAttribute("sessionID");
    }
}
