package eu.seal.linking.exceptions;

public class AuthStartSessionException extends LinkAuthException
{
    public AuthStartSessionException()
    {
        super();
    }

    public AuthStartSessionException(String error)
    {
        super(error);
    }
}
