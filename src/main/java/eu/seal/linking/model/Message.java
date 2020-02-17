package eu.seal.linking.model;

import eu.seal.linking.exceptions.MessageNotValidException;
import eu.seal.linking.model.enums.UserMessageType;

public class Message
{
    private Long timestamp;

    private String sender;

    private String senderType;

    private String recipient;

    private String recipientType;

    private String message;

    public Long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Long timestamp)
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

    public void validate() throws MessageNotValidException
    {
        if (!senderType.isEmpty() && !UserMessageType.isValid(senderType))
        {
            throw new MessageNotValidException("Message sender type not valid");
        }

        if (!recipientType.isEmpty() && !UserMessageType.isValid(recipientType))
        {
            throw new MessageNotValidException("Message recipient type not valid");
        }
    }
}
