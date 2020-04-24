package eu.seal.linking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("validator")
public class InterfaceController
{
    @GetMapping("main")
    public String getMainPage()
    {
        return "main";
    }

    @GetMapping("auth")
    public String getAuthPage()
    {
        return "auth";
    }
}
