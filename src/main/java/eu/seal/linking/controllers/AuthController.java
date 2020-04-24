package eu.seal.linking.controllers;

import eu.seal.linking.model.domain.EntityMetadataList;
import eu.seal.linking.services.AuthService;
import eu.seal.linking.services.cm.ConfMngrConnService;

import org.springframework.beans.factory.annotation.Autowired;
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
    public EntityMetadataList getAuthSources()
    {
        return authService.getAuthSources();
    }
}
