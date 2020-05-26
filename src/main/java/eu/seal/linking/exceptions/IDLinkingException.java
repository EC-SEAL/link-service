package eu.seal.linking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class IDLinkingException extends Exception
{
    public IDLinkingException()
    {
        super();
    }

    public IDLinkingException(String error)
    {
        super(error);
    }
}
