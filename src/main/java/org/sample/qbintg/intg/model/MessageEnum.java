package org.sample.qbintg.intg.model;

public enum MessageEnum {

	CONNECTION_INCOMPLETE("No Company found. Check if Connectiong to Quickbook is successfull"),
	SETUP_ERROR("Error while handling setup :: "),
	BEARER_ERROR("Error while calling bearer token :: "),
	REFRESH_ERROR("Failed to retrieve Refresh Token"),
	UNAUTH_ERROR("Received 401 during bills call, refreshing tokens now"),
	RETRIEVE_BILL_ERROR("Failed to retrieve bills"),
	RETRIEVE_BILLPAYMENT_ERROR("Failed to retrieve bill payments"),
	UNABLE_PAYMENT_ERROR("Failed to complete bill payment"),
	JSON_CONV_ERROR("Unable to convert into proper json string"),
	GENERIC_ERROR("Generic exception caught.");

	private final String desc;

	MessageEnum(String d) {
		desc = d;
	}
	public String desc() {
		return desc;
	}



}
