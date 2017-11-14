package org.sample.qbintg;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.Environment;
import com.intuit.oauth2.config.OAuth2Config;

@Service
@PropertySource(value="classpath:/application.properties", ignoreResourceNotFound=false)
public class OAuth2PlatformClientFactory {
	
	private static final Logger logger = Logger.getLogger(OAuth2PlatformClientFactory.class);
	
	@Autowired
	private org.springframework.core.env.Environment env;
	private OAuth2PlatformClient client;
	private OAuth2Config oauth2Config;
	
	@PostConstruct
	public void init() {
		logger.info("Initializing the Oauth factory for Quickbook connection.");
		// Initialize a single thread executor, this will ensure only one thread processes the queue
		oauth2Config = new OAuth2Config.OAuth2ConfigBuilder(env.getProperty("OAuth2AppClientId"), env.getProperty("OAuth2AppClientSecret")) //set client id, secret
				.callDiscoveryAPI(Environment.SANDBOX) // call discovery API to populate urls
				.buildConfig();
		
		logger.info("clientId"+oauth2Config.getClientId());
		logger.info("clientSecret"+oauth2Config.getClientSecret());
		logger.info("intuitIdTokenIssuer"+oauth2Config.getIntuitIdTokenIssuer());
		logger.info("intuitAuthorizationEndpoint"+oauth2Config.getIntuitAuthorizationEndpoint());
		logger.info("intuitBearerTokenEndpoint"+oauth2Config.getIntuitBearerTokenEndpoint());
		logger.info("intuitRevokeTokenEndpoint"+oauth2Config.getIntuitRevokeTokenEndpoint());
		logger.info("intuitJwksURI"+oauth2Config.getIntuitJwksURI());
		logger.info("userProfileEndpoint"+oauth2Config.getUserProfileEndpoint());
		client  = new OAuth2PlatformClient(oauth2Config);
	}
	
	
	public OAuth2PlatformClient getOAuth2PlatformClient()  {
		return client;
	}
	
	public OAuth2Config getOAuth2Config()  {
		return oauth2Config;
	}
	
	public String getPropertyValue(String proppertyName) {
		return env.getProperty(proppertyName);
	}

}