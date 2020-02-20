package eu.seal.linking.exceptions;

public class RequestStatusException extends LinkApplicationException
{
    public RequestStatusException()
    {
        super();
    }

    public RequestStatusException(String error)
    {
        super(error);
    }
}
