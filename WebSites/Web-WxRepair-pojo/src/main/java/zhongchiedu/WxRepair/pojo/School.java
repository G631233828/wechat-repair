package zhongchiedu.WxRepair.pojo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import zhongchiedu.framework.pojo.GeneralBean;



@Setter
@Getter
@Document
public class School extends GeneralBean<School>{

	private String name;
	private String addr;
	private String area;
}
