package zhongchiedu.controller.WebRepair;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import lombok.extern.slf4j.Slf4j;
import zhongchiedu.WxRepair.pojo.RepairClass;
import zhongchiedu.WxRepair.pojo.RepairMsg;
import zhongchiedu.WxRepair.service.Impl.RepairClassServiceImpl;
import zhongchiedu.WxRepair.service.Impl.RepairMsgServiceImpl;
import zhongchiedu.common.Excel.ExcelUtil;
import zhongchiedu.common.utils.Common;
import zhongchiedu.controller.WebRepair.utils.MsgInfo;
import zhongchiedu.framework.pagination.Pagination;
import zhongchiedu.log.annotation.SystemControllerLog;

@Slf4j
@Controller
public class MsgController {
	
	@Autowired
	private RepairMsgServiceImpl repairMsgService;
	@Autowired
	private RepairClassServiceImpl repairClassServiceImpl;
	
	@GetMapping("/msgs")
	@SystemControllerLog(description = "查询所有")
	public String list(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, Model model,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
		// 分页查询数据
		Pagination<RepairMsg> pagination;
		try {
			pagination = repairMsgService.findPaginationByQuery(new Query(), pageNo, pageSize, RepairMsg.class);
			if (pagination == null)
				pagination = new Pagination<RepairMsg>();
			Query query=new Query();
			List<RepairClass> rcs=this.repairClassServiceImpl.find(query, RepairClass.class);
			model.addAttribute("rclist", rcs);
			model.addAttribute("pageList", pagination);
		} catch (Exception e) {
			log.info("查询所有维修失败——————————》" + e.toString());
			e.printStackTrace();
		}

		return "repair/msg/list";
	}
	
	@DeleteMapping("/msg/{id}")
	@RequiresPermissions(value = "msg:delete")
	@SystemControllerLog(description = "删除维修信息")
	public String delete(@PathVariable String id){
		String[] strids = id.split(",");
		for (String delids : strids) {
			RepairMsg mg = this.repairMsgService.findOneById(delids,RepairMsg.class);
			this.repairMsgService.remove(mg);// 删除某个id
		}
		return "redirect:/msgs";
	}
	
	
	/**
	 * 
	 * @param startTime  开始时间
	 * @param endTime    结束时间
	 * @param area       地区
	 * @param response   
	 * @throws IOException
	 */
    @GetMapping(value={"/getExcel/{startTime}/{endTime}/{area}/{rcId}",
    		"/getExcel/{startTime}/{endTime}/{area}"})
    public void cooperation(@PathVariable("startTime")String startTime,
    		@PathVariable("endTime")String endTime,
    		@PathVariable(required=false)String rcId,
    		@PathVariable("area")String area,HttpServletResponse response) throws IOException {
    	 	startTime=startTime.replaceAll("-", "").concat("000000000000");	
    	 	endTime=endTime.replaceAll("-", "").concat("000000000000");
    	 	Query query=new Query();
    	 	query.addCriteria(Criteria.where("orderId").gt(startTime).lt(endTime));
    	 	List<RepairMsg> rm=this.repairMsgService.find(query, RepairMsg.class);
    	 	if(Common.isNotEmpty(rcId)) {
    	 		RepairClass rc=this.repairClassServiceImpl.findOneById(rcId, RepairClass.class);
    	 		if(rc!=null&&rm!=null) {
    	 			rm=rm.stream().filter(r->r.getRepairclass().getId().equals(rc.getId())).collect(Collectors.toList());
    	 		}
    	 	}
    	 	
    	 	if(Common.isNotEmpty(rm)) {
    	 	List<MsgInfo> list = getList(rm,area);
        String fileName = "Excel文件";
        String sheetName = "第一个 sheet";
        ExcelUtil.writeExcel(response, list, fileName, sheetName, new MsgInfo());
    	}
    }

    	private List<MsgInfo> getList(List<RepairMsg> rm,String area){
    			List<RepairMsg> listofMsg=rm.stream().filter(msg->msg.getSchool().getArea().equals(area))
    			.collect(Collectors.toList());
    			List<MsgInfo> msgs=new ArrayList<>();
    			if(Common.isNotEmpty(listofMsg)) {
    			    listofMsg.stream().forEach(msg->{
    			    	MsgInfo info=new MsgInfo();
    			    	info.setContent(msg.getContent());
    			    	info.setCreateTime(Common.fromDateY(msg.getCreateTime()));
    			    	info.setNote(msg.getDonenote());
    			    	if(Common.isNotEmpty(msg.getRepairman())) {
    			    		info.setRepairmanname(msg.getRepairman().getName());
    			    	}
    			    	if(Common.isNotEmpty(msg.getRepairclass())) {
    			    		info.setRepairclass(msg.getRepairclass().getName());
    			    	}
    			    	info.setRepairtime(Common.isNotEmpty(msg.getDonetime())?msg.getDonetime():msg.getExpectedtime());
    			    	info.setSchoolname(msg.getSchool().getName());
    			    	info.setTeachername(msg.getPerson().getName());
    			    	info.setTeachertel(msg.getPerson().getTel());
    			    	info.setStatus(msg.getStatus()== 2?"完成":"进行中");
    			    	info.setTimelag(getTimelag(msg.getCreateTime()));
    			    	msgs.add(info);
    			    });
    			}
    			return msgs;
    	}
    	
    	/**
    	 * 获取传入日期与现在时间的间隔
    	 * @param date
    	 * @return
    	 */
    	private String getTimelag(Date date) {
    		Instant ins=date.toInstant();
    		ZoneId zId=ZoneId.systemDefault();
    		LocalDate date1=ins.atZone(zId).toLocalDate();
    		Period p=Period.between(date1,LocalDate.now());
    		String lag=p.getDays()+"天";
    		if(p.getMonths()!=0) {
    			lag=p.getMonths()+"月"+lag;
    		}
    		return lag;
    	}
    		
}
	
