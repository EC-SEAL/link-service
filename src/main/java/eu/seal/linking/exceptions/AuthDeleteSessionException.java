package eu.seal.linking.exceptions;

public class AuthDeleteSessionException extends LinkAuthException
{
    public AuthDeleteSessionException()
    {
        super();
    }

    public AuthDeleteSessionException(String error)
    {
        super(error);
    }
}
