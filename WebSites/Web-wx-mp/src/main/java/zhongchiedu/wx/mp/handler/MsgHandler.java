package zhongchiedu.wx.mp.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import zhongchiedu.wx.mp.builder.TextBuilder;
import zhongchiedu.wx.mp.utils.JsonUtils;

@Component
public class MsgHandler extends AbstractHandler {

	@Override
	public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
			WxSessionManager sessionManager) throws WxErrorException {
		
		logger.info("wxMessage是：{}",JsonUtils.toJson(wxMpService));	
		return new TextBuilder().build("收到信息内容："+JsonUtils.toJson(wxMpService), wxMessage, wxMpService);
	}

}
