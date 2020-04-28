package eu.seal.linking.exceptions;

public class LinkAuthException extends Exception
{
    public LinkAuthException()
    {
        super();
    }

    public LinkAuthException(String error)
    {
        super(error);
    }
}
