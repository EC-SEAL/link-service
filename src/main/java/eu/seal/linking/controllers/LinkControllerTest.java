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
        //ClassPathResource resource = new ClassPathResource("request2.json");
        ClassPathResource resource = new ClassPathResource("request-atos.json");
        //String strRequest = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        String strRequest = "{\"id\":\"urn:mace:project-seal.eu:link:ManualLinkms001:128052%40gn-vho.grnet.gr:https%3A%2F%2Feid-proxy.aai-dev.grnet.gr%2FSaml2IDP%2Fproxy.xml:GR%2FGR%2FERMIS-11076669:eIDAS_GR\",\"issuer\":\"ManualLinkms001\",\"lloa\":null,\"issued\":\"Thu Dec 03 10:37:05 GMT 2020\",\"type\":null,\"expiration\":null,\"uri\":null,\"datasetA\":{\"id\":\"e8de74e0-a701-4c41-aafe-f1c00aa383d6\",\"type\":\"eduGAIN\",\"categories\":null,\"issuerId\":\"issuerEntityId\",\"subjectId\":\"eduPersonPrincipalName\",\"loa\":null,\"issued\":\"Thu, 3 Dec 2020 09:42:22 GMT\",\"expiration\":null,\"attributes\":[{\"name\":\"issuerEntityId\",\"friendlyName\":\"issuerEntityId\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"https://eid-proxy.aai-dev.grnet.gr/Saml2IDP/proxy.xml\"]},{\"name\":\"urn:oid:1.3.6.1.4.1.5923.1.1.1.10\",\"friendlyName\":\"eduPersonTargetedID\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[null]},{\"name\":\"urn:oid:2.5.4.42\",\"friendlyName\":\"givenName\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"ΧΡΙΣΤΙΝΑ CHRISTINA\"]},{\"name\":\"urn:oid:0.9.2342.19200300.100.1.3\",\"friendlyName\":\"mail\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"seal-test0@example.com\"]},{\"name\":\"urn:oid:2.5.4.3\",\"friendlyName\":\"cn\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"ΠΑΛΙΟΚΩΣΤΑ PALIOKOSTA ΧΡΙΣΤΙΝΑ CHRISTINA\"]},{\"name\":\"urn:oid:2.5.4.4\",\"friendlyName\":\"sn\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"ΠΑΛΙΟΚΩΣΤΑ PALIOKOSTA\"]},{\"name\":\"urn:oid:2.16.840.1.113730.3.1.241\",\"friendlyName\":\"displayName\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"ΧΡΙΣΤΙΝΑ CHRISTINA ΠΑΛΙΟΚΩΣΤΑ PALIOKOSTA\"]},{\"name\":\"urn:oid:1.3.6.1.4.1.5923.1.1.1.6\",\"friendlyName\":\"eduPersonPrincipalName\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"128052@gn-vho.grnet.gr\"]},{\"name\":\"urn:oid:1.3.6.1.4.1.5923.1.1.1.7\",\"friendlyName\":\"eduPersonEntitlement\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"urn:mace:grnet.gr:seal:test\"]}],\"properties\":null},\"datasetB\":{\"id\":\"fb93a014-626c-4175-83ea-853d02cbf107\",\"type\":\"eIDAS\",\"categories\":null,\"issuerId\":\"eidasDatasetIssuer\",\"subjectId\":\"PersonIdentifier\",\"loa\":\"1\",\"issued\":\"Thu, 3 Dec 2020 09:41:15 GMT\",\"expiration\":null,\"attributes\":[{\"name\":\"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName\",\"friendlyName\":\"FamilyName\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"ΠΕΤΡΟΥ PETROU\"]},{\"name\":\"http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName\",\"friendlyName\":\"GivenName\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"ΑΝΔΡΕΑΣ ANDREAS\"]},{\"name\":\"http://eidas.europa.eu/attributes/naturalperson/DateOfBirth\",\"friendlyName\":\"DateOfBirth\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"1980-01-01\"]},{\"name\":\"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier\",\"friendlyName\":\"PersonIdentifier\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"GR/GR/ERMIS-11076669\"]},{\"name\":\"http://eidas.europa.eu/LoA\",\"friendlyName\":\"LevelOfAssurance\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"1\"]},{\"name\":\"eidasDatasetIssuer\",\"friendlyName\":\"eidasDatasetIssuer\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"eIDAS_GR\"]}],\"properties\":null},\"evidence\":null,\"conversation\":null}";

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
        //resource = new ClassPathResource("request2.json");
        resource = new ClassPathResource("request3.json");
        String strRequest = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        //strRequest = "{\"id\":\"urn:mace:project-seal.eu:link:ManualLinkms001:128052%40gn-vho.grnet.gr:https%3A%2F%2Feid-proxy.aai-dev.grnet.gr%2FSaml2IDP%2Fproxy.xml:GR%2FGR%2FERMIS-11076669:eIDAS_GR\",\"issuer\":\"ManualLinkms001\",\"lloa\":null,\"issued\":\"Thu Dec 03 10:37:05 GMT 2020\",\"type\":null,\"expiration\":null,\"uri\":null,\"datasetA\":{\"id\":\"e8de74e0-a701-4c41-aafe-f1c00aa383d6\",\"type\":\"eduGAIN\",\"categories\":null,\"issuerId\":\"issuerEntityId\",\"subjectId\":\"eduPersonPrincipalName\",\"loa\":null,\"issued\":\"Thu, 3 Dec 2020 09:42:22 GMT\",\"expiration\":null,\"attributes\":[{\"name\":\"issuerEntityId\",\"friendlyName\":\"issuerEntityId\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"https://eid-proxy.aai-dev.grnet.gr/Saml2IDP/proxy.xml\"]},{\"name\":\"urn:oid:1.3.6.1.4.1.5923.1.1.1.10\",\"friendlyName\":\"eduPersonTargetedID\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[null]},{\"name\":\"urn:oid:2.5.4.42\",\"friendlyName\":\"givenName\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"ΧΡΙΣΤΙΝΑ CHRISTINA\"]},{\"name\":\"urn:oid:0.9.2342.19200300.100.1.3\",\"friendlyName\":\"mail\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"seal-test0@example.com\"]},{\"name\":\"urn:oid:2.5.4.3\",\"friendlyName\":\"cn\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"ΠΑΛΙΟΚΩΣΤΑ PALIOKOSTA ΧΡΙΣΤΙΝΑ CHRISTINA\"]},{\"name\":\"urn:oid:2.5.4.4\",\"friendlyName\":\"sn\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"ΠΑΛΙΟΚΩΣΤΑ PALIOKOSTA\"]},{\"name\":\"urn:oid:2.16.840.1.113730.3.1.241\",\"friendlyName\":\"displayName\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"ΧΡΙΣΤΙΝΑ CHRISTINA ΠΑΛΙΟΚΩΣΤΑ PALIOKOSTA\"]},{\"name\":\"urn:oid:1.3.6.1.4.1.5923.1.1.1.6\",\"friendlyName\":\"eduPersonPrincipalName\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"128052@gn-vho.grnet.gr\"]},{\"name\":\"urn:oid:1.3.6.1.4.1.5923.1.1.1.7\",\"friendlyName\":\"eduPersonEntitlement\",\"encoding\":null,\"language\":null,\"mandatory\":null,\"values\":[\"urn:mace:grnet.gr:seal:test\"]}],\"properties\":null},\"datasetB\":{\"id\":\"fb93a014-626c-4175-83ea-853d02cbf107\",\"type\":\"eIDAS\",\"categories\":null,\"issuerId\":\"eidasDatasetIssuer\",\"subjectId\":\"PersonIdentifier\",\"loa\":\"1\",\"issued\":\"Thu, 3 Dec 2020 09:41:15 GMT\",\"expiration\":null,\"attributes\":[{\"name\":\"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName\",\"friendlyName\":\"FamilyName\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"ΠΕΤΡΟΥ PETROU\"]},{\"name\":\"http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName\",\"friendlyName\":\"GivenName\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"ΑΝΔΡΕΑΣ ANDREAS\"]},{\"name\":\"http://eidas.europa.eu/attributes/naturalperson/DateOfBirth\",\"friendlyName\":\"DateOfBirth\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"1980-01-01\"]},{\"name\":\"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier\",\"friendlyName\":\"PersonIdentifier\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"GR/GR/ERMIS-11076669\"]},{\"name\":\"http://eidas.europa.eu/LoA\",\"friendlyName\":\"LevelOfAssurance\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"1\"]},{\"name\":\"eidasDatasetIssuer\",\"friendlyName\":\"eidasDatasetIssuer\",\"encoding\":\"UTF-8\",\"language\":\"N/A\",\"mandatory\":null,\"values\":[\"eIDAS_GR\"]}],\"properties\":null},\"evidence\":null,\"conversation\":null}";

        authService.setVariableInSession(sessionId, "linkRequest", strRequest);

        // POST

        String msToken = "eyJhbGciOiJSUzI1NiJ9.eyJzZXNzaW9uSWQiOiI2NDI0YjA0Mi1mZjFjLTRjNGItYmIyYS0yYTMwN2M1NTcwYTMiLCJzZW5kZXIiOiJDTG1zMDAxIiwicmVjZWl2ZXIiOiJNYW51YWxMaW5rbXMwMDEiLCJkYXRhIjoiZXh0cmFEYXRhIiwiaXNzIjoiIiwianRpIjoiNjM1ODYyMzMtMTVmYi00M2NiLTk0NzEtMGEzZDQxMmZkOTVmIiwiaWF0IjoxNjA2OTk1Njg4LCJleHAiOjE2MDY5OTU5ODh9.FSrqoTTXDpwo1rwjFTkAWdUBTZcZ5gCNoFjO4mAawaP1dwrr-FMTEi3sKFwgMeeRkBRtI8VF5Rg5wTU4IhEixhRdbppbk8lcEMD02MJX90HTtTgAByuWU293oDqoNMGC3HqoFXN-8vcW4Oc1wk7c_pmisINQP9UT7BPylKIHBcCGB2vIdF4t8GuCxGJbyk8TDnW1N7rX-4w9PDI-z2clpkgiotIIRKmRf48QK8JNoprycg2ECwV4J65PXVWfTso3KARh1yG111TOgDgLptJJoBGufuF8wHkyG1ptTYDqMCBmFy8u1OqhYFr6IFo8VHU8eHTJF9Eojn0-VBNUc_RKuA";
        URL url = new URL("http://localhost:8093/link/request/submit");
        //URL url = new URL("https://vm.project-seal.eu:8093/link/request/submit");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        Map<String, String> arguments = new HashMap<>();
        arguments.put("msToken", authRequestData.getMsToken());
        //arguments.put("msToken", msToken);
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

    @RequestMapping(value = "/{requestId}/result/get/test", produces = "text/html")
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

        URL url = new URL("http://localhost:8093/link/" + requestId + "/result/get");
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
