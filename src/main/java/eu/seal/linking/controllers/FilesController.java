package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.IDLinkingException;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.model.FileObject;
import eu.seal.linking.model.User;
import eu.seal.linking.services.FilesService;

import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("link")
public class FilesController extends BaseController
{
    @Autowired
    private FilesService filesService;

    @RequestMapping(value = "/{requestId}/files/upload", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public Response uploadFile(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken,
                               @RequestParam(required = true) String file)
            throws IDLinkingException
    {
        //User user = getUserFromSessionToken(sessionToken);

        try
        {
            filesService.storeFileRequest(requestId, file);

            return Response.ok().build();
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/files/download/list", method = RequestMethod.GET)
    public List<FileObject> getFilesFromRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            User user = getUserFrom(sessionToken);
            return filesService.getFilesFromRequest(requestId, user);
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/files/download/{fileId}", method = RequestMethod.GET)
    public FileObject getFileFromRequest(@PathVariable("requestId") String requestId, @PathVariable("fileId") Long fileId,
                                         @RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            User user = getUserFrom(sessionToken);
            return filesService.getFileFromRequest(requestId, fileId, user);
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

}
