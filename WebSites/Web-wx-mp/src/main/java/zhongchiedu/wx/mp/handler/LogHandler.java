package zhongchiedu.wx.mp.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import zhongchiedu.wx.mp.utils.JsonUtils;


@Slf4j
@Component
public class LogHandler extends AbstractHandler{

	
	
	
	public LogHandler() {
		log.info("----------------LogHandler初始化中");
	}
	
	@Override
	public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
			WxSessionManager sessionManager) throws WxErrorException {
		this.logger.info("\n接收到请求消息,内容:{}-Handler:LogHandler",JsonUtils.toJson(wxMpService));
		return null;
	}

}
