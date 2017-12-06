package com.ericsson.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController 
{
	/*@RequestMapping(value = "/index", method = RequestMethod.GET)
	   public String index() {
	      return "index";
	   }
	   @RequestMapping(value = "/prodresrc", method = RequestMethod.GET)
	   public String redirect() {
	      return "redirect:pages/appresource-prod.html";
	   }
	   
	   @RequestMapping(value = "/devresrc", method = RequestMethod.GET)
	   public String redirectDev() {
	      return "redirect:pages/appresource-dev.html";
	   }
*/	   
	   @RequestMapping(value = { "/production" }, method = RequestMethod.GET)
		public String productionPage() {

			ModelAndView model = new ModelAndView();
			model.addObject("title", "Spring Security Hello World");
			model.addObject("message", "This is welcome page!");
			model.setViewName("appresource-prod");
			return "appresource-prod";

		}
	   
	   @RequestMapping(value = { "/lab" }, method = RequestMethod.GET)
		public String labPage() {

			ModelAndView model = new ModelAndView();
			model.addObject("title", "Spring Security Hello World");
			model.addObject("message", "This is welcome page!");
			model.setViewName("appresource-dev");
			return "appresource-dev";

		}
	   
	   @RequestMapping(value = { "/home" }, method = RequestMethod.GET)
		public String homePage() {

			ModelAndView model = new ModelAndView();
			model.addObject("title", "Spring Security Hello World");
			model.addObject("message", "This is welcome page!");
			model.setViewName("home");
			return "home";

		}
	   
	   @RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
		public String loginPage() {

			ModelAndView model = new ModelAndView();
			model.addObject("title", "Spring Security Hello World");
			model.addObject("message", "This is welcome page!");
			model.setViewName("login");
			return "loginPage";

		}
}
