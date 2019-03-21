package zhongchiedu.controller.WebRepair.wx;



import com.google.gson.Gson;

import zhongchiedu.WxRepair.pojo.Repairman;

public class Test {

	
		public static void main(String[] args) {
			Repairman c=new Repairman();
			c.setCity("asd");
			c.setTel("gesgwe");
			String ts=c.toString();
			System.out.println(ts.toString());
			Gson g=new Gson();
		}
}
