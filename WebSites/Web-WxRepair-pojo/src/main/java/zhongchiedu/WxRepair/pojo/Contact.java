package zhongchiedu.WxRepair.pojo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import zhongchiedu.general.pojo.User;

/**
 * 基础信息
 * @author cd
 *
 */


@Getter
@Setter
public class Contact  extends User{

	private String city;    //市
	private String area;    //区
	private String addr;    //具体地址
	private String tel;     //手机号
	private String name;    //个人姓名
	private String openid;//微信相关

	@Override
	  public String toString() {
	    String[] excludeFlieds=new String[] {"createTime","role","resource"};
		ReflectionToStringBuilder.
		setDefaultStyle(ToStringStyle.JSON_STYLE);
		return ReflectionToStringBuilder.toStringExclude(this,excludeFlieds);
		}

}
