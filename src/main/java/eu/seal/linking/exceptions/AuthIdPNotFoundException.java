package eu.seal.linking.exceptions;

public class AuthIdPNotFoundException extends LinkAuthException
{
    public AuthIdPNotFoundException()
    {
        super();
    }

    public AuthIdPNotFoundException(String error)
    {
        super(error);
    }
}
