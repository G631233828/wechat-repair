package zhongchiedu.WxRepair.util;

public enum MsgStatus {
 
	Create(0),Get(1),Done(2),Canel(3),Send(4);
	private int value;
	
	MsgStatus(int value){
		this.value=value;
	}
	
	public int getValue() {
		return this.value;
	}
	

}
