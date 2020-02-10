package eu.seal.linking.exceptions;

public class RequestException extends LinkApplicationException
{
    public RequestException()
    {
        super();
    }

    public RequestException(String error)
    {
        super(error);
    }
}
