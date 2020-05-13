package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.model.DataSet;
import eu.seal.linking.model.FileObject;
import eu.seal.linking.model.User;
import eu.seal.linking.services.AuthService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("link")
public class FilesController extends BaseController
{
    @Autowired
    private FilesService filesService;

    @RequestMapping(value = "/{requestId}/files/upload", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public Response uploadFile(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken,
                               @RequestParam(required = true) String file)
            throws LinkApplicationException
    {
        //User user = getUserFromSessionToken(sessionToken);

        filesService.storeFileRequest(requestId, file);

        return Response.ok().build();
    }

    @RequestMapping(value = "/{requestId}/files/download/list", method = RequestMethod.GET)
    public List<FileObject> getFilesFromRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken,
                                                HttpSession session) throws LinkApplicationException
    {
        User user = getSessionUser(session);
        return filesService.getFilesFromRequest(requestId, user);
    }

    @RequestMapping(value = "/{requestId}/files/download/{fileId}", method = RequestMethod.GET)
    public FileObject getFileFromRequest(@PathVariable("requestId") String requestId, @PathVariable("fileId") Long fileId,
                                         @RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);
        return filesService.getFileFromRequest(requestId, fileId, user);
    }

}
