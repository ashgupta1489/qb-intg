package org.sample.qbintg.intg.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.sample.qbintg.OAuth2PlatformClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.OAuth2Config;
import com.intuit.oauth2.config.Scope;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.InvalidRequestException;
import com.intuit.oauth2.exception.OAuthException;

@Controller
public class QuickBookOAuthController {

	@Autowired
	private OAuth2PlatformClientFactory factory;

	private final AtomicLong counter = new AtomicLong();
	private static final Logger logger = Logger.getLogger(QuickBookOAuthController.class);

	
	@RequestMapping("/")
	public String login() {
		return "dashboard";
	}
	/*
	@RequestMapping("/error")
	public String error() {
		return "error";
	}*/
	
	
	@RequestMapping("/dashboard")
	public String home() {
		return "dashboard";
	}

	@RequestMapping("/connected")
	public String connected() {
		return "connected";
	}


	/**
	 * Controller mapping for connectToQuickbooks button
	 * @return
	 */
	@RequestMapping("/connectToQuickbooks")
	public View connectToQuickbooks(HttpSession session) {
		logger.info("inside connectToQuickbooks ");
		OAuth2Config oauth2Config = factory.getOAuth2Config();
		String redirectUri = factory.getPropertyValue("OAuth2AppRedirectUri"); 
		String csrf = oauth2Config.generateCSRFToken();
		session.setAttribute("csrfToken", csrf);
		try {
			List<Scope> scopes = new ArrayList<Scope>();
			scopes.add(Scope.Accounting);
			return new RedirectView(oauth2Config.prepareUrl(scopes, redirectUri, csrf), true, true, false);
		} catch (InvalidRequestException e) {
			System.out.println("Exception calling connectToQuickbooks ");
		}
		return null;
	}


	@RequestMapping("/oauth2redirect")
	public String oauthCallBack(@RequestParam("code") String authCode, @RequestParam("state") String state, @RequestParam(value = "realmId", required = false) String realmId, HttpSession session) {
		logger.info("Inside OAuth callback method " + counter.getAndIncrement());
		logger.info("Auth Code : "+ authCode);
		logger.info("State (Unique Id): "+ state);
		logger.info("Company Id : "+ realmId);
		try {
			String csrfToken = (String) session.getAttribute("csrfToken");
			if (csrfToken.equals(state)) {
				session.setAttribute("realmId", realmId);
				session.setAttribute("auth_code", authCode);
				OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
				String redirectUri = factory.getPropertyValue("OAuth2AppRedirectUri");
				logger.info("inside oauth2redirect of sample -- redirectUri " + redirectUri  );
				BearerTokenResponse bearerTokenResponse = client.retrieveBearerTokens(authCode, redirectUri);
				session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
				session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());
				logger.info("AccessToken Id: "+ bearerTokenResponse.getAccessToken());
				logger.info("Referesh Token "+ bearerTokenResponse.getRefreshToken());
				return "dashboard";
			}
			logger.info("csrf token mismatch " );
		} catch (OAuthException e) {
			logger.error("Exception in callback handler ",e);
		} 
		return null;
	}

}