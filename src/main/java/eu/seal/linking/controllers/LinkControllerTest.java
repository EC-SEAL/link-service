package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.AuthStartSessionException;
import eu.seal.linking.exceptions.DataStoreException;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.AuthRequestData;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.StatusResponse;
import eu.seal.linking.model.User;
import eu.seal.linking.model.common.DataSet;
import eu.seal.linking.model.enums.UserMessageType;
import eu.seal.linking.services.AuthService;
import eu.seal.linking.services.LinkService;
import eu.seal.linking.services.SessionUsersService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.net.HttpResponse;
//import com.sun.deploy.net.HttpResponse;

@RestController
@RequestMapping("test/link")
public class LinkControllerTest
{
    private final static Logger LOG = LoggerFactory.getLogger(LinkController.class);

    @Autowired
    private LinkService linkService;

    @Autowired
    private AuthService authService;

    @Autowired
    private SessionUsersService sessionUsersService;

    @RequestMapping(value = "/request/submit", method = RequestMethod.GET, produces = "application/json")
    public LinkRequest startLinkRequest(@RequestParam(required = true) String msToken, HttpSession session)
            throws LinkApplicationException, IOException, AuthStartSessionException, DataStoreException
    {
        User user = getSessionUser(session);

        // Test with local file
        ClassPathResource resource = new ClassPathResource("request2.json");
        String strRequest = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);

        LinkRequest linkRequest = linkService.storeNewRequest(strRequest, user.getId());

        String sessionId = (String) session.getAttribute("authId");

        if (sessionId == null)
        {
            sessionId = authService.startSession();
            session.setAttribute("authId", sessionId);
            authService.startDataStoreSession(sessionId);
        }

        authService.addLinkRequestToDataStore(sessionId, linkRequest);

        //authService.deleteLinkRequestFromDataStore(sessionId, linkRequest);
        //Checking the datestore
        LinkRequest linkRequest2 = authService.getEntryFromDataStore(sessionId, linkRequest);

        linkRequest2.setLloa("TEST");
        authService.addLinkRequestToDataStore(sessionId, linkRequest2);

        LinkRequest linkRequest3 = authService.getEntryFromDataStore(sessionId, linkRequest2);


        return linkRequest;
    }

    @RequestMapping(value = "/{requestId}/status", method = RequestMethod.GET, produces = "application/json")
    public StatusResponse getRequestStatus(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);

        String requestStatus = linkService.getRequestStatus(requestId);

        return StatusResponse.build(requestStatus);
    }

    @RequestMapping(value = "/{requestId}/cancel", method = RequestMethod.GET)
    public Response cancelRequest(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);

        linkService.cancelRequest(requestId, user.getId());

        return Response.ok().build();
    }

    @RequestMapping(value = "/{requestId}/result/get", produces = "application/json")
    public LinkRequest getRequestResult(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken,
                                        HttpSession session) throws LinkApplicationException
    {
        User user = getSessionUser(session);

        LinkRequest linkRequest =  linkService.getRequestResult(requestId, user.getId());
        linkRequest.buildUriRepresentation("test Issuer");

        //return linkService.getRequestResult(requestId, user.getId());
        return linkRequest;
    }

    private User getSessionUser(HttpSession session) throws LinkApplicationException
    {
        User user = (User) session.getAttribute("user");
        if (user == null)
        {
            user = sessionUsersService.getTestUser("USER");
            session.setAttribute("user", user);
        }

        return user;
    }

    // Testing real services

    @RequestMapping(value = "/request/submit/test", method = RequestMethod.GET, produces = "application/json")
    public String startLinkRequestTest(HttpResponse response, HttpSession session)
            throws IOException, LinkAuthException
    {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null)
        {
            sessionId = authService.startSession();
            session.setAttribute("sessionId", sessionId);
        }
        AuthRequestData authRequestData = authService.generateAuthRequest("eIDAS", sessionId);

        ClassPathResource resource = new ClassPathResource("user.json");
        ObjectMapper objectMapper = new ObjectMapper();

        DataSet dataSet = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructType(DataSet.class));

        authService.setVariableInSession(sessionId, "authenticatedSubject", objectMapper.writeValueAsString(dataSet));

        // Test with local file
        resource = new ClassPathResource("request2.json");
        String strRequest = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);

        authService.setVariableInSession(sessionId, "linkRequest", strRequest);

        // POST

        URL url = new URL("http://localhost:8090/link/request/submit");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        Map<String, String> arguments = new HashMap<>();
        arguments.put("msToken", authRequestData.getMsToken());
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet())
        {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream())
        {
            os.write(out);
        }

        //Do something with http.getInputStream()

        BufferedReader br = new BufferedReader(new InputStreamReader((http.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null)
        {
            sb.append(output);
        }
        return sb.toString();

        //return Response.ok(sb.toString()).build();
    }

    @RequestMapping(value = "/{requestId}/cancel/test", method = RequestMethod.GET)
    public String cancelRequestTest(@PathVariable("requestId") String requestId, HttpSession session)
            throws LinkAuthException, IOException
    {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null)
        {
            sessionId = authService.startSession();
            session.setAttribute("sessionId", sessionId);
        }
        AuthRequestData authRequestData = authService.generateAuthRequest("eIDAS", sessionId);

        ClassPathResource resource = new ClassPathResource("user.json");
        ObjectMapper objectMapper = new ObjectMapper();

        DataSet dataSet = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructType(DataSet.class));

        authService.setVariableInSession(sessionId, "authenticatedSubject", objectMapper.writeValueAsString(dataSet));

        // POST

        URL url = new URL("http://localhost:8090/link/" + requestId + "/cancel");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        Map<String, String> arguments = new HashMap<>();
        arguments.put("msToken", authRequestData.getMsToken());
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet())
        {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream())
        {
            os.write(out);
        }

        //Do something with http.getInputStream()

        BufferedReader br = new BufferedReader(new InputStreamReader((http.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null)
        {
            sb.append(output);
        }
        return sb.toString();

        //return Response.ok().build();
    }

    @RequestMapping(value = "/{requestId}/status/test", method = RequestMethod.GET, produces = "application/json")
    public StatusResponse getRequestStatusTest(@PathVariable("requestId") String requestId, HttpSession session)
            throws LinkAuthException, IOException
    {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null)
        {
            sessionId = authService.startSession();
            session.setAttribute("sessionId", sessionId);
        }
        AuthRequestData authRequestData = authService.generateAuthRequest("eIDAS", sessionId);

        ClassPathResource resource = new ClassPathResource("user.json");
        ObjectMapper objectMapper = new ObjectMapper();

        DataSet dataSet = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructType(DataSet.class));

        authService.setVariableInSession(sessionId, "authenticatedSubject", objectMapper.writeValueAsString(dataSet));

        // GET
        URL url = new URL("http://localhost:8090/link/" + requestId + "/status");    //?sessionToken=" + sessionId);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("GET");
        http.setDoOutput(true);
        http.connect();


        //Do something with http.getInputStream()

        BufferedReader br = new BufferedReader(new InputStreamReader((http.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null)
        {
            sb.append(output);
        }
        //return sb.toString();

        return StatusResponse.build(sb.toString());
    }

    @RequestMapping(value = "/{requestId}/result/get/test", produces = "application/json")
    public String getRequestResultTest(@PathVariable("requestId") String requestId, HttpSession session)
            throws LinkAuthException, IOException
    {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null)
        {
            sessionId = authService.startSession();
            session.setAttribute("sessionId", sessionId);
        }
        AuthRequestData authRequestData = authService.generateAuthRequest("eIDAS", sessionId);

        ClassPathResource resource = new ClassPathResource("user.json");
        ObjectMapper objectMapper = new ObjectMapper();

        DataSet dataSet = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructType(DataSet.class));

        authService.setVariableInSession(sessionId, "authenticatedSubject", objectMapper.writeValueAsString(dataSet));

        // POST

        URL url = new URL("http://localhost:8090/link/" + requestId + "/result/get");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        Map<String, String> arguments = new HashMap<>();
        arguments.put("msToken", authRequestData.getMsToken());
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet())
        {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream())
        {
            os.write(out);
        }

        //Do something with http.getInputStream()

        BufferedReader br = new BufferedReader(new InputStreamReader((http.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null)
        {
            sb.append(output);
        }
        return sb.toString();
    }

    @RequestMapping(value = "/{requestId}/files/upload", produces = "application/json")
    public String uploadFileTest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String update)
            throws LinkAuthException, IOException
    {
        String sessionId = authService.startSession();
        AuthRequestData authRequestData = authService.generateAuthRequest("eIDAS", sessionId);

        ClassPathResource resource = new ClassPathResource("user.json");
        ObjectMapper objectMapper = new ObjectMapper();

        DataSet dataSet = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructType(DataSet.class));

        authService.setVariableInSession(sessionId, "authenticatedSubject", objectMapper.writeValueAsString(dataSet));

        String file = (update == null) ? "file.json" : "file2.json";
        resource = new ClassPathResource(file);
        String strFile = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);


        // POST

        URL url = new URL("http://localhost:8090/link/" + requestId + "/files/upload"); //?sessionToken=" + sessionId);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        Map<String, String> arguments = new HashMap<>();
        arguments.put("file", strFile);
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet())
        {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream())
        {
            os.write(out);
        }

        //Do something with http.getInputStream()

        BufferedReader br = new BufferedReader(new InputStreamReader((http.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null)
        {
            sb.append(output);
        }
        return sb.toString();
    }

    @RequestMapping(value = "/{requestId}/messages/send/{recipient:requester|officer}", method = RequestMethod.GET)
    public String sendMessageTest(@PathVariable("requestId") String requestId, @PathVariable("recipient") String recipient,
                                    HttpSession session)
            throws LinkApplicationException, LinkAuthException, IOException
    {
        String strMessage = null;
        String sessionId = null;

        if (recipient.equals(UserMessageType.OFFICER.toString()))
        {
            sessionId = authService.startSession();
            AuthRequestData authRequestData = authService.generateAuthRequest("eIDAS", sessionId);

            ClassPathResource resource = new ClassPathResource("user.json");
            ObjectMapper objectMapper = new ObjectMapper();

            DataSet dataSet = objectMapper.readValue(resource.getInputStream(),
                    objectMapper.getTypeFactory().constructType(DataSet.class));

            authService.setVariableInSession(sessionId, "authenticatedSubject", objectMapper.writeValueAsString(dataSet));

            // Test with local files
            resource = new ClassPathResource("message-requester.json");
            strMessage = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        }
        else if (recipient.equals(UserMessageType.REQUESTER.toString()))
        {
            // NOT WORKING: when connects, generates another session
            session.setAttribute("user", sessionUsersService.getTestUser("ADMIN"));

            // Test with local files
            ClassPathResource resource = new ClassPathResource("message-officer.json");
            strMessage = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        }

        // POST

        URL url = new URL("http://localhost:8090/link/" + requestId + "/messages/send/" + recipient +
                "?sessionToken=" + sessionId);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        Map<String, String> arguments = new HashMap<>();
        arguments.put("message", strMessage);
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet())
        {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream())
        {
            os.write(out);
        }

        //Do something with http.getInputStream()

        BufferedReader br = new BufferedReader(new InputStreamReader((http.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null)
        {
            sb.append(output);
        }
        return sb.toString();
    }

    @RequestMapping(value = "/{requestId}/messages/receive", method = RequestMethod.GET, produces = "application/json")
    public String getConversationTest(@PathVariable("requestId") String requestId)
            throws LinkAuthException, IOException
    {
        String sessionId = authService.startSession();
        AuthRequestData authRequestData = authService.generateAuthRequest("eIDAS", sessionId);

        ClassPathResource resource = new ClassPathResource("user.json");
        ObjectMapper objectMapper = new ObjectMapper();

        DataSet dataSet = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructType(DataSet.class));

        authService.setVariableInSession(sessionId, "authenticatedSubject", objectMapper.writeValueAsString(dataSet));

        // GET
        URL url = new URL("http://localhost:8090/link/" + requestId + "/messages/receive"); //?sessionToken=" + sessionId);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("GET");
        http.setDoOutput(true);
        http.connect();


        //Do something with http.getInputStream()

        BufferedReader br = new BufferedReader(new InputStreamReader((http.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null)
        {
            sb.append(output);
        }

        return sb.toString();
    }
}
