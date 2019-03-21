package zhongchiedu.WxRepair.pojo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 维修方
 * @author cd
 *
 */
@Getter
@Setter
@Document
public class Repairman extends Contact{
		
	private String type;
}
