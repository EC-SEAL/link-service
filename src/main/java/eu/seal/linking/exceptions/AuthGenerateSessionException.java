package eu.seal.linking.exceptions;

public class AuthGenerateSessionException extends LinkAuthException
{
    public AuthGenerateSessionException()
    {
        super();
    }

    public AuthGenerateSessionException(String error)
    {
        super(error);
    }
}
