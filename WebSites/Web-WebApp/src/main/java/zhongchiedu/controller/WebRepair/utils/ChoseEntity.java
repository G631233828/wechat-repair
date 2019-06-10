package zhongchiedu.controller.WebRepair.utils;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChoseEntity {
	
	private String label;
	private String value;
	private List<ChoseEntity> children;

}
