package com.leads.sandbox.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class SwaggerController {

  @RequestMapping("/")
  public String index() {
    return "redirect:swagger-ui.html";
  }

}