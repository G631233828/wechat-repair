package zhongchiedu.controller.WebRepair.wx;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import zhongchiedu.common.utils.BasicDataResult;
import zhongchiedu.common.utils.Common;
import zhongchiedu.controller.WebRepair.utils.FileUtil;
import zhongchiedu.controller.WebRepair.utils.TemplateIdEnum;
import zhongchiedu.controller.WebRepair.utils.TemplateMsgUtil;
import zhongchiedu.framework.pagination.Pagination;
import zhongchiedu.general.pojo.MultiMedia;
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
	
	@Autowired
	private SchoolImpl schoolImpl;
	
	@Autowired
	private HttpServletRequest request;
	
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
	 * @param rcId  报修分类的id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public BasicDataResult addRepairMsg(@ModelAttribute("msg")RepairMsg msg,
			@RequestParam(value="id",defaultValue="")String teacherid,
			@RequestParam("image")String[] image,
			@RequestParam("schoolId")String schoolId,
			@RequestParam(name="classId",defaultValue="")String classId,
			@RequestParam("rcId")String rcId,
			@ModelAttribute("teacher")Teacher t
			) throws Exception {                   
		if(Common.isEmpty(schoolId)||Common.isEmpty(t)) {
			return BasicDataResult.build(201, "缺少必要参数", null);	
		}
		if(Common.isEmpty(rcId)) {
			return BasicDataResult.build(201, "请选择报修分类", null);	
		}
		School school=this.schoolImpl.findOneById(schoolId, School.class);
		//改动    将classId换成rcId
		RepairClass clazz=this.repairClassService.findOneById(rcId, RepairClass.class);
		if(clazz == null) {
			return BasicDataResult.build(201, "没有该维修分类", null);	
		}
		if(Common.isEmpty(school)) {
			return BasicDataResult.build(201, "二维码内容错误", null);	
		}
		List<MultiMedia> lists=null;		
				if(image.length > 0) {
					long startTime = System.currentTimeMillis(); //获取开始时间
					MultipartFile[] file=FileUtil.base64ToMultipart(image);
    				lists=multiMediaService.uploadImages(dir, imgpath, null,file);
//    				long endTime = System.currentTimeMillis(); //获取结束时间
//    				System.out.println("程序运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间
				}
//				else {
//    				return BasicDataResult.build(201, "请上传故障图片", null);	
//    			}
    		BasicDataResult result=repairMsgService.edit(msg, teacherid, lists,clazz,school,t); 
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
		    	List<Repairman> mans=repairmanService.findAllManByArea(((RepairMsg)result.getData()).getSchool().getArea());
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
	 * 教师查看维修详情页面
	 * @param id
	 * @param map
	 * @return
	 */
	@RequestMapping("/toMsg/status/{status}/{id}")
    @AspectLog(operationName = "教师查看维修详情页面",operationType = "GET")
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
	 * @param id  维修人元repairman的id
	 * @return
	 */
	@RequestMapping("/tolist/{status}")
    @AspectLog(operationName = "售后根据维修状态查询列表",operationType = "GET")
	public String RepairList(@PathVariable("status")Integer status,@RequestParam("id")String id,ModelMap map) {
		Repairman man = this.repairmanService.findOneById(id, Repairman.class);
		map.put("man", man);
		if(man!=null) {
			if(status == 2) {
				map.put("title", "维修记录");
				map.put("status", status); 
			}else {
				if(man.getType().equals(TypeRepair.Person.getType()) ) {
					map.put("status", 1); 
				}else if(man.getType().equals(TypeRepair.Manager.getType())||
						man.getType().equals(TypeRepair.Sendto.getType())){
					int c[]= {1,4};
					map.put("status",c);
				}	
				map.put("title", "正在处理");
			}
		}
		return "repair/wxrepair/rm/RepairList";	
	}
	
	
	
	


	
	
	/**
	 * 根据条件获取数据
	 * @param pageNo    
	 * @param pageSize  
	 * @param status   msg的状态
	 * @param manid    售后id
	 * @param area	        售后area	
	 * @param type     售后type
	 * @return
	 */
	@RequestMapping("/getRepairList/pageNo/{pageNo}/pageSize/{pageSize}/status/{status}/{manid}/{area}/{type}")
	@ResponseBody
    @AspectLog(operationName = "售后根据条件获取数据",operationType = "GET")
	public BasicDataResult getRepairList(@PathVariable()Integer pageNo,
			@PathVariable()Integer pageSize,@PathVariable()Integer[] status,
			@PathVariable()String manid,
			@PathVariable(required=false)String area,
			@PathVariable(required=false)String type) {
		// 分页查询数据
		Pagination<RepairMsg> pagination=null;
		try {
			Query query=new Query();
			if(Common.isNotEmpty(type)&&type.equals(TypeRepair.Person.getType())) {
				query=this.repairMsgService.AddIdAndStatus(query, manid, status);
			}
			if(Common.isNotEmpty(type)&&(type.equals(TypeRepair.Manager.getType())
					||type.equals(TypeRepair.Sendto.getType()))) {
				query=this.repairMsgService.AddareaAndstatus(query, area,status);
			}
			pagination = repairMsgService.findPaginationByQuery(query, pageNo, pageSize, RepairMsg.class);
			if (pagination == null)
				pagination = new Pagination<RepairMsg>();
		}catch (Exception e) {
		}
		return BasicDataResult.ok(pagination);	
	}
	
	
	
	
	
	
	/**
	 * 售后分配任务
	 * @param manId  分配人id
	 * @param msgId	   维修信息的id
	 * @param clsId	 分类id
	 * @param mgId	指定人id
	 * @param sendIds 抄送
	 * @param note 主管备注
	 * @return
	 * @throws MalformedURLException 
	 */
	@RequestMapping("/assign")
	@ResponseBody
    @AspectLog(operationName = "售后分配任务",operationType = "GET")
	public BasicDataResult assign(@RequestParam(name="manid1",defaultValue="")String manid1,
			@RequestParam(name="msgId")String msgId,@RequestParam(name="clsId",defaultValue="")String clsId,
			@RequestParam(name="mgId",defaultValue="")String mgId,HttpServletRequest request,
			@RequestParam(name="sendIds",defaultValue="")String sendIds,
			@RequestParam(name="note",defaultValue="")String note) throws MalformedURLException {
			Repairman man=this.repairmanService.findByName(mgId);
			RepairMsg msg=this.repairMsgService.findOneById(msgId, RepairMsg.class);
			if(Common.isEmpty(manid1)) {
				return BasicDataResult.build(203, "别搞我", "");
			}
			if(Common.isEmpty(mgId)|| man ==null) {
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
			if(Common.isEmpty(msg.getRepairclass())) {
				RepairClass cls=repairClassService.findOneByName(clsId);
				if(Common.isNotEmpty(cls)) {
					msg.setRepairclass(cls);
				}else {
					return BasicDataResult.build(203, "请选择维修分类", "");
				}
			}
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
				templateMessage2.addData(new WxMpTemplateData("first","您有新的抄送通知,请查看!","#000000"));
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
		    @AspectLog(operationName = "售后维修接受",operationType = "GET")
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
			 * @throws Exception 
			 */
			@RequestMapping("/finish")
			@ResponseBody
			public BasicDataResult finish(@RequestParam(name="msgId",defaultValue="")String msgId,
					@RequestParam(name="manId",defaultValue="")String manId,
					@RequestParam("image")String[] image,HttpServletRequest request) throws Exception {
				if(Common.isNotEmpty(manId)&&Common.isNotEmpty(msgId)) {
					Repairman man=this.repairmanService.findOneById(manId, Repairman.class);
					RepairMsg msg=this.repairMsgService.findOneById(msgId, RepairMsg.class);
					if(man == null || msg == null ) {
						return BasicDataResult.build(201, "维修信息或人员为空", null);
					}
					msg.setStatus(MsgStatus.Done.getValue());
					msg.setDonetime(Common.fromDateH());
					List<MultiMedia> lists=null;		
					if(image.length > 0) {
						MultipartFile[] file=FileUtil.base64ToMultipart(image);
	    				lists=multiMediaService.uploadImages(dir, imgpath, null,file);
					}else {
	    				return BasicDataResult.build(201, "请上传故障图片", null);	
	    			}
    				msg.setPictureofresults(lists);
					String getContextPath = request.getContextPath();  
					String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+getContextPath+"/";  
					String url=basePath+"wx/rm/toMsg/status/"+msg.getStatus()+"/"+msg.getId();
			    	WxMpService wxService=WxMpConfiguration.getMpServices().get(appid);
			    	WxMpTemplateMsgService wtms=wxService.getTemplateMsgService();
			    	TemplateMsgUtil util=new TemplateMsgUtil();
	
			    	
// 通知教师			    	
//					WxMpTemplateMessage templateMessage=util.service.getTemplateById(TemplateIdEnum.feedback,msg,url);
//					templateMessage.addData(new WxMpTemplateData("first","老师，您的报修已经完成","	#000000"));
//					templateMessage.addData(new WxMpTemplateData("remark","感谢您的使用","#000000"));
//					templateMessage.setToUser(msg.getPerson().getOpenid());
//					try {
//						wtms.sendTemplateMsg(templateMessage);
//					} catch (WxErrorException e) {
//						e.printStackTrace();
//					}
					
			    	List<Repairman> mans=this.repairmanService.findAllManByArea(msg.getRepairman().getArea());		
					WxMpTemplateMessage templateMessage2=util.service.getTemplateById(TemplateIdEnum.feedback,msg,url);
					templateMessage2.addData(new WxMpTemplateData("first","您有新的抄送通知,请查看!","#000000"));
			    	String url2=basePath+"wx/rp/InRepair/"+msg.getId()+"?manid=%s";
					mans.stream().filter(m1->(m1.getType().equals(TypeRepair.Manager.getType())||m1.getType().equals(TypeRepair.Sendto.getType()))
					&&Common.isNotEmpty(m1.getOpenid())).collect(Collectors.toList()).forEach(
								m2->{
									templateMessage2.setUrl(String.format(url2, m2.getId()));
									templateMessage2.addData(new WxMpTemplateData("remark","维修已处理完毕","#000000"));
									templateMessage2.setToUser(m2.getOpenid());
									try {
										wtms.sendTemplateMsg(templateMessage2);
									} catch (WxErrorException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							);
					this.repairMsgService.save(msg);
					return BasicDataResult.build(200, "提交成功",man);
				}
				return BasicDataResult.build(201, "维修信息或人员为空", null);
			}
			
		/**
		 * 教师获取维修记录
		 * @param pageNo
		 * @param pageSize
		 * @return
		 */
		@RequestMapping("/getLog")
		@ResponseBody
	    @AspectLog(operationName = "教师获取维修记录",operationType = "GET")
		public BasicDataResult GetLog(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
				@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
			Pagination<RepairMsg> pagination=null;
			try {
				pagination = repairMsgService.findPaginationByQuery(new Query(), pageNo, pageSize, RepairMsg.class);
				if (pagination == null)
					pagination = new Pagination<RepairMsg>();
			} catch (Exception e) {
				log.info("查询所有维修失败——————————》" + e.toString());
				e.printStackTrace();
			}
			return BasicDataResult.ok(pagination);
		}
			
		
		
		
		
		/**
		 * 获取教师查看报修列表
		 * @param teacherid
		 * @param map
		 * @return
		 */
		@RequestMapping("/getLog/pageNo/{pageNo}/pageSize/{pageSize}/status/{status}/{teacherid}")
		@ResponseBody
	    @AspectLog(operationName = "教师查看报修列表",operationType = "GET")
		public BasicDataResult getLog(@PathVariable()Integer pageNo,
				@PathVariable()Integer pageSize,@PathVariable()Integer[] status,
				@PathVariable()String teacherid) {
			// 分页查询数据
			Pagination<RepairMsg> pagination=null;
			try {
				Query query=new Query();
                query=this.repairMsgService.AddByTeacherIdAndStatus(query, teacherid, status);
				pagination = repairMsgService.findPaginationByQuery(query, pageNo, pageSize, RepairMsg.class);
				if (pagination == null)
					pagination = new Pagination<RepairMsg>();
			}catch (Exception e) {
			}
			return BasicDataResult.ok(pagination);	
		}
		
		/**
		 * 教师-》列表
		 * @param teacherid
		 * @param map
		 * @return
		 */
		@RequestMapping("/list/status/{status}")
	    @AspectLog(operationName = "教师-》列表",operationType = "GET")
		public String getOnrepairMsgList(@RequestParam("teacherid")String teacherid
				,ModelMap map,@PathVariable("status")Integer[] status) {
				map.put("teacherid", teacherid);
				if(status.length>1) {
					map.put("title", "正在维修");
					List<Integer> s=new ArrayList<>();
					for (Integer integer : status) {
						s.add(integer);
					}
					map.put("status", s);
				}else {
					map.put("title", "维修记录");
					map.put("status",status[0]);
				}
				return "repair/wxrepair/teacher/teacherList";
		}
			
}



/**
 * 售后根据维修状态查询列表
 * @param status   
 * @param id  维修人元repairman的id
 * @return
 */
//@RequestMapping("/tolist/{status}")
//public String RepairList(@PathVariable("status")Integer status,@RequestParam("id")String id,ModelMap map) {
//	Repairman man = this.repairmanService.findOneById(id, Repairman.class);
//	map.put("man", man);
//	if(man!=null) {
//		if(man.getType().equals(TypeRepair.Person.getType())) {
//		List<RepairMsg> list=this.repairMsgService.findListByIdAndStatus(man.getId(),status);
//		map.put("list", list);
//		return "repair/wxrepair/rm/InRepairList";	
//		}else if(man.getType().equals(TypeRepair.Manager.getType())||
//				man.getType().equals(TypeRepair.Sendto.getType())){
//			List<RepairMsg> list=this.repairMsgService.areastatusList(man.getArea(),1,4);
//			map.put("list", list);
//			return "repair/wxrepair/rm/LogList";
//		}else {
//			//todo 
//		}
//	}
//	return "";
//}


/**
 * 售后查看维修记录
 * @param status
 * @param id
 * @return
 */
//@RequestMapping("/log/{status}")
//public String RepairListlog(@PathVariable("status")Integer status,@RequestParam("id")String id,ModelMap map) {
//	Repairman man = this.repairmanService.findOneById(id, Repairman.class);
//	map.put("man", man);
//	if(man!=null) {
//		if(man.getType().equals(TypeRepair.Person.getType())) {
//		List<RepairMsg> list=this.repairMsgService.findListByIdAndStatus(man.getId(),status);
//		map.put("list", list);
//		}else{
//			List<RepairMsg> list=this.repairMsgService.areastatusList(man.getArea(),status);
//			map.put("list", list);
//		}
//	}
//	return "repair/wxrepair/rm/LogList2";
//}

