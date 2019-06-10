package zhongchiedu.controller.WebRepair.wx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import zhongchiedu.WxRepair.pojo.RepairClass;
import zhongchiedu.WxRepair.pojo.School;
import zhongchiedu.WxRepair.pojo.Teacher;
import zhongchiedu.WxRepair.service.Impl.RepairClassServiceImpl;
import zhongchiedu.WxRepair.service.Impl.SchoolImpl;
import zhongchiedu.WxRepair.service.Impl.TeacherServiceImpl;
import zhongchiedu.aspect.AspectLog;
import zhongchiedu.common.utils.BasicDataResult;
import zhongchiedu.common.utils.Common;
import zhongchiedu.controller.WebRepair.utils.ChoseEntity;


@Slf4j
@Controller
@RequestMapping("/wx/t")
public class WxTeacherController {

	
	@Autowired
	private TeacherServiceImpl teacherService;
	
	
	@Autowired
	private RepairClassServiceImpl repairClassS;
	
	@Autowired
	private SchoolImpl schoolImpl;
	
	@Value("${wxappid}")
	private String appid;
	

	
//		 @RequestMapping(method=RequestMethod.POST,value="/add")	
//		 @ResponseBody
//	     @AspectLog(operationName = "教师添加个人信息",operationType = "GET")
//		 public BasicDataResult  add(@ModelAttribute("teacher")Teacher teacher,
//				 @RequestParam(value="wxuser",defaultValue="")String wxuser,
//				 ModelMap map) {
//			 BasicDataResult result=BasicDataResult.build(304,"微信code为空", null);
//			 if(!"".equals(wxuser) && wxuser!=null ) {
//				 teacher.setArea(teacher.getArea() == "浦东新区"?"pudong":"fx");
//				 wxuser=wxuser.replace("openId", "openid");
//			    WxMpUser user=WxMpUser.fromJson(wxuser);
//				teacher.setOpenid(user.getOpenId());
//				result=this.teacherService.addorUpdate(teacher);
//			}
//			 return result;
//		}
//		 
		 
		 
		 	@RequestMapping("/torepair/{id}/{schoolid}")
		    @AspectLog(operationName = "教师->维修页面",operationType = "GET")
		 	public String toRepair(@PathVariable("id")String id,ModelMap map,
		 			@PathVariable("schoolid")String schoolid) {	
		 		Teacher t=this.teacherService.findOneById(id, Teacher.class);
		 		School school=this.schoolImpl.findOneById(schoolid, School.class);
				List<ChoseEntity> lce=GetChoseEntityList();
		 		map.put("celist", lce);
		 		//todo
		 		map.put("teacher", t);
		 		map.put("school", school);
		 		return "repair/wxrepair/teacher/repair";
		 	}
		 

			 @RequestMapping("/toIndex/{id}")
			 @AspectLog(operationName = "教师->主页面",operationType = "GET")
			 public String toIndex(@PathVariable("id")String id,ModelMap map) {
					Teacher t=this.teacherService.findOneById(id, Teacher.class);
					if(t!=null) {
						map.put("teacher", t);
					}
					return "repair/wxrepair/teacher/index";
	
			}
			
			 @RequestMapping("/Msg/{id}/{schoolid}")
			 @AspectLog(operationName = "教师->个人信息修改页面",operationType = "GET")
			 public String toMsg(@PathVariable("id")String id,ModelMap map,
					 @PathVariable(name="schoolid",required=false)String schoolid) {
					Teacher t=this.teacherService.findOneById(id, Teacher.class);
					if(Common.isNotEmpty(schoolid)) {
						School school=this.schoolImpl.findOneById(schoolid, School.class);
						map.put("school", school);
					}
					if(t!=null) {
						map.put("teacher", t);
					}
					return "repair/wxrepair/teacher/editmsg";
			}
	
			 
			 
			 /**
			  * 教师添加修改联系信息
			  * @param teacher   
			  * @param schoolid
			  * @param addr
			  * @param schoolname
			  * @param area
			  * @return
			  */
			 @ResponseBody
			 @RequestMapping("/editMsg")
			 @AspectLog(operationName = "教师添加修改联系信息",operationType = "GET")
			 public BasicDataResult editMsg(@ModelAttribute("teacher")Teacher teacher,
					 @RequestParam(name="schoolId",defaultValue="")String schoolId,
					 @RequestParam(name="addr",defaultValue="")String addr,
					 @RequestParam(name="schoolname",defaultValue="")String schoolname,
					 @RequestParam(name="area",defaultValue="")String area) {   
				 	 Teacher t=this.teacherService.findOneById(teacher.getId(),Teacher.class);
				 	 if(t!=null) {
				 		 t.setName(teacher.getName());
				 		 t.setTel(teacher.getTel());
				 		 t.setSchoolid(schoolId);
				 	 }else {
				 		 return BasicDataResult.build(201, "无法识别信息", null);
				 	 }
				 	 Map<String, String> map=new HashMap<>();
				 	 if(Common.isEmpty(schoolId)&&Common.isNotEmpty(schoolname)&&Common.isNotEmpty(addr)) {
				 		 String sname=schoolname.substring(0, schoolname.length()-1);
				 		 Query query=new Query();
				 		 Pattern pattern=Pattern.compile("^.*"+sname+".*$", Pattern.CASE_INSENSITIVE);
				 		 query.addCriteria(Criteria.where("name").regex(pattern));
				 		 School school=this.schoolImpl.findOneByQuery(query, School.class);
				 		 if(Common.isEmpty(school)) {
				 			 school=new School();	
				 			 school.setName(schoolname);
				 			 school.setAddr(addr);
				 			 school.setArea(area);
				 		     this.schoolImpl.insert(school);
				 		 }
				 		 t.setSchoolid(school.getId());
				 		 map.put("schoolid", school.getId());
				 	 }else {
				 		 map.put("schoolid", schoolId);
				 	 }
				 	 this.teacherService.save(t);
				 	 map.put("teacherid", t.getId());
				 return BasicDataResult.ok(map);
			 }
			 
			 private List<ChoseEntity> GetChoseEntityList() {
				 List<RepairClass> lrp=this.repairClassS.find(new Query(), RepairClass.class);
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
