package zhongchiedu.WxRepair.util;


public enum TypeRepair {

	//主管,人员，抄送人
	Manager("manager"),Person("person"),Sendto("send");
	private String type;
 
	TypeRepair(String type) {
		 this.type=type;
	 }
	
	public String getType() {
		return this.type;
	}
}
