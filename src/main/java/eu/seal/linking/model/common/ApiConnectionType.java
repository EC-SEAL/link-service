package eu.seal.linking.model.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ApiConnectionType
{
    post("post"),

    get("get");

    private String value;

    ApiConnectionType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static ApiConnectionType fromValue(String text) {
        for (ApiConnectionType b : ApiConnectionType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
