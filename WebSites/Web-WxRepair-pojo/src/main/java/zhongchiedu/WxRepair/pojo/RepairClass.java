package zhongchiedu.WxRepair.pojo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import zhongchiedu.framework.pojo.GeneralBean;

/**
 * 维修分类
 * @author cd
 *
 */

@Setter
@Getter
@ToString
@Document
public class RepairClass  extends GeneralBean<RepairClass>{

	private String name;//分类名
}
