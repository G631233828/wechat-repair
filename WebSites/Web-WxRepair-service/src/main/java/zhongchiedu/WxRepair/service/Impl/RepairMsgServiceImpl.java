package zhongchiedu.WxRepair.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import zhongchiedu.WxRepair.pojo.RepairMsg;
import zhongchiedu.WxRepair.pojo.Teacher;
import zhongchiedu.WxRepair.util.MsgStatus;
import zhongchiedu.common.utils.BasicDataResult;
import zhongchiedu.common.utils.Common;
import zhongchiedu.framework.service.GeneralServiceImpl;

@Service
public class RepairMsgServiceImpl extends GeneralServiceImpl<RepairMsg> {
		
	@Autowired
	private TeacherServiceImpl teacherService;
	
	
		/**
		 * 新添加报修信息
		 * @param msg
		 * @param id
		 */
		public BasicDataResult edit(RepairMsg msg,String id) {
			if(Common.isEmpty(id)) {
				return BasicDataResult.build(201, "未识别用户请重新操作", null);
			};
			if(Common.isNotEmpty(msg)&&Common.isNotEmpty(id)) {
				if(Common.isEmpty(msg.getContent())) {
					return BasicDataResult.build(201, "请填写报修的内容", null);
				}
				Teacher t=this.teacherService.findOneById(id, Teacher.class);
				msg.setPerson(t);
				this.insert(msg);
				return BasicDataResult.build(200, "提交成功", msg);
			}
			return BasicDataResult.build(201, "提交信息为空", null);
		}
		
			/**
			 * 根据老师的id查询status为0和1的单子
			 * @param teacherid
			 * @return
			 */
			public List<RepairMsg> findByTeacherIdAndStatus(String teacherid,Integer...ints){
				Query query=new Query();
				query.addCriteria(Criteria.where("person.id").is(teacherid));
				query=this.service.statuService(query,ints);
				List<RepairMsg> lrm=this.find(query, RepairMsg.class);
				return lrm!=null?lrm:null;
			}
			
			
			/**
			 * 	根据repairman的id和状态 
			 * @param manId
			 * @param ints
			 * @return  msg的集合
			 */
			public List<RepairMsg> findListByIdAndStatus(String manId,Integer... ints){
				Query query=new Query();
				query.addCriteria(Criteria.where("repairman.$id").is(new ObjectId(manId)));
				if(ints.length>=1) {
					query=this.service.statuService(query,ints);
				}
				List<RepairMsg> lrm=this.find(query, RepairMsg.class);
				return lrm!=null?lrm:null;
			}
			
			/**
			 * 根据地区和状态查看保修单子
			 * @param area
			 * @return
			 */
			public List<RepairMsg> areastatusList(String area,Integer...ints){
				Query query=new Query();
				List<Teacher> ts=this.teacherService.find(new Query().addCriteria(Criteria
						.where("area").is(area)), Teacher.class);
				List<ObjectId> oids=new ArrayList<>();
				ts.stream().forEach(t->{
					 ObjectId id=new ObjectId(t.getId());
					 oids.add(id);
				});
				query.addCriteria(Criteria.where("person.$id").in(oids));
				query=service.statuService(query, ints);
				List<RepairMsg> list=this.find(query, RepairMsg.class);
				return list!=null?list:null;
			}
			
			
			
			
			
			interface queryService{
				Query statuService(Query query,Integer... ints);
			}
			
			 queryService service=(Query query,Integer... ints)->{
				if(ints.length>=1) {
					query.addCriteria(Criteria.where("status").in(ints));
				}
					return query;
			};
			
			/**
			 * 根据id查询维修单信息
			 * @param id
			 * @return
			 */
			public RepairMsg GetMsgByid(String id) {
				RepairMsg msg=this.findOneById(id, RepairMsg.class);
				return msg!=null?msg:null;
			}
}
