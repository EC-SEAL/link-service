package eu.seal.linking.model;

public class Message
{
    private Integer timestamp;

    private String sender;

    private String senderType;

    private String recipient;

    private String recipientType;

    private String message;

    public Integer getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public String getSenderType()
    {
        return senderType;
    }

    public void setSenderType(String senderType)
    {
        this.senderType = senderType;
    }

    public String getRecipient()
    {
        return recipient;
    }

    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }

    public String getRecipientType()
    {
        return recipientType;
    }

    public void setRecipientType(String recipientType)
    {
        this.recipientType = recipientType;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
