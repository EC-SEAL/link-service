package eu.seal.linking.exceptions;

public class AuthNotAuthenticatedException extends LinkAuthException
{
    public AuthNotAuthenticatedException()
    {
        super();
    }

    public AuthNotAuthenticatedException(String error)
    {
        super(error);
    }
}
