package eu.seal.linking.services;

import eu.seal.linking.model.DataSet;
import eu.seal.linking.model.domain.EntityMetadataList;
import eu.seal.linking.model.domain.MsMetadataList;
import eu.seal.linking.services.cm.ConfMngrConnService;
import eu.seal.linking.services.sm.SessionManagerConnService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TestService
{
    @Autowired
    private ConfMngrConnService confMngrConnService;

    @Autowired
    private SessionManagerConnService sessionManagerConnService;

    public MsMetadataList pruebaServiciosCM()
    {
        MsMetadataList msMetadataList = confMngrConnService.getAllMicroservices();

        return msMetadataList;
    }

    public EntityMetadataList getAuthSources()
    {
        return confMngrConnService.getEntityMetadataSet("AUTHSOURCE");
    }

    public void setMockAuthDataSet(String sessionId) throws Exception
    {
        DataSet authenticationSet = new DataSet();

        ClassPathResource resource = new ClassPathResource("admin.json");
        ObjectMapper objectMapper = new ObjectMapper();

        DataSet dataSet = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructType(DataSet.class));

        ObjectMapper objIdpMetadata = new ObjectMapper();
        sessionManagerConnService.updateVariable(sessionId, "authenticationSet", objIdpMetadata.writeValueAsString(authenticationSet));
    }
}
