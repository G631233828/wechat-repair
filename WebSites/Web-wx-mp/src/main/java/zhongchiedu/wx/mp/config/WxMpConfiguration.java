package zhongchiedu.wx.mp.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import zhongchiedu.wx.mp.handler.LogHandler;
import zhongchiedu.wx.mp.handler.MsgHandler;


@Slf4j
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpConfiguration {
	
	
	
	private WxMpProperties properties;
	private LogHandler logHandler;
	private MsgHandler msgHandler;
	
	private static Map<String, WxMpService> mpServices=Maps.newHashMap();
    private static Map<String, WxMpMessageRouter> routers = Maps.newHashMap();
   
    public static Map<String,WxMpMessageRouter> getRouters(){
        return routers;
    }
    public static Map<String, WxMpService> getMpServices() {
        return mpServices;
    }

    
    @Autowired
    public WxMpConfiguration(WxMpProperties properties,LogHandler logHandler,
                             MsgHandler msgHandler) {
    	log.info("进去WxMpconfiguration的构造函数");
    	this.properties = properties;
        this.logHandler=logHandler;
        this.msgHandler=msgHandler;
    }
    
    @PostConstruct
	public void initServices() {
        final List<WxMpProperties.MpConfig> configs = this.properties.getConfigs();
        if (configs == null) {
            throw new RuntimeException("无法读取properties文件！");
        }
        mpServices = configs.stream().map(a -> {
            WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
            configStorage.setAppId(a.getAppId());
            configStorage.setSecret(a.getSecret());
            configStorage.setToken(a.getToken());
            configStorage.setAesKey(a.getAesKey());
            WxMpService service = new WxMpServiceImpl();
            service.setWxMpConfigStorage(configStorage);
            routers.put(a.getAppId(),this.initRouter(service));
            return service;
        }).collect(Collectors.toMap(s -> s.getWxMpConfigStorage().getAppId(), a -> a, (o, n) -> o));	
	}
    
    
    private WxMpMessageRouter initRouter(WxMpService wxMpService){
        final WxMpMessageRouter newRouter=new WxMpMessageRouter(wxMpService);
        newRouter.rule().handler(this.logHandler).next();
        newRouter.rule().async(false).handler(this.msgHandler).end();
        return newRouter;
    }
	
}
