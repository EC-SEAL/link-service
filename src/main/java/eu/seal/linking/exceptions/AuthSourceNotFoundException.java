package eu.seal.linking.exceptions;

public class AuthSourceNotFoundException extends LinkAuthException
{
    public AuthSourceNotFoundException()
    {
        super();
    }

    public AuthSourceNotFoundException(String error)
    {
        super(error);
    }
}
