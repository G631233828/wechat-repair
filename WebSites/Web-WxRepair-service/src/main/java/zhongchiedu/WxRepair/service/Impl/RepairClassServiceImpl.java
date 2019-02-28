package zhongchiedu.WxRepair.service.Impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import zhongchiedu.WxRepair.pojo.RepairClass;
import zhongchiedu.common.utils.BasicDataResult;
import zhongchiedu.common.utils.Common;
import zhongchiedu.framework.service.GeneralServiceImpl;

@Slf4j
@Service
public class RepairClassServiceImpl extends GeneralServiceImpl<RepairClass>{

	
	
	public void addOne(RepairClass repairClass) {
		this.save(repairClass);	
	}
	
	
	public void addOrUpdate(RepairClass repairClass) {
		if(repairClass != null) {
			if(Common.isNotEmpty(repairClass.getId())) {
			RepairClass rc=this.findOneById(repairClass.getId(), RepairClass.class);
			if(rc==null) {	rc=new RepairClass();}
			BeanUtils.copyProperties(repairClass, rc);
			this.save(rc);
			}else {
				RepairClass r=new RepairClass();
				BeanUtils.copyProperties(repairClass, r);
				this.insert(r);
			}
		}
	}
	
	
	public void deleteOne(String id) {
		this.findAndRemove(new Query()
				.addCriteria(Criteria.where("id").is(id)), RepairClass.class);
	}
	
	public void editOne(RepairClass repairClass) {
		this.insert(repairClass);
	}
	
	
	public RepairClass findOneById(String id) {
		RepairClass rc=this.findOneById(id);
		return rc!=null?rc:null;
	}
	
	public RepairClass findOneByName(String name) {
		Query query=new Query();
		query.addCriteria(Criteria.where("name").is(name));
		RepairClass repairClass=this.findOneByQuery(query, RepairClass.class);
		return repairClass!=null?repairClass:null;
	}
	
	
	public List<RepairClass> findAll(){
		List<RepairClass> repairClasses=this.find(new Query(), RepairClass.class);
		return repairClasses!=null?repairClasses:null;	
	}
	
	
	
	//查询重复名字
	public BasicDataResult ajaxgetRepletes(String Name) {
		if(Common.isNotEmpty(Name)){
			RepairClass repairClass = this.findOneByName(Name);
			if(repairClass!=null){
				return BasicDataResult.build(206,"产品分类重复", null);
			}
			return BasicDataResult.ok();
		}
		return BasicDataResult.build(400,"未能获取到请求的信息", null);
	}
	
	
}
