package zhongchiedu.controller.WebRepair;


import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import zhongchiedu.WxRepair.pojo.RepairClass;
import zhongchiedu.WxRepair.service.Impl.RepairClassServiceImpl;
import zhongchiedu.framework.pagination.Pagination;
import zhongchiedu.log.annotation.SystemControllerLog;
@Slf4j
@Controller
public class RepairClassController {

	
	
	
	@Autowired
	private RepairClassServiceImpl repairClassServiceImpl;
	
	
	@GetMapping("/classes")
//	@RequiresPermissions(value = "repairclass:list")
	@SystemControllerLog(description = "查询所有分类")
	public String list(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, Model model,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
		// 分页查询数据
		Pagination<RepairClass> pagination;
		try {
			pagination = repairClassServiceImpl.findPaginationByQuery(new Query(), pageNo, pageSize, RepairClass.class);
			if (pagination == null)
				pagination = new Pagination<RepairClass>();

			model.addAttribute("pageList", pagination);
		} catch (Exception e) {
			log.info("查询所有维修分类信息失败——————————》" + e.toString());
			e.printStackTrace();
		}

		return "repair/web/list";
	}
	
	/**
	 * 跳转到添加页面
	 */
	@GetMapping("/repairclass")
	@RequiresPermissions(value = "repairclass:add")
	public String addUserPage(Model model) {
		Query query=new Query();
		query.addCriteria(Criteria.where("isparent").is(true));
		List<RepairClass> rcs=this.repairClassServiceImpl.find(query, RepairClass.class);
		model.addAttribute("rclist", rcs);
		return "repair/web/add";
	}
	
	@PostMapping("/repairclass")
	@RequiresPermissions(value="repairclass:add")
	@SystemControllerLog(description = "添加维修类别")
	public String addOne(@ModelAttribute("repairclass")RepairClass repairclass) {
		log.info(repairclass.toString());
		this.repairClassServiceImpl.addOrUpdate(repairclass);
		return "redirect:classes";
		
	}
	@PutMapping("/repairclass")
	@RequiresPermissions(value="repairclass:edit")
	@SystemControllerLog(description = "修改维修类别")
	public String editOne(@ModelAttribute("repairclass")RepairClass repairclass) {
		this.repairClassServiceImpl.addOrUpdate(repairclass);
		return "redirect:classes";
	}
	
	@GetMapping("/repairclass{id}")
	public String toEdit(@PathVariable("id")String id,Model model) {
		RepairClass rc=this.repairClassServiceImpl.findOneById(id, RepairClass.class);
		Query query=new Query();
		query.addCriteria(Criteria.where("isparent").is(true));
		List<RepairClass> rcs=this.repairClassServiceImpl.find(query, RepairClass.class);
		if(rcs!=null&&rc!=null) {
		rcs=rcs.stream().filter(r->!id.equals(r.getId())).collect(Collectors.toList());
		}
		model.addAttribute("rclist", rcs);
		model.addAttribute("repairclass", rc);
		return "repair/web/add";
	}
	
	
	@DeleteMapping("/repairclass/{id}")
	@RequiresPermissions(value = "repairclass:delete")
	@SystemControllerLog(description = "删除维修分类")
	public String delete(@PathVariable String id){
		String[] strids = id.split(",");
		for (String delids : strids) {
			log.info("删除维修分类---》" + delids);
			RepairClass rc = this.repairClassServiceImpl.findOneById(delids,RepairClass.class);
			this.repairClassServiceImpl.remove(rc);// 删除某个id
		}
		return "redirect:/classes";
	}


}
