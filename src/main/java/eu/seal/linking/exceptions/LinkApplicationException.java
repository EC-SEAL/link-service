package eu.seal.linking.exceptions;

public class LinkApplicationException extends Exception
{
    public LinkApplicationException()
    {
        super();
    }

    public LinkApplicationException(String error)
    {
        super(error);
    }
}
