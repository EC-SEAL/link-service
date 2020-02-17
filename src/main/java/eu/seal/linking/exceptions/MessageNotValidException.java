package eu.seal.linking.exceptions;

public class MessageNotValidException extends LinkApplicationException
{
    public  MessageNotValidException()
    {
        super();
    }

    public MessageNotValidException(String error)
    {
        super(error);
    }
}
