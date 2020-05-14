package eu.seal.linking.services;

import eu.seal.linking.model.common.DataSet;
import eu.seal.linking.model.common.EntityMetadataList;
import eu.seal.linking.model.common.MsMetadataList;
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
        ClassPathResource resource = new ClassPathResource("admin.json");
        ObjectMapper objectMapper = new ObjectMapper();

        DataSet dataSet = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructType(DataSet.class));

        sessionManagerConnService.updateVariable(sessionId, "authenticationSet", objectMapper.writeValueAsString(dataSet));
    }
}
