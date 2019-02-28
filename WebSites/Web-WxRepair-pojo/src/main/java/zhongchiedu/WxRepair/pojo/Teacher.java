package zhongchiedu.WxRepair.pojo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 报修老师
 * @author cd
 *
 */

@Getter
@Setter
@Document
@ToString
public class Teacher extends Contact{

	private String schoolName;
}
