package zhongchiedu.controller.WebRepair;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import zhongchiedu.WxRepair.pojo.Repairman;
import zhongchiedu.WxRepair.service.Impl.RepairmanServiceImpl;
import zhongchiedu.common.utils.BasicDataResult;
import zhongchiedu.framework.pagination.Pagination;
import zhongchiedu.general.pojo.Role;
import zhongchiedu.general.service.Impl.RoleServiceImpl;
import zhongchiedu.log.annotation.SystemControllerLog;

@Slf4j
@Controller
public class RepairManController {

	
	@Autowired
	private RepairmanServiceImpl repairmanService;

	@Autowired
	private RoleServiceImpl roleService;
	
	

	@GetMapping("mans")
	@RequiresPermissions(value = "man:list")
	@SystemControllerLog(description = "查询所有人员")
	public String list(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, Model model,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
		// 分页查询数据
		Pagination<Repairman> pagination;
		try {
			pagination = this.repairmanService.findPaginationByQuery(new Query(), pageNo, pageSize, Repairman.class);
			if (pagination == null)
				pagination = new Pagination<Repairman>();

			model.addAttribute("pageList", pagination);
		} catch (Exception e) {
			log.info("查询所有维修人员信息失败——————————》" + e.toString());
			e.printStackTrace();
		}
		return "repair/man/list";
	}
	

	/**
	 * 跳转到添加页面
	 */
	@GetMapping("/man")
	@RequiresPermissions(value = "man:add")
	public String addUserPage(Model model) {

		List<Role> list = this.roleService.findAllRoleByisDisable();

		model.addAttribute("roleList", list);
		return "repair/man/add";
	}

	@PostMapping("/man")
	@RequiresPermissions(value = "man:add")
	@SystemControllerLog(description = "添加维修人员")
	public String addMan(HttpServletRequest request, @ModelAttribute("man") Repairman man,
			@RequestParam(value = "roleId", defaultValue = "") String roleId) {
		this.repairmanService.saveOrUpdateMan(man, roleId);
		return "redirect:mans";
	}
	
	
	@PutMapping("/man")
	@RequiresPermissions(value = "man:edit")
	@SystemControllerLog(description = "修改维修人员")
	public String editMan(HttpServletRequest request, @ModelAttribute("man") Repairman man,
			@RequestParam(value = "roleId", defaultValue = "") String roleId) {
		this.repairmanService.saveOrUpdateMan(man, roleId);
		return "redirect:mans";
	}
	
	/**
	 * 跳转到编辑界面
	 * 
	 * @return
	 */
	@GetMapping("/man{id}")
	@RequiresPermissions(value = "man:edit")
	@SystemControllerLog(description = "编辑维修人员信息")
	public String toeditPage(@PathVariable String id, Model model) {
		List<Role> list = this.roleService.findAllRoleByisDisable();
		model.addAttribute("roleList", list);
		Repairman man= this.repairmanService.findOneById(id, Repairman.class);
		model.addAttribute("man", man);
		return "repair/man/add";
	}
	
	@DeleteMapping("/man/{id}")
	@RequiresPermissions(value = "man:delete")
	@SystemControllerLog(description = "删除维修人员")
	public String delete(@PathVariable String id){
		String[] strids = id.split(",");
		for (String delids : strids) {
			log.info("删除维修人员---》" + delids);
			Repairman rm = this.repairmanService.findOneById(delids,Repairman.class);
			this.repairmanService.remove(rm);// 删除某个id
		}
		return "redirect:/mans";
	}
	
	@RequestMapping(value = "/man/ajaxgetRepletes", method = RequestMethod.POST)
	@ResponseBody
	public BasicDataResult ajaxgetRepletes(@RequestParam(value = "accountName", defaultValue = "") String accountName) {
			log.info(accountName);
		return	this.repairmanService.ajaxgetRepletes(accountName);
	}
	
}
