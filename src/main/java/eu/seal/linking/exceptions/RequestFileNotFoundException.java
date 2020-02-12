package eu.seal.linking.exceptions;

public class RequestFileNotFoundException extends LinkApplicationException
{
    public RequestFileNotFoundException()
    {
        super();
    }

    public RequestFileNotFoundException(String error)
    {
        super(error);
    }
}
