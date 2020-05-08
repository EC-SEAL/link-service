package eu.seal.linking.exceptions;

public class AuthTokenNotValidatedException extends LinkAuthException
{
    public AuthTokenNotValidatedException()
    {
        super();
    }

    public AuthTokenNotValidatedException(String error)
    {
        super(error);
    }
}
