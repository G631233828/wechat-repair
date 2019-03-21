package zhongchiedu.controller.WebRepair.wx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import zhongchiedu.WxRepair.pojo.RepairClass;
import zhongchiedu.WxRepair.pojo.RepairMsg;
import zhongchiedu.WxRepair.pojo.Repairman;
import zhongchiedu.WxRepair.service.Impl.RepairClassServiceImpl;
import zhongchiedu.WxRepair.service.Impl.RepairMsgServiceImpl;
import zhongchiedu.WxRepair.service.Impl.RepairmanServiceImpl;
import zhongchiedu.WxRepair.service.Impl.TeacherServiceImpl;
import zhongchiedu.WxRepair.util.MsgStatus;
import zhongchiedu.WxRepair.util.TypeRepair;
import zhongchiedu.common.utils.BasicDataResult;
import zhongchiedu.common.utils.Common;
import zhongchiedu.controller.WebRepair.utils.TemplateIdEnum;
import zhongchiedu.controller.WebRepair.utils.TemplateMsgUtil;
import zhongchiedu.general.pojo.MultiMedia;
import zhongchiedu.general.service.MultiMediaService;
import zhongchiedu.general.service.Impl.MultiMediaServiceImpl;
import zhongchiedu.wx.mp.config.WxMpConfiguration;


@Slf4j
@Controller
@RequestMapping("/wx/rm")
public class RepairMsgController {

	
	@Autowired
	private TeacherServiceImpl teacherService;
	
	@Autowired
	private RepairMsgServiceImpl repairMsgService;
	
	@Autowired
	private RepairmanServiceImpl repairmanService;
	
	@Autowired
	private RepairClassServiceImpl repairClassService;
	
	@Autowired
	private MultiMediaServiceImpl  multiMediaService;
	
	@Value("${wxappid}")
	private String appid;
	@Value("${upload-imgpath}")
	private String imgpath;
	@Value("${upload-dir}")
	private String dir;
	
	/**
	 * 教师添加维修信息
	 * @param msg
	 * @param teacherid
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public BasicDataResult editMsg(@ModelAttribute("msg")RepairMsg msg,
			@RequestParam(value="teacherid",defaultValue="")String teacherid,HttpServletRequest request) throws Exception {
		    BasicDataResult result=repairMsgService.edit(msg, teacherid); 
		    if(result.getData()!=null) {
		    	WxMpService wxService=WxMpConfiguration.getMpServices().get(appid);
		    	WxMpTemplateMsgService wtms=wxService.getTemplateMsgService();
		    	TemplateMsgUtil util=new TemplateMsgUtil();
		    	URL requestURL = new URL(request.getRequestURL().toString());
	            String url = wxService
	                    .oauth2buildAuthorizationUrl(
	                        String.format("%s://%s/web/wx/redirect/%s/toMsg?msgId=%s", requestURL.getProtocol(), requestURL.getHost(), appid,msg.getId()),
	                        WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
		    	
		    	WxMpTemplateMessage templateMessage=util.service.getTemplateById(TemplateIdEnum.RepairToast, (RepairMsg)result.getData(),url);
		    	List<Repairman> mans=repairmanService.findAllManByArea(((RepairMsg)result.getData()).getPerson().getArea());
		    	mans.stream().filter(man->Common.isNotEmpty(man.getOpenid())&&man.getType().equals(TypeRepair.Manager.getType())).collect(Collectors.toList()).forEach(man->{
		    			templateMessage.setToUser(man.getOpenid());
		    	        try {
							wtms.sendTemplateMsg(templateMessage);
						} catch (WxErrorException e) {
							e.printStackTrace();
						}
		    	});
		    }
		    return result;	
		}
	
	/**
	 * 教师查看待报修列表
	 * @param teacherid
	 * @param map
	 * @return
	 */
	@RequestMapping("/list/status/{status}")
	public String getOnrepairMsgList(@RequestParam("teacherid")String teacherid
			,ModelMap map,@PathVariable("status")Integer[] status) {
			log.info("进入list方法,教师的id:{}",teacherid);
			List<RepairMsg> list=this.repairMsgService.findByTeacherIdAndStatus(teacherid,status);
			map.put("teacherid", teacherid);
			map.put("list", list);
			if(status.length>1) {
				return "repair/wxrepair/teacher/onRepairList";
			}else {
				return "repair/wxrepair/teacher/LogList";
			}
	}
	
	/**
	 * 教师查看维修详情页面
	 * @param id
	 * @param map
	 * @return
	 */
	@RequestMapping("/toMsg/status/{status}/{id}")
	public String toMsg(@PathVariable("id")String id,@PathVariable("status")Integer status,ModelMap map) {
			RepairMsg msg=this.repairMsgService.GetMsgByid(id);
			map.put("msg",msg);
			if(status == 2) {
				return "repair/wxrepair/teacher/DoneRepair";
			}else {
				return "repair/wxrepair/teacher/onRepairmsg";
			}
	}
	
	

	
	/**
	 * 售后根据维修状态查询列表
	 * @param status
	 * @param id
	 * @return
	 */
	@RequestMapping("/tolist/{status}")
	public String RepairList(@PathVariable("status")Integer status,@RequestParam("id")String id,ModelMap map) {
		Repairman man = this.repairmanService.findOneById(id, Repairman.class);
		map.put("man", man);
		if(man!=null) {
			if(man.getType().equals(TypeRepair.Person.getType())) {
			List<RepairMsg> list=this.repairMsgService.findListByIdAndStatus(man.getId(),status);
			map.put("list", list);
			return "repair/wxrepair/rm/InRepairList";	
			}else if(man.getType().equals(TypeRepair.Manager.getType())||
					man.getType().equals(TypeRepair.Sendto.getType())){
				List<RepairMsg> list=this.repairMsgService.areastatusList(man.getArea(),1,4);
				map.put("list", list);
				return "repair/wxrepair/rm/LogList";
			}else {
				//todo 
			}
		}
		return "";
	}
	
	
	
	/**
	 * 售后查看维修记录
	 * @param status
	 * @param id
	 * @return
	 */
	@RequestMapping("/log/{status}")
	public String RepairListlog(@PathVariable("status")Integer status,@RequestParam("id")String id,ModelMap map) {
		Repairman man = this.repairmanService.findOneById(id, Repairman.class);
		map.put("man", man);
		if(man!=null) {
			if(man.getType().equals(TypeRepair.Person.getType())) {
			List<RepairMsg> list=this.repairMsgService.findListByIdAndStatus(man.getId(),status);
			map.put("list", list);
			}else{
				List<RepairMsg> list=this.repairMsgService.areastatusList(man.getArea(),status);
				map.put("list", list);
			}
		}
		return "repair/wxrepair/rm/LogList2";
	}
	
	
	
	
	/**
	 * 售后分配任务
	 * @param manId  制定维修人员的id
	 * @param msgId	   维修信息的id
	 * @param clsId	 分类id
	 * @param mgId	分配人id
	 * @param sendIds 抄送
	 * @param note 主管备注
	 * @return
	 * @throws MalformedURLException 
	 */
	@RequestMapping("/assign")
	@ResponseBody
	public BasicDataResult assign(@RequestParam(name="manId",defaultValue="")String manId,
			@RequestParam(name="msgId")String msgId,@RequestParam(name="clsId",defaultValue="")String clsId,
			@RequestParam(name="mgId",defaultValue="")String mgId,HttpServletRequest request,
			@RequestParam(name="sendIds",defaultValue="")String sendIds,
			@RequestParam(name="note",defaultValue="")String note) throws MalformedURLException {
			Repairman man=this.repairmanService.findByName(manId);
			RepairMsg msg=this.repairMsgService.findOneById(msgId, RepairMsg.class);
			if(Common.isEmpty(mgId)) {
				return BasicDataResult.build(203, "别搞我", "");
			}
			if(Common.isEmpty(manId)|| man ==null) {
				return BasicDataResult.build(203, "没有该成员", "");
			}
			if(Common.isEmpty(man.getOpenid())) {
				return BasicDataResult.build(203, "联系该人员登录微信平台进行绑定", "");
			}
			
			if(Common.isEmpty(msgId)|| msg ==null) {
				return BasicDataResult.build(203, "没有该维修", "");
			}
			msg.setManagernote(note);
			msg.setRepairman(man);
			RepairClass cls=repairClassService.findOneByName(clsId);
			msg.setRepairclass(cls);
			msg.setStatus(MsgStatus.Send.getValue());
			this.repairMsgService.save(msg);
	    	WxMpService wxService=WxMpConfiguration.getMpServices().get(appid);
	    	WxMpTemplateMsgService wtms=wxService.getTemplateMsgService();
	    	TemplateMsgUtil util=new TemplateMsgUtil();
	    	URL requestURL = new URL(request.getRequestURL().toString());
            String url = wxService
                    .oauth2buildAuthorizationUrl(
                        String.format("%s://%s/web/wx/redirect/%s/toMsg?msgId=%s", requestURL.getProtocol(), requestURL.getHost(), appid,msg.getId()),
                        WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
			WxMpTemplateMessage templateMessage=util.service.getTemplateById(TemplateIdEnum.toManToast,msg,url);
			//通知维修人员
			templateMessage.setToUser(man.getOpenid());
			try {
				wtms.sendTemplateMsg(templateMessage);
			} catch (WxErrorException e) {
				e.printStackTrace();
			}
			//抄送其它人员
			if(Common.isNotEmpty(sendIds)) {
				WxMpTemplateMessage templateMessage2=util.service.getTemplateById(TemplateIdEnum.toManToast,msg,url);
				String[] ids=sendIds.split(",");
				List<Repairman> mans=this.repairmanService.findByIds(ids);
				mans.stream().filter(m1->m1.getType().equals(TypeRepair.Sendto.getType())&&
						Common.isNotEmpty(m1.getOpenid()))
				.collect(Collectors.toList()).forEach(m2->{
					templateMessage2.setToUser(m2.getOpenid());
					try {
						wtms.sendTemplateMsg(templateMessage2);
					} catch (WxErrorException e) {
						e.printStackTrace();
					}
				});
			}
			
	    	return BasicDataResult.build(200,"成功",mgId);
	}
			
			/**
			 * 售后维修接受
			 * @param donetime  预计时间
			 * @param manId     维修人员id号			
			 * @param msgId     消息id
			 * @param request
			 * @return
			 */
			@RequestMapping("/receipt")
			@ResponseBody
			public BasicDataResult receipt(@RequestParam(name="donetime",defaultValue="")String donetime,
					@RequestParam(name="manId",defaultValue="")String manId,
					@RequestParam(name="msgId")String msgId,HttpServletRequest request) {
				Repairman man=this.repairmanService.findOneById(manId, Repairman.class);
				RepairMsg msg=this.repairMsgService.findOneById(msgId, RepairMsg.class);
				if(man == null || msg  == null) {
					return BasicDataResult.build(201, "维修信息或人员为空", null);
				}
				String getContextPath = request.getContextPath();  
				String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+getContextPath+"/";  
				String url=basePath+"wx/rm/toMsg/status/"+msg.getStatus()+"/"+msg.getId();
				log.info(url);
		    	WxMpService wxService=WxMpConfiguration.getMpServices().get(appid);
		    	WxMpTemplateMsgService wtms=wxService.getTemplateMsgService();
		    	TemplateMsgUtil util=new TemplateMsgUtil();
				WxMpTemplateMessage templateMessage=util.service.getTemplateById(TemplateIdEnum.feedback,msg,url);
				templateMessage.setToUser(msg.getPerson().getOpenid());
				try {
					wtms.sendTemplateMsg(templateMessage);
				} catch (WxErrorException e) {
					e.printStackTrace();
				}
		    	msg.setRepairman(man);
				msg.setExpectedtime(donetime);
				msg.setStatus(MsgStatus.Get.getValue());
				this.repairMsgService.save(msg);
				return BasicDataResult.build(200, "已接收消息", "");
			}
			
			/**
			 * 完成维修
			 * @param msgId  维修信息id
			 * @param manId  维修人员id
			 * @param files	  上传结果照片 	
			 * @return
			 */
			@RequestMapping("/finish")
			@ResponseBody
			public BasicDataResult finish(@RequestParam(name="msgId",defaultValue="")String msgId,
					@RequestParam(name="manId",defaultValue="")String manId,
					@RequestParam("files")MultipartFile[] files,HttpServletRequest request) {
				if(Common.isNotEmpty(manId)&&Common.isNotEmpty(msgId)) {
					Repairman man=this.repairmanService.findOneById(manId, Repairman.class);
					RepairMsg msg=this.repairMsgService.findOneById(msgId, RepairMsg.class);
					if(man == null || msg == null ) {
						return BasicDataResult.build(201, "维修信息或人员为空", null);
					}
					if(files.length == 0) {
						return BasicDataResult.build(201, "请上传结果图片", null);
					}
					msg.setStatus(MsgStatus.Done.getValue());
					msg.setDonetime(Common.fromDateH());
					List<MultiMedia> lists=multiMediaService.uploadPictures(files, dir, imgpath, manId);
					msg.setPictureofresults(lists);
					String getContextPath = request.getContextPath();  
					String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+getContextPath+"/";  
					String url=basePath+"wx/rm/toMsg/status/"+msg.getStatus()+"/"+msg.getId();
			    	WxMpService wxService=WxMpConfiguration.getMpServices().get(appid);
			    	WxMpTemplateMsgService wtms=wxService.getTemplateMsgService();
			    	TemplateMsgUtil util=new TemplateMsgUtil();
					WxMpTemplateMessage templateMessage=util.service.getTemplateById(TemplateIdEnum.feedback,msg,url);
					templateMessage.addData(new WxMpTemplateData("first","老师，您的报修已经完成","#000000"));
					templateMessage.addData(new WxMpTemplateData("remark","感谢您的使用","#000000"));
					templateMessage.setToUser(msg.getPerson().getOpenid());
					try {
						wtms.sendTemplateMsg(templateMessage);
					} catch (WxErrorException e) {
						e.printStackTrace();
					}
					this.repairMsgService.save(msg);
					return BasicDataResult.build(200, "提交成功",man);
				}
				return BasicDataResult.build(201, "维修信息或人员为空", null);
			}
				
					
			
}
