package zhongchiedu.WxRepair.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import zhongchiedu.WxRepair.pojo.Repairman;
import zhongchiedu.common.utils.BasicDataResult;
import zhongchiedu.common.utils.Common;
import zhongchiedu.framework.service.GeneralServiceImpl;
import zhongchiedu.general.pojo.Resource;
import zhongchiedu.general.pojo.Role;
import zhongchiedu.general.pojo.User;
import zhongchiedu.general.service.Impl.RoleServiceImpl;
import zhongchiedu.general.service.Impl.UserServiceImpl;

@Repository
public class RepairmanServiceImpl extends GeneralServiceImpl<Repairman> {

	

	@Autowired
	private RoleServiceImpl roleService;
	
	@Autowired
	private UserServiceImpl userService;
	
	/**
	 * 人团添加或修改
	 * 
	 * @param man
	 * @param roleId
	 * @return
	 */
	public void saveOrUpdateMan(Repairman man, String roleId) {
		if (Common.isNotEmpty(man)) {
			if (Common.isNotEmpty(man.getId())) {
				// 执行修改操作
				Repairman repairman = this.findOneById(man.getId(),Repairman.class);
				if (repairman == null)
					repairman = new Repairman();
				String accountName=repairman.getAccountName();
				BeanUtils.copyProperties(man, repairman);
				repairman.setAccountName(accountName);
				Role role = this.roleService.findRoleById(roleId);
				// 通过hid获取角色信息，如果为null 返回null否则返回role
				repairman.setRole(role != null ? role : null);
				this.save(repairman);
			} else {
				Repairman rm = new Repairman();
				BeanUtils.copyProperties(man, rm);
				rm.setResource(new ArrayList<Resource>());
				Role role = this.roleService.findRoleById(roleId);
				rm.setRole(role != null ? role : null);
				// 执行添加操作
				this.insert(rm);
			}
		}
	}
	
	public List<Repairman> findAllMan() throws Exception {
		List<Repairman> listman = this.find(new Query(), Repairman.class);
		if (listman != null)
			return listman;
		else
			return null;
	}
	
	public List<Repairman> findAllManByArea(String area) throws Exception {
		Query query=new Query();
		query.addCriteria(Criteria.where("area").is(area));
		List<Repairman> listman = this.find(query, Repairman.class);
		if (listman != null)
			return listman;
		else
			return null;
	}
	
	/**
	 * 根据ids查看mans
	 * @param ids
	 * @return
	 */
	public List<Repairman> findByIds(String[] ids){
		List<ObjectId> oids=new ArrayList<>();
		for (String id : ids) {
			ObjectId oid=new ObjectId(id);
			oids.add(oid);
		}
		Query query=new Query();
		query.addCriteria(Criteria.where("_id").in(oids));
		List<Repairman> mans=this.find(query, Repairman.class);
		return mans!=null?mans:null;
	}
	
	
	
	
	/**
	 * 根据人员帐号查询人员信息
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public Repairman findManByAccountName(String accountName) {
		Query query = new Query();
		query.addCriteria(Criteria.where("accountName").is(accountName));
		User user=this.userService.findUserByAccountName(accountName);
		Repairman man=null;
		if(user!=null) {
			man=new Repairman();
			man.setAccountName("has acount");
		}else {
			man=this.findOneByQuery(query, Repairman.class);
		}
		return man;
	}
			
			/**
			 *
			 * @param name
			 * @return
			 */
			public Repairman findByName(String name) {
				Query query=new Query();
				query.addCriteria(Criteria.where("name").is(name));
				Repairman man=this.findOneByQuery(query, Repairman.class);
				return man!=null?man:null;
			}
			
	
		
	
	public BasicDataResult checklogin(Repairman man) {
		String accountName=man.getName();
		String passWord=man.getTel();
		if(Common.isNotEmpty(accountName)&&Common.isNotEmpty(passWord)) {
			Query query=new Query();
			query.addCriteria(Criteria.where("name").is(accountName)
					.and("tel").is(passWord));
		Repairman repairman=this.findOneByQuery(query, Repairman.class);
		if(repairman!=null ) {
			if(!man.getOpenid().equals(repairman.getOpenid())) {
				repairman.setOpenid(man.getOpenid());
			}
			this.save(repairman);
		}
		return repairman!=null?BasicDataResult.build(200, "登录成功", repairman):BasicDataResult.build(202, "登录失败", man);
	}
		return BasicDataResult.build(203, "帐号或密码为空", null);
}	
	
	public Repairman findByOpenid(String openid) {
		Query query=new Query();
		query.addCriteria(Criteria.where("openid").is(openid));
		Repairman man=this.findOneByQuery(query, Repairman.class);
		return man!=null?man:null;
	}
	
	
	
	//查询重复帐号
	public BasicDataResult ajaxgetRepletes(String accountName) {
		if(Common.isNotEmpty(accountName)){
			Repairman man = this.findManByAccountName(accountName);
			
			if(man!=null){
				return BasicDataResult.build(206,"当前账号已经被注册，请重新输入", null);
			}
			
			return BasicDataResult.ok();
		}
		
		return BasicDataResult.build(400,"未能获取到请求的信息", null);
	}
	
	
}
