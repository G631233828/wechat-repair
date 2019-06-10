package zhongchiedu.WxRepair.pojo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import zhongchiedu.WxRepair.util.MsgStatus;
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

	private String orderId;
	
	
	
	public RepairMsg() {
		//格式化当前时间
		SimpleDateFormat sfDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String strDate = sfDate.format(new Date());
		//获取3个随机数
		String random = getRandom620(3);
		//最后得到20位订单编号。
		this.orderId=strDate + random;
	}
	
	private String content;//报修内容
	private String teachernote;//教师备注
	private String managernote;//主管备注
	private String donenote;//完成维修备注

	private Integer status=MsgStatus.Create.getValue(); //报修状态
	
	private String evaluation;//老师评价
	private String expectedtime;//维修人员预期时间
	private String donetime;//完成时间
	@DBRef
	private List<MultiMedia> pictureofresults;//维修结果照片
	@DBRef
	private List<MultiMedia> fault;
	@DBRef
	private Teacher person;//报修人
	@DBRef
	private Repairman repairman;//维修人
	@DBRef
	private RepairClass repairclass;//分类所属
	
	@DBRef
	private School school;//所属学校
	public static String getRandom620(Integer length) {
		String result = "";
		Random rand = new Random();
		int n = 20;
		if (null != length && length > 0) {
			n = length;
		}
		int randInt = 0;
		for (int i = 0; i < n; i++) {
			randInt = rand.nextInt(10);
			result += randInt;
		}
		return result;
	}
	
	
}
