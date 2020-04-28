package eu.seal.linking.exceptions;

public class AuthSourceServicesNotFoundException extends LinkAuthException
{
    public AuthSourceServicesNotFoundException()
    {
        super();
    }

    public AuthSourceServicesNotFoundException(String error)
    {
        super(error);
    }
}
