package zhongchiedu.controller.WebRepair.utils;

import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import zhongchiedu.WxRepair.pojo.RepairMsg;

public class TemplateMsgUtil {
		
	
		public  interface templateService{
			WxMpTemplateMessage getTemplateById(TemplateIdEnum em,RepairMsg msg,String url);
		}
		

		public  templateService service=(em,msg,url)->{
			WxMpTemplateMessage templateMessage=WxMpTemplateMessage.builder()
					.templateId(em.getTemplateid())
					.build();
			if(em==TemplateIdEnum.RepairToast) {
				templateMessage.addData(new WxMpTemplateData("first","","#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword1",msg.getPerson().getSchoolName(),"#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword2","点击查看","#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword3",msg.getPerson().getAddr(),"#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword4",msg.getCreateDate(),"#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword5",msg.getTeachernote(),"#000000"));
				templateMessage.setUrl(url);
			}
			if(em==TemplateIdEnum.toManToast) {
				templateMessage.addData(new WxMpTemplateData("first","您有新的维修消息，请及时处理!","#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword1",msg.getPerson().getName(),"#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword2",msg.getPerson().getAddr(),"#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword3",msg.getPerson().getTel(),"#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword4",msg.getRepairclass().getName(),"#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword5",msg.getManagernote(),"#000000"));
				templateMessage.setUrl(url);
			}
			if(em==TemplateIdEnum.feedback) {
				templateMessage.addData(new WxMpTemplateData("first","老师，您的报修已经受理","#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword1",msg.getOrderId(),"#000000"));
				templateMessage.addData(new WxMpTemplateData("keyword2",msg.getPerson().getAddr()+"("+msg.getPerson().getSchoolName()+")","#000000"));
				templateMessage.addData(new WxMpTemplateData("remark","请耐心等待","#000000"));
				templateMessage.setUrl(url);
			}
			return templateMessage;
		};
}
