package zhongchiedu.compent;

import java.util.Map;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;


@Component
public class MyErrorAttributes  extends DefaultErrorAttributes{
	
	
	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
		Map<String, Object> map=super.getErrorAttributes(webRequest, includeStackTrace);
		map.put("mymsg", "this is the wrong title");
		return map;
	}

}
