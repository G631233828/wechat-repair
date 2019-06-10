package zhongchiedu.controller.WebRepair.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@PropertySource(value={"classpath:url.properties"})
@Getter
@Setter
public class UrlConfig {
	@Value("${port}")
	private int port;
	@Value("${scheme}")
	private String scheme;
	@Value("${servletname}")
	private String servletname;
	@Value("${contextpath}")
	private String contextpath;
}
