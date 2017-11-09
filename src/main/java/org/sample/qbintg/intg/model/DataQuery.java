package org.sample.qbintg.intg.model;

public class DataQuery {

	public static final String OUTSTANDING_BILL_QUERY = "select * from bill where balance != '0' order by id desc";
	public static final String BILLPAYMENT_QUERY = "select * from billpayment order by id desc";
	public static final String VENDOR_QUERY = "select * from vendor order by id desc";
}
