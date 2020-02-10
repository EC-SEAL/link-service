package eu.seal.linking.exceptions;

public class RequestNotFoundException extends LinkApplicationException
{
    public RequestNotFoundException()
    {
        super();
    }

    public RequestNotFoundException(String error)
    {
        super(error);
    }
}
