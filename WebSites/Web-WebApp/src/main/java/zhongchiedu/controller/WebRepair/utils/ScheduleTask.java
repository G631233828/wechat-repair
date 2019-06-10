package zhongchiedu.controller.WebRepair.utils;


import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import zhongchiedu.WxRepair.pojo.RepairMsg;
import zhongchiedu.WxRepair.service.Impl.RepairMsgServiceImpl;
import zhongchiedu.WxRepair.util.MsgStatus;
import zhongchiedu.wx.mp.config.WxMpConfiguration;



@Component
@Configuration
@EnableScheduling
public class ScheduleTask {

	
			@Autowired
			private RepairMsgServiceImpl repairMsgService;
			
			@Resource
			private UrlConfig url;

			
			@Value("${wxappid}")
			private String appid;
			
			/**
			 * 每天中午推送一次
			 * @throws MalformedURLException 
			 */
//			@Scheduled(cron="* * * * * ?")
			@Scheduled(cron = "0 0 12 1/1 * ?")
			public void configureTask() throws MalformedURLException {
		       Query query=new Query();
		       query.addCriteria(Criteria.where("status").is(MsgStatus.Get.getValue()));
		       List<RepairMsg> list=this.repairMsgService.find(query, RepairMsg.class);
		       if(list!=null) {
		    	  list=list.stream().filter(rm->compareDate(rm.getCreateTime())).
		    	   collect(Collectors.toList());
		       }
		    	WxMpService wxService=WxMpConfiguration.getMpServices().get(appid);
		    	WxMpTemplateMsgService wtms=wxService.getTemplateMsgService();
		    	TemplateMsgUtil util=new TemplateMsgUtil();
				String basePath = url.getScheme()+"://"+url.getServletname()+":"+url.getPort()+url.getContextpath()+"/";  
				String concaturl=basePath+"wx/rp/InRepair/%s/?manid=%s";
				System.out.println(concaturl);
				list.stream().forEach(rm->{
					//todo
				});
			}
			
			
			
			/**
			 * 判断创建日期和当前日期是否相等
			 * @param createdate
			 * @return
			 */
			private boolean compareDate(Date createdate) {
	    		Instant ins=createdate.toInstant();
	    		ZoneId zId=ZoneId.systemDefault();
	    		LocalDate date1=ins.atZone(zId).toLocalDate();
				return LocalDate.now().compareTo(date1)==0?true:false;
			}
			
}
