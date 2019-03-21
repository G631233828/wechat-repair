package zhongchiedu.controller.WebRepair.wx;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/"})
public class OAuthController {

	
	 @RequestMapping({"MP_verify_ioJTu5WvkIxOE6Oc.txt"})
	 @ResponseBody
	    private String returnConfigFile() {
		 return "ioJTu5WvkIxOE6Oc";
	 };

}
