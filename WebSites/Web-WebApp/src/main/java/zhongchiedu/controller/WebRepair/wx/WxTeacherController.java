package zhongchiedu.controller.WebRepair.wx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import zhongchiedu.WxRepair.pojo.Repairman;
import zhongchiedu.WxRepair.pojo.Teacher;
import zhongchiedu.WxRepair.service.Impl.TeacherServiceImpl;
import zhongchiedu.common.utils.BasicDataResult;
import zhongchiedu.wechat.oauto2.NSNUserInfo;
import zhongchiedu.wx.mp.config.WxMpConfiguration;


@Slf4j
@Controller
@RequestMapping("/wx/t")
public class WxTeacherController {

	
	@Autowired
	private TeacherServiceImpl teacherService;
	
	@Value("${wxappid}")
	private String appid;
	

	
	
		 @RequestMapping(method=RequestMethod.POST,value="/add")	
		 @ResponseBody
		 public BasicDataResult  add(@ModelAttribute("teacher")Teacher teacher,
				 @RequestParam(value="wxuser",defaultValue="")String wxuser,
				 ModelMap map) {
			 BasicDataResult result=BasicDataResult.build(304,"微信code为空", null);
			 if(!"".equals(wxuser) && wxuser!=null ) {
				 teacher.setArea(teacher.getArea() == "浦东新区"?"pudong":"fx");
				 wxuser=wxuser.replace("openId", "openid");
			    WxMpUser user=WxMpUser.fromJson(wxuser);
				teacher.setOpenid(user.getOpenId());
				result=this.teacherService.addorUpdate(teacher);
			}
			 return result;
		}
		 
		 
		 
		 	@RequestMapping("/torepair/{id}")
		 	public String toRepair(@PathVariable("id")String id,ModelMap map) {	
		 		log.info("id是{}",id);
		 		Teacher t=this.teacherService.findOneById(id, Teacher.class);
		 		map.put("teacher", t);
		 		return "repair/wxrepair/teacher/repair";
		 	}
		 

			 @RequestMapping("/toIndex/{id}")
			 public String toIndex(@PathVariable("id")String id,ModelMap map) {
					Teacher t=this.teacherService.findOneById(id, Teacher.class);
					if(t!=null) {
						map.put("teacher", t);
					}
					return "repair/wxrepair/teacher/index";
	
			}
			
			 @RequestMapping("/Msg/{id}")
			 public String toMsg(@PathVariable("id")String id,ModelMap map) {
					Teacher t=this.teacherService.findOneById(id, Teacher.class);
					if(t!=null) {
						map.put("teacher", t);
					}
					return "repair/wxrepair/teacher/msg";
	
			}
		 
}
