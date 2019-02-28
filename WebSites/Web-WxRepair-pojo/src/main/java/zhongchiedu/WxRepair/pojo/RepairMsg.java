package zhongchiedu.WxRepair.pojo;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import zhongchiedu.framework.pojo.GeneralBean;
import zhongchiedu.general.pojo.MultiMedia;

/**
 * 维修信息
 * @author cd
 *
 */

@Document
@Setter
@Getter
@ToString
public class RepairMsg extends GeneralBean<RepairMsg>{

	
	private String content;//报修内容
	private String teachernote;//教师备注
	private String managernote;//主管备注
	private String donenote;//完成维修备注
	
	private String evaluation;//老师评价
	private String expectedtime;//维修人员预期时间
	private String donetime;//完成时间
	@DBRef
	private MultiMedia pictureofresult;//维修结果照片
	@DBRef
	private Teacher person;//报修人
	@DBRef
	private Repairman repairman;//维修人
	@DBRef
	private RepairClass repairclass;//分类所属
	
	
	
	
	
}
