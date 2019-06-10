package zhongchiedu.WxRepair.util;

public enum MsgStatus {
	/**
	 * 创建初始状态0
	 * 主管分配任务状态4
	 * 任务指定人员接受状态1
	 * 
	 * 
	 */
	Create(0),Get(1),Done(2),Canel(3),Send(4);
	private int value;
	
	MsgStatus(int value){
		this.value=value;
	}
	
	public int getValue() {
		return this.value;
	}
	

}
