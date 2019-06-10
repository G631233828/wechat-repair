package zhongchiedu.controller.WebRepair.utils;

public enum TemplateIdEnum {
	//设备维修通知  (售后主管)
	RepairToast("NfACHKU0YE0kkjqgfkQ1-nhQfA4byBu-7dKpQgGRC0Q"),
	
	//新维修单通知  (售后维修)
	toManToast("UcoLh4c8dEyfwYdkVBWdGU8_5rdxuzZRwbSAlnt6Wjo"),
	
	toSendToast("asdasdsdasasdasd1"),
	//接收任务消息反馈给老师
	feedback("gvIOcxeiX1Q0V3Vc0wE0uiWOjrOz_PF6vYZh0rwM-Yk"),
	
	//受理结果
	resultback("EczwS8JRd1XeqdP1fuOh-vf56Z95ZnStMhYKZKmdtzo");
	
	
	private String templateid;
	TemplateIdEnum(String templateid){
		this.templateid=templateid;
	}
	public String getTemplateid() {
		return this.templateid;
	}
}
