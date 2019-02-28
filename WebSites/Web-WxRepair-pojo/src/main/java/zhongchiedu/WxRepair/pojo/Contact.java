package zhongchiedu.WxRepair.pojo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import zhongchiedu.general.pojo.User;

/**
 * 基础信息
 * @author cd
 *
 */


@Getter
@Setter
@Document
@ToString
public class Contact  extends User{

	private String city;    //市
	private String area;    //区
	private String addr;    //具体地址
	private String tel;     //手机号
	private String name;    //个人姓名
}
