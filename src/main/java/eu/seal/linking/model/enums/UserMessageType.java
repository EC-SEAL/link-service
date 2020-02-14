package eu.seal.linking.model.enums;

public enum UserMessageType
{
    OFFICER,
    REQUESTER;

    @Override
    public String toString()
    {
        return name().toLowerCase();
    }
}
