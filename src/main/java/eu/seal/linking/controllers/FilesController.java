package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.model.FileObject;
import eu.seal.linking.model.User;
import eu.seal.linking.services.FilesService;
import eu.seal.linking.services.SessionUsersService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("link")
public class FilesController
{
    @Autowired
    private FilesService filesService;

    @Autowired
    private SessionUsersService sessionUsersService;

    @RequestMapping(value = "/{requestId}/files/upload", method = RequestMethod.GET)
    // TODO: file param
    public Response uploadFile(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken,
                               @RequestParam(required = false) String update, HttpSession session)
            throws LinkApplicationException, IOException
    {
        User user = getSessionUser(session, "USER");

        // Test with local files
        String file = (update == null) ? "file.json" : "file2.json";
        ClassPathResource resource = new ClassPathResource(file);
        String strFile = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);

       filesService.storeFileRequest(requestId, strFile, user);

        return Response.ok().build();
    }

    @RequestMapping(value = "/{requestId}/files/download/list", method = RequestMethod.GET)
    public List<FileObject> getFilesFromRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken,
                                                HttpSession session) throws LinkApplicationException
    {
        User user = getSessionUser(session, "ADMIN");
        return filesService.getFilesFromRequest(requestId, user);
    }

    @RequestMapping(value = "/{requestId}/files/download/{fileId}", method = RequestMethod.GET)
    public FileObject getFileFromRequest(@PathVariable("requestId") String requestId, @PathVariable("fileId") Long fileId,
                                         @RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session, "ADMIN");
        return filesService.getFileFromRequest(requestId, fileId, user);
    }

    private User getSessionUser(HttpSession session, String userType) throws LinkApplicationException
    {
        User user = null;

        if (userType.equals("USER"))
        {
            user = (User) session.getAttribute("user");
        }
        else
        {
            user = (User) session.getAttribute("user2");
        }

        if (user == null)
        {
            user = sessionUsersService.getTestUser(userType);
            if (userType.equals("USER"))
            {
                session.setAttribute("user", user);
            }
            else
            {
                session.setAttribute("user2", user);
            }
        }

        return user;
    }
}
