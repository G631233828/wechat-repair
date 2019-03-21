package zhongchiedu.controller.WebRepair.wx;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import zhongchiedu.WxRepair.pojo.RepairMsg;
import zhongchiedu.WxRepair.pojo.Repairman;
import zhongchiedu.WxRepair.pojo.Teacher;
import zhongchiedu.WxRepair.service.Impl.RepairMsgServiceImpl;
import zhongchiedu.WxRepair.service.Impl.RepairmanServiceImpl;
import zhongchiedu.WxRepair.service.Impl.TeacherServiceImpl;
import zhongchiedu.WxRepair.util.MsgStatus;
import zhongchiedu.WxRepair.util.TypeRepair;
import zhongchiedu.wx.mp.config.WxMpConfiguration;

@Slf4j
@Controller
@RequestMapping("/wx/redirect/{appid}")
public class WxRedirectController {

	@Autowired
	private TeacherServiceImpl teacherService;
	
	@Autowired
	private RepairmanServiceImpl repairmanService;
	
	@Autowired
	private RepairMsgServiceImpl repairMsgService;
		
	
		/**
		 * 微信菜单维修入口
		 * @param appid
		 * @param code
		 * @param map
		 * @return
		 */
		@RequestMapping("/index")
		public String toLogin(@PathVariable String appid, 
				@RequestParam(value="code",defaultValue="") String code, ModelMap map) {
	      if(code!="") {
			WxMpService mpService = WxMpConfiguration.getMpServices().get(appid);
	        try {
	            WxMpOAuth2AccessToken accessToken = mpService.oauth2getAccessToken(code);
	            WxMpUser user = mpService.oauth2getUserInfo(accessToken, null);
	            Repairman man=repairmanService.findByOpenid(user.getOpenId());
	            if(man!=null) {
	            	map.put("man", man);
	            	return "repair/wxrepair/rm/index";
	            }
	            map.put("user", user);
	        } catch (WxErrorException e) {
	            e.printStackTrace();
	        } 
	      }
	        return "repair/wxrepair/rm/login";	
		}
		
		/**
		 * 模拟二维码跳转入口
		 * @param appid
		 * @param code
		 * @param map
		 * @return
		 * @throws WxErrorException
		 */
		@RequestMapping("/repairIndex")
		public String toIndex(@PathVariable String appid, 
				@RequestParam String code, ModelMap map) throws WxErrorException {
	        WxMpService mpService = WxMpConfiguration.getMpServices().get(appid);
	     	WxMpOAuth2AccessToken accessToken = mpService.oauth2getAccessToken(code);
			WxMpUser user = mpService.oauth2getUserInfo(accessToken, null);
	        Teacher t=this.teacherService.findByOpenId(user.getOpenId()); 
			if(t !=null ) {
				map.put("teacher", t);
				return "repair/wxrepair/teacher/index";	
			}else {
				map.put("wxuser",user.toString());
				return "repair/wxrepair/teacher/editmsg";	
			}
		}
		
		
		/**
		 * 售后跳转详情页
		 * @return
		 */
		@RequestMapping("/toMsg")
		public String toMsg(@PathVariable String appid, 
				@RequestParam String code, ModelMap map,@RequestParam("msgId")String msgId,HttpServletRequest request) {
				log.info("-------ip是:{}",getIpAddress(request));
			if(code!="") {
					WxMpService mpService = WxMpConfiguration.getMpServices().get(appid);
			        try {
			            WxMpOAuth2AccessToken accessToken = mpService.oauth2getAccessToken(code);
			            WxMpUser user = mpService.oauth2getUserInfo(accessToken, null);
			            Repairman man=repairmanService.findByOpenid(user.getOpenId());
			            RepairMsg msg=repairMsgService.findOneById(msgId, RepairMsg.class);
		            	map.put("user", user);
			            map.put("msg", msg);
		            	map.put("man", man);
			            if(man!=null&&man.getType().equals(TypeRepair.Manager.getType())) {
			            	//状态为0跳转分配页面
			            	if(msg.getStatus().equals(MsgStatus.Create.getValue())) {
			            		return "repair/wxrepair/rm/distribution";
			            	}else {
			            		//跳转详情页面
			            		return "repair/wxrepair/rm/detalimsg";
			            	}
			            }
			            
			            if(man!=null&&man.getType().equals(TypeRepair.Person.getType())) {
			            	//状态为Send跳转接收页面
			            	if(msg.getStatus().equals(MsgStatus.Send.getValue())) {
			            		return "repair/wxrepair/rm/receipt";
			            		}
			            	//状态为Get跳转完成页面
			            	if(msg.getStatus().equals(MsgStatus.Get.getValue())) {
			            		return "repair/wxrepair/rm/afterRepair";
			            		}
			            	
			            	 if(msg.getStatus().equals(MsgStatus.Done.getValue())) {
				            		return "repair/wxrepair/rm/detalimsg";
			            	 }
			            	 
			            }
			           
			            
			        } catch (WxErrorException e) {
			            e.printStackTrace();
			        } 
			      }
			return "repair/wxrepair/rm/login";
		}
		
		     public static String getIpAddress(HttpServletRequest request) {  
			         String ip = request.getHeader("x-forwarded-for");  
			         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			             ip = request.getHeader("Proxy-Client-IP");  
			         }  
			         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			             ip = request.getHeader("WL-Proxy-Client-IP");  
			         }  
			         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			             ip = request.getHeader("HTTP_CLIENT_IP");  
			         }  
			         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			             ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
			         }  
			         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			             ip = request.getRemoteAddr();  
			         }  
			         return ip;  
			     }  

}
