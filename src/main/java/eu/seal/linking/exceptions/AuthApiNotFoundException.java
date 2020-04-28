package eu.seal.linking.exceptions;

public class AuthApiNotFoundException extends LinkAuthException
{
    public AuthApiNotFoundException()
    {
        super();
    }

    public AuthApiNotFoundException(String error)
    {
        super(error);
    }
}
