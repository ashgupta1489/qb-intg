package org.sample.qbintg.intg.model;

import static org.sample.qbintg.intg.model.MessageEnum.*;
import static org.sample.qbintg.intg.model.DataQuery.*;

public enum DataType {

	BILL(SUCCESSFUL_DATA_RETRIEVEL,RETRIEVE_BILL_ERROR,OUTSTANDING_BILL_QUERY),
	BILL_PAYMENT(SUCCESSFUL_DATA_RETRIEVEL,RETRIEVE_BILLPAYMENT_ERROR,BILLPAYMENT_QUERY),
	VENDOR(SUCCESSFUL_DATA_RETRIEVEL,RETRIEVE_VENDOR_ERROR,VENDOR_QUERY);

	private final MessageEnum success;
	private final MessageEnum failure;
	private final String sqlQuery;
	
	public MessageEnum getFailureMessage(){
		return failure;
	}
	
	public MessageEnum getSuccessMessage(){
		return success;
	}
	
	public String getSql(){
		return sqlQuery;
	}
	
	private DataType(MessageEnum success, MessageEnum failure, String sql){
		this.success = success;
		this.failure = failure;
		this.sqlQuery = sql;
	}
}

