package eu.seal.linking.exceptions;

public class LinkInternalException extends LinkApplicationException
{
    public LinkInternalException()
    {
        super();
    }

    public LinkInternalException(String error)
    {
        super(error);
    }
}
