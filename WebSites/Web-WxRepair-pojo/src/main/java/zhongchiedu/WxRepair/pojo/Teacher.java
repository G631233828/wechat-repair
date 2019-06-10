package zhongchiedu.WxRepair.pojo;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;


/**
 * 报修老师
 * @author cd
 *
 */

@Getter
@Setter
@Document
public class Teacher extends Contact{

	private String schoolid;
	private String schoolName;
	

	
//	@Override
//  public String toString() {
//    String[] excludeFlieds=new String[] {"createTime"};
//	ReflectionToStringBuilder.
//	setDefaultStyle(ToStringStyle.JSON_STYLE);
//	return ReflectionToStringBuilder.toStringExclude(this,excludeFlieds);
//	}

}
