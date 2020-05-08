package eu.seal.linking.exceptions;

public class AuthLinkRequestException extends LinkAuthException
{
    public AuthLinkRequestException()
    {
        super();
    }

    public AuthLinkRequestException(String error)
    {
        super(error);
    }
}
