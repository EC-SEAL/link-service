package eu.seal.linking.model.common;

import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SessionData
 */
@Validated

public class SessionData
{
    @JsonProperty("sessionId")
    private String sessionId = null;

    @JsonProperty("sessionVariables")
    private Object sessionVariables = null;

    public SessionData sessionId(String sessionId)
    {
        this.sessionId = sessionId;
        return this;
    }

    /**
     * Get sessionId
     *
     * @return sessionId
     **/
    @ApiModelProperty(value = "")


    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public SessionData sessionVariables(Object sessionVariables)
    {
        this.sessionVariables = sessionVariables;
        return this;
    }

    /**
     * Dictionary of session variables retrieved from the SM
     *
     * @return sessionVariables
     **/
    @ApiModelProperty(value = "Dictionary of session variables retrieved from the SM")


    public Object getSessionVariables()
    {
        return sessionVariables;
    }

    public void setSessionVariables(Object sessionVariables)
    {
        this.sessionVariables = sessionVariables;
    }


    @Override
    public boolean equals(java.lang.Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        SessionData sessionData = (SessionData) o;
        return Objects.equals(this.sessionId, sessionData.sessionId) &&
                Objects.equals(this.sessionVariables, sessionData.sessionVariables);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(sessionId, sessionVariables);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class SessionData {\n");

        sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
        sb.append("    sessionVariables: ").append(toIndentedString(sessionVariables)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o)
    {
        if (o == null)
        {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

