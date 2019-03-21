package zhongchiedu.controller.WebRepair.wx;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class GlobalException  implements HandlerExceptionResolver{

	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exp) {
		String ServletPath=request.getServletPath();
		String requestURi=request.getRequestURI();
		log.info("servletpath:{},requesturi:{}",ServletPath,requestURi);
		return null;
	}

	
}
