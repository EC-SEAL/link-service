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

    public static boolean isValid(String value)
    {
        for (UserMessageType type : UserMessageType.values())
        {
            if (type.toString().equals(value))
            {
                return true;
            }
        }

        return false;
    }
}
