package org.sample.qbintg.intg.controller;

import static org.sample.qbintg.intg.model.DataType.BILL;
import static org.sample.qbintg.intg.model.DataType.BILL_PAYMENT;
import static org.sample.qbintg.intg.model.MessageEnum.BEARER_ERROR;
import static org.sample.qbintg.intg.model.MessageEnum.CONNECTION_INCOMPLETE;
import static org.sample.qbintg.intg.model.MessageEnum.GENERIC_ERROR;
import static org.sample.qbintg.intg.model.MessageEnum.REFRESH_ERROR;
import static org.sample.qbintg.intg.model.MessageEnum.RETRIEVE_BILLPAYMENT_ERROR;
import static org.sample.qbintg.intg.model.MessageEnum.RETRIEVE_BILL_ERROR;
import static org.sample.qbintg.intg.model.MessageEnum.SETUP_ERROR;
import static org.sample.qbintg.intg.model.MessageEnum.UNAUTH_ERROR;
import static org.sample.qbintg.intg.util.Utilities.getMessage;
import static org.sample.qbintg.intg.util.Utilities.getResultJsonString;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sample.qbintg.OAuth2PlatformClientFactory;
import org.sample.qbintg.intg.helper.DataHelper;
import org.sample.qbintg.intg.model.BillModelView;
import org.sample.qbintg.intg.model.BillPaymentModelView;
import org.sample.qbintg.intg.model.DataQuery;
import org.sample.qbintg.intg.model.DataType;
import org.sample.qbintg.intg.model.MakePaymentModalView;
import org.sample.qbintg.intg.model.MessageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.Bill;
import com.intuit.ipp.data.BillPayment;
import com.intuit.ipp.data.Error;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.security.OAuth2Authorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.Config;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;

@RestController
public class RestAPIController {

	private static final Logger logger = Logger.getLogger(RestAPIController.class);

	@Autowired
	private OAuth2PlatformClientFactory factory;

	private DataService dataService;

	@RequestMapping("/health")
	public String checkHealth(){
		return "Working fine";
	}

	private String validateConfig(HttpSession session){
		String failureMsg="";
		try {
			String realmId = (String)session.getAttribute("realmId");
			if (StringUtils.isEmpty(realmId)) {
				failureMsg =getMessage(CONNECTION_INCOMPLETE);
			}
			String accessToken = (String)session.getAttribute("access_token");
			String url = factory.getPropertyValue("IntuitAccountingAPIHost") + "/v3/company";
			Config.setProperty(Config.BASE_URL_QBO, url);
			getDataService(realmId, accessToken);
		}
		catch (FMSException e) {
			List<Error> list = e.getErrorList();
			list.forEach(error -> logger.error(SETUP_ERROR.desc() + error.getMessage()));
			return getMessage(CONNECTION_INCOMPLETE);
		}
		catch(Exception e){
			logger.error(GENERIC_ERROR.desc(),e);
			return getMessage(GENERIC_ERROR);
		}
		return failureMsg;
	}


	private void getDataService(String realmId, String accessToken) throws FMSException {
		OAuth2Authorizer oauth = new OAuth2Authorizer(accessToken);
		Context context = new Context(oauth, ServiceType.QBO, realmId); 
		dataService =  new DataService(context);
	}


	/*
	 * This method is called to handle 401 status code - 
	 * If a 401 response is received, refresh tokens should be used to get a new access token,
	 * and the API call should be tried again.
	 */

	private String  refreshTokens(HttpSession session) throws Exception {
		try{
			OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
			String refreshToken = (String)session.getAttribute("refresh_token");
			BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken);
			session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
			session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());
			getDataService((String)session.getAttribute("realmId"), bearerTokenResponse.getAccessToken());
			return "";
		}
		catch (OAuthException e1) {
			logger.error(BEARER_ERROR.desc() + e1.getMessage());
			return getMessage(REFRESH_ERROR);
		}
		catch (FMSException e) {
			List<Error> list = e.getErrorList();
			list.forEach(error -> logger.error(SETUP_ERROR.desc() + error.getMessage()));
			return getMessage(CONNECTION_INCOMPLETE);
		}

	}



	private String getData(HttpSession session, DataType dataType){
		String failureMsg="",sql="";
		MessageEnum messageEnum = GENERIC_ERROR ;
		if(dataType.equals(BILL)){
			messageEnum= RETRIEVE_BILL_ERROR;
			failureMsg = RETRIEVE_BILL_ERROR.desc();
			sql = DataQuery.OUTSTANDING_BILL_QUERY;
		}
		else if(dataType.equals(BILL_PAYMENT)){
			messageEnum= RETRIEVE_BILLPAYMENT_ERROR;
			failureMsg = RETRIEVE_BILLPAYMENT_ERROR.desc();
			sql = DataQuery.BILLPAYMENT_QUERY;
		}

		try {
			failureMsg = validateConfig(session);
			if(!StringUtils.isEmpty(failureMsg)){
				return failureMsg;
			}
			QueryResult queryResult = dataService.executeQuery(sql);
			return processResponseForType(queryResult,dataType);
		}
		catch (InvalidTokenException e) {			
			logger.error(failureMsg + e.getMessage());
			logger.info(UNAUTH_ERROR.desc());
			try {
				refreshTokens(session);
				logger.info("Calling data service again");
				QueryResult queryResult = dataService.executeQuery(sql);
				return processResponseForType(queryResult,dataType);
			} 
			catch (FMSException e2) {
				logger.error(failureMsg + e2.getMessage());
				return getMessage(messageEnum);
			}
			catch (Exception e1) {
				logger.error("Retry failed with fresh token" + e.getMessage());
				return getMessage(messageEnum);
			}

		} 
		catch (FMSException e2) {
			logger.error(failureMsg + e2.getMessage());
			return getMessage(messageEnum);
		}
	}

	/*
	 * move to factory
	 */
	private String processResponseForType(QueryResult queryResult, DataType dataType) {
		if(dataType.equals(BILL)){
			return processBillResponse(queryResult);
		}
		else if(dataType.equals(BILL_PAYMENT)){
			return processBillPaymentResponse(queryResult);
		}
		return getMessage(GENERIC_ERROR);
	}

	/**
	 * Bills QBO API call using OAuth2 tokens
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getBills")
	public String getBills(HttpSession session) {
			return getData(session, BILL);
	}

	
	@ResponseBody
	@RequestMapping("/getBillPayments")
	public String getBillPayments(HttpSession session) {
			return getData(session, BILL_PAYMENT);
	}
	

	
	@ResponseBody
	@RequestMapping(value = "/makePayment", method = RequestMethod.POST)
	public String makePayment(HttpSession session, @RequestBody MakePaymentModalView makePayment) {
		logger.info("Inside Payment object");
		String failureMsg = validateConfig(session);
		boolean isPaid = false;
		if(!StringUtils.isEmpty(failureMsg)){
			return failureMsg;
		}
		try{
			isPaid= DataHelper.createBillPayment(dataService,makePayment);
		}
		catch(Exception e){
			return MessageEnum.UNABLE_PAYMENT_ERROR.desc();
		}
		logger.info("Bill Paid : " + isPaid);
		return "Payment Completed";
	}
	


	private String processBillPaymentResponse(QueryResult queryResult) {
		if (!queryResult.getEntities().isEmpty() && queryResult.getEntities().size() > 0) {
			try {
				List<BillPayment> data = (List<BillPayment>)queryResult.getEntities();
				logger.info("Total number of data: " + data.size());
				List<BillPaymentModelView> views = new ArrayList<>();
				if(CollectionUtils.isNotEmpty(data)){
					data.stream().forEach(bill -> views.add(new BillPaymentModelView(bill)));
				}
				else{
					return getMessage(RETRIEVE_BILL_ERROR);
				}
				return getResultJsonString(views);
			} catch (Exception e) {
				logger.error(GENERIC_ERROR.desc(), e);
				return getMessage(GENERIC_ERROR);
			}
		}
		return GENERIC_ERROR.desc();
	}
	
	private String processBillResponse(QueryResult queryResult) {
		if (!queryResult.getEntities().isEmpty() && queryResult.getEntities().size() > 0) {
			try {
				List<Bill> data = (List<Bill>)queryResult.getEntities();
				logger.info("Total number of data: " + data.size());
				List<BillModelView> views = new ArrayList<>();
				if(CollectionUtils.isNotEmpty(data)){
					data.stream().forEach(bill -> views.add(new BillModelView(bill)));
				}
				else{
					return getMessage(RETRIEVE_BILL_ERROR);
				}
				return getResultJsonString(views);
			} catch (Exception e) {
				logger.error(GENERIC_ERROR.desc(), e);
				return getMessage(GENERIC_ERROR);
			}
		}
		return GENERIC_ERROR.desc();
	}

}
