package org.sample.qbintg.intg.model;

public enum MessageEnum {

	XERO_CONN_MESSAGE("Once you click on below link, this would take you to the Xero OAuth portal to authenticate and authorize you. Once authorized,"
						+ " the page would automatically refresh with connection status"),
	QUICKBOOK_CONN_MESSAGE("Once you click on below link, this would take you to the QuickBooks OAuth portal to authenticate and authorize you. Once authorized, "
					+"the page would automatically refresh with connection status."),
	CONNECTION_INCOMPLETE("No Company found. Check if connection to Quickbook is successful."),
	SETUP_ERROR("Error while handling setup :: "),
	BEARER_ERROR("Error while calling bearer token :: "),
	REFRESH_ERROR("Failed to retrieve Refresh Token"),
	UNAUTH_ERROR("Received 401 during bills call, refreshing tokens now"),
	NO_DATA_FOUND("There is no data available in the environment for this particular entity"),
	RETRIEVE_BILL_ERROR("Failed to retrieve bills"),
	RETRIEVE_VENDOR_ERROR("Failed to retrieve vendors"),
	SUCCESSFUL_DATA_RETRIEVEL("Recieved the data from QBO API"),
	RETRIEVE_BILLPAYMENT_ERROR("Failed to retrieve bill payments"),
	UNABLE_PAYMENT_ERROR("Failed to complete bill payment"),
	UNABLE_VENDOR_SAVE_ERROR("Failed to save/edit vendor information"),
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
