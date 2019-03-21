package zhongchiedu.controller.WebRepair.wx;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

@Controller
@RequestMapping("/wx/rp")
public class WxPortalController {

	@Autowired
	private RepairmanServiceImpl repairService;

	// @Autowired
	// private TeacherServiceImpl teacherService;

	@Autowired
	private RepairMsgServiceImpl repairmsgService;

	@Autowired
	private RepairClassServiceImpl repairClassService;

	@RequestMapping("/tologin")
	@ResponseBody
	public BasicDataResult Login(Repairman man, HttpSession session) {
		BasicDataResult result = this.repairService.checklogin(man);
		return result;
	}

	@RequestMapping("/toedit")
	public String toEdit(@RequestParam(value = "code", defaultValue = "") String code) {
		return "repair/wxrepair/teacher/editmsg";
	}

	@RequestMapping("/toIndex/{id}")
	public String toIndex(@PathVariable("id") String id, ModelMap map) {
		Repairman t = this.repairService.findOneById(id, Repairman.class);
		if (t != null) {
			map.put("man", t);
		}
		return "repair/wxrepair/rm/index";
	}

	/**
	 * 售后查看list
	 * 
	 * @param id
	 * @param map
	 * @return
	 */
	@RequestMapping("/list")
	public String getOnrepairMsgList(@RequestParam("id") String id, ModelMap map) {
		Repairman man = this.repairService.findOneById(id, Repairman.class);
		List<RepairMsg> list = null;
		if (man != null) {
			if (man.getType().equals(TypeRepair.Manager.getType())) {
				list = this.repairmsgService.areastatusList(man.getArea(),0);
			} else {
				list = this.repairmsgService.findListByIdAndStatus(id, MsgStatus.Send.getValue());
			}
			map.put("man", man);
		}
		map.put("list", list);
		return "repair/wxrepair/rm/onRepairList";
	}

	/**
	 * 售后跳转界面
	 * 
	 * @param id
	 * @param map
	 * @param manid
	 * @return
	 */
	@RequestMapping("/toMsg/{id}")
	public String toMsg(@PathVariable("id") String id, ModelMap map, @RequestParam("manid") String manid) {
		RepairMsg msg = this.repairmsgService.GetMsgByid(id);
		map.put("msg", msg);
		Repairman man = this.repairService.findOneById(manid, Repairman.class);
		map.put("man", man);
		if (man.getType().equals(TypeRepair.Manager.getType())) {
			return "repair/wxrepair/rm/distribution";
		} else if (man.getType().equals(TypeRepair.Person.getType())) {
			return "repair/wxrepair/rm/receipt";
		}
		return "";
	}
	
	/**
	 * 售后查看正在维修
	 * 
	 * @param id
	 * @param map
	 * @param manid
	 * @return
	 */
	@RequestMapping("/InRepair/{id}")
	public String InRepair(@PathVariable("id") String id, ModelMap map, @RequestParam("manid") String manid) {
		RepairMsg msg = this.repairmsgService.GetMsgByid(id);
		map.put("msg", msg);
		Repairman man = this.repairService.findOneById(manid, Repairman.class);
		map.put("man", man);
		if (man.getType().equals(TypeRepair.Manager.getType())||man.getType().equals(TypeRepair.Sendto.getType())) {
			return "repair/wxrepair/rm/detalimsg";
		} else if (man.getType().equals(TypeRepair.Person.getType())) {
			return "repair/wxrepair/rm/afterRepair";
		}
		return "repair/wxrepair/rm/afterRepair";
	}
	
	
	
	

	/**
	 * 根据area获取维修人员
	 * 
	 * @param area
	 * @return
	 */
	@RequestMapping("/getMans")
	@ResponseBody
	public BasicDataResult getMans(@RequestParam("area") String area) {
		List<Repairman> mans;
		try {
			mans = this.repairService.findAllManByArea(area);
		} catch (Exception e) {
			e.printStackTrace();
			return BasicDataResult.build(201, "查询出错", null);
		}
		List<Repairman> names = mans.stream().filter(man -> man.getType().equals(TypeRepair.Person.getType()))
				.collect(Collectors.toList());
		return BasicDataResult.build(200, "", names);
	}

	@RequestMapping("/getRpcs")
	@ResponseBody
	public BasicDataResult getRpcs() {
		List<RepairClass> rpcs = this.repairClassService.findAll();
		return BasicDataResult.build(200, "", rpcs);
	}

	@RequestMapping("/getSendMans")
	@ResponseBody
	public BasicDataResult getSendMans(@RequestParam("area") String area) {
		List<Repairman> mans;
		try {
			mans = this.repairService.findAllManByArea(area);
		} catch (Exception e) {
			e.printStackTrace();
			return BasicDataResult.build(201, "查询出错", null);
		}
		List<Repairman> names = mans.stream().filter(man -> man.getType().equals(TypeRepair.Sendto.getType()))
				.collect(Collectors.toList());
		return BasicDataResult.build(200, "", names);
	}
}
