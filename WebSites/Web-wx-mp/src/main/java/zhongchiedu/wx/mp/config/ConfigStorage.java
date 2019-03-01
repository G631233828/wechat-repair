package zhongchiedu.wx.mp.config;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;

@XStreamAlias("xml")
public class ConfigStorage  extends WxMpInMemoryConfigStorage{

    private String qrconnectRedirectUrl;
    private String templateId;


	public String getQrconnectRedirectUrl() {
		return qrconnectRedirectUrl;
	}
	public void setQrconnectRedirectUrl(String qrconnectRedirectUrl) {
		this.qrconnectRedirectUrl = qrconnectRedirectUrl;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
