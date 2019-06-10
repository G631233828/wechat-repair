package zhongchiedu.controller.WebRepair.wx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import zhongchiedu.WxRepair.pojo.RepairClass;
import zhongchiedu.WxRepair.pojo.RepairMsg;
import zhongchiedu.WxRepair.pojo.Repairman;
import zhongchiedu.WxRepair.pojo.School;
import zhongchiedu.WxRepair.pojo.Teacher;
import zhongchiedu.WxRepair.service.Impl.RepairClassServiceImpl;
import zhongchiedu.WxRepair.service.Impl.RepairMsgServiceImpl;
import zhongchiedu.WxRepair.service.Impl.RepairmanServiceImpl;
import zhongchiedu.WxRepair.service.Impl.SchoolImpl;
import zhongchiedu.WxRepair.service.Impl.TeacherServiceImpl;
import zhongchiedu.WxRepair.util.MsgStatus;
import zhongchiedu.WxRepair.util.TypeRepair;
import zhongchiedu.aspect.AspectLog;
import zhongchiedu.common.utils.Common;
import zhongchiedu.controller.WebRepair.utils.ChoseEntity;
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
	
	@Autowired
	private RepairClassServiceImpl classImpl;
	
	@Autowired
	private SchoolImpl schoolImpl;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
		/**
		 * 微信菜单维修入口
		 * @param appid
		 * @param code
		 * @param map
		 * @return
		 */
		@RequestMapping("/index")
        @AspectLog(operationName = "微信菜单维修人员入口",operationType = "P")
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
		@RequestMapping("/repairIndex/{i}")
        @AspectLog(operationName = "模拟二维码跳转入口",operationType = "")
		public String toIndex(@PathVariable String appid,@PathVariable Integer i, 
				@RequestParam String code, ModelMap map,
				@RequestParam(name="schoolId",defaultValue="")String schoolId,
				@RequestParam(name="classId",defaultValue="")String classId) throws WxErrorException {
	        WxMpService mpService = WxMpConfiguration.getMpServices().get(appid);
	     	WxMpOAuth2AccessToken accessToken = mpService.oauth2getAccessToken(code);
			WxMpUser user = mpService.oauth2getUserInfo(accessToken, null);
	        Teacher t=this.teacherService.findByOpenId(user.getOpenId()); 
	        if(t == null) {
	        	t=new Teacher();
	        	t.setOpenid(user.getOpenId());
	        	this.teacherService.insert(t);
	        }
	        if(Common.isNotEmpty(t.getSchoolid())){
				School school=this.schoolImpl.findOneById(t.getSchoolid(), School.class);
				map.put("school", school);
	        }
	        if(Common.isNotEmpty(schoolId)&&t.getSchoolid()!=(schoolId)) {
					School school=this.schoolImpl.findOneById(schoolId, School.class);
					map.put("school", school);
			 }


			if(Common.isNotEmpty(classId)) {
				RepairClass cls=this.classImpl.findOneById(classId, RepairClass.class);
				map.put("cls", cls);
			}
			map.put("teacher",t);
			map.put("hidden", true);
			if(i == 1) {
				List<ChoseEntity> lce=GetChoseEntityList();
		 		map.put("celist", lce);
				return "repair/wxrepair/teacher/repair";	
			}
			return "repair/wxrepair/teacher/index";
		}
		
		
		/**
		 * 售后跳转详情页
		 * @return
		 */
		@RequestMapping("/toMsg")
        @AspectLog(operationName = "售后跳转维修详情页",operationType = "")
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
		     
		   
		   /**
		    * 二维码转跳地址  
		    * @param schoolId
		    * @param classId
		    * @param appid
		    * @throws MalformedURLException
		    */
		  @RequestMapping("/jump")
		  @ResponseBody
	      @AspectLog(operationName = "二维码转跳地址  jump",operationType = "")
		  public void jump(@RequestParam(name="schoolId",defaultValue="")String schoolId,
					@RequestParam(name="classId",defaultValue="")String classId,
					@PathVariable("appid")String appid) throws MalformedURLException {
		        URL requestURL = new URL(request.getRequestURL().toString());
		        String redirectURL=String.format("%s://%s/web/wx/redirect/%s/repairIndex/1?", requestURL.getProtocol(), requestURL.getHost(),appid);
		        String rul=redirectURL+"schoolId="+schoolId+"&classId="+classId;
		        String redirecturl= WxMpConfiguration.getMpServices().get(appid)
		                .oauth2buildAuthorizationUrl(rul,WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
		        try {
					response.sendRedirect(redirecturl);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
		     	
		  
		  
		  	/**
		  	 * 获取报修内容选项
		  	 * @return
		  	 */
			 private List<ChoseEntity> GetChoseEntityList() {
				 List<RepairClass> lrp=this.classImpl.find(new Query(), RepairClass.class);
				 List<ChoseEntity> lce=new ArrayList<>();
				 if(lrp.size()>0) {
					Map<Boolean, List<RepairClass>> map=lrp.stream().collect(Collectors.groupingBy
							 (RepairClass::isIsparent));
					List<RepairClass> falselist=map.get(false);
					List<RepairClass> truelist=map.get(true);
					if(truelist!=null) {
					truelist.stream().forEach( (RepairClass tr)->{
						ChoseEntity ce=new ChoseEntity();
						ce.setLabel(tr.getName());
						ce.setValue(tr.getId());
						if(falselist!=null) {
							 List<ChoseEntity> celist=falselist.stream().filter(fr->fr.getParentid().equals(tr.getId()))
										.map(rp->{
											ChoseEntity ce1=new ChoseEntity();
											ce1.setLabel(rp.getName());
											ce1.setValue(rp.getId());
											return ce1;
										}).collect(Collectors.toList());
							 ce.setChildren(celist);
						}
						lce.add(ce);
					});
					}
				 }
				 return lce;
			 }
		     
		     
}
