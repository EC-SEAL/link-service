package eu.seal.linking.controllers;

import eu.seal.linking.model.UserCM;
import eu.seal.linking.model.common.EntityMetadataList;
import eu.seal.linking.model.common.MsMetadataList;
import eu.seal.linking.services.TestService;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("cmtest")
public class TestController
{

    @Autowired
    private TestService testService;

    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public MsMetadataList getServices()
    {
        return testService.pruebaServiciosCM();
    }

    @RequestMapping(value = "/authsources", method = RequestMethod.GET)
    public EntityMetadataList getAuthSources()
    {
        return testService.getAuthSources();
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public void authTestService(@RequestParam String msToken, HttpServletRequest request,
                                HttpServletResponse response, HttpSession session) throws Exception
    {
        testService.setMockAuthData((String) session.getAttribute("sessionID")); // have to be defined

        String forward = request.getHeader("referer");

        response.setHeader("Location", forward);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    }


    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<UserCM> getUsersCM() throws IOException
    {
        ClassPathResource resource = new ClassPathResource("users-cm.json");
        ObjectMapper objectMapper = new ObjectMapper();

        List<UserCM> users = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserCM.class));

        return users;
    }
}
