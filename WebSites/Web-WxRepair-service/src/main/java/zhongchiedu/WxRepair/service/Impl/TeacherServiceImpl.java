package zhongchiedu.WxRepair.service.Impl;


import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import zhongchiedu.WxRepair.pojo.Teacher;
import zhongchiedu.common.utils.BasicDataResult;
import zhongchiedu.common.utils.Common;
import zhongchiedu.framework.service.GeneralServiceImpl;
import zhongchiedu.wechat.oauto2.NSNUserInfo;


@Repository
public class TeacherServiceImpl	extends GeneralServiceImpl<Teacher> {

		public BasicDataResult addorUpdate(Teacher t) {
			if (Common.isNotEmpty(t)) {
				if (Common.isNotEmpty(t.getId())) {
					// 执行修改操作
					Teacher teacher = this.findOneById(t.getId(),Teacher.class);
					if (teacher == null)
						teacher = new Teacher();
					BeanUtils.copyProperties(t, teacher);
					this.save(teacher);
					t=teacher;
				} else {
					Teacher tr = new Teacher();
					BeanUtils.copyProperties(t,tr);
					// 执行添加操作
					this.insert(tr);
					t=tr;
				}
				return BasicDataResult.ok(t.getId());
			}else {
				return BasicDataResult.build(203, "填写的信息不能为空", null);
			}
		}
		
		
		/**
		 * 根据openid获得老师
		 * @param openid
		 * @return
		 */
		public Teacher findByOpenId(String openid) {
			Query query = new Query();
			query.addCriteria(Criteria.where("openid").is(openid));
			Teacher t = this.findOneByQuery(query, Teacher.class);
			return t!=null?t:null;
		}
		
		
}
