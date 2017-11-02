package org.sample.qbintg.intg.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.sample.qbintg.intg.util.Utilities;

import com.intuit.ipp.data.BillPayment;
import com.intuit.ipp.data.Line;
import com.intuit.ipp.data.LinkedTxn;

public class BillPaymentModelView implements Serializable{

	private static final long serialVersionUID  =1L;
	private String billPaymentId;
	private String vendorName;
	private String txnDate;
	private String paymentType;
	private String amountPaid;
	private String billTxnId;

	private Predicate<LinkedTxn> nonNullPredicate = Objects::nonNull;
	private Predicate<LinkedTxn> typeNotNull = p -> p.getTxnType() != null;
	private Predicate<LinkedTxn> billType = p -> p.getTxnType().equals("Bill");

	public BillPaymentModelView(BillPayment billPayment){
		if(billPayment==null){
			throw new IllegalArgumentException("Wrong or null input");
		}
		this.paymentType= billPayment.getPayType().value();
		this.billPaymentId = billPayment.getId();
		this.vendorName=billPayment.getVendorRef().getName();
		this.txnDate = Utilities.convertDateToString(billPayment.getTxnDate());
		this.amountPaid = Utilities.toCurrency(billPayment.getTotalAmt(), billPayment.getCurrencyRef().getValue());
		Predicate<LinkedTxn> fullPredicate = nonNullPredicate.and(typeNotNull).and(billType);
		StringBuffer billTransId = new StringBuffer();
		for ( Line line : billPayment.getLine()) {
			billTransId.append(line.getLinkedTxn().stream().filter(fullPredicate).
					map(s -> s.getTxnId()).collect(Collectors.joining(",")));
		}
		this.billTxnId = billTransId.toString();
	}

	
	
	public String getBillPaymentId() {
		return billPaymentId;
	}

	public String getPaymentType() {
		return paymentType;
	}
	public String getVendorName() {
		return vendorName;
	}
	public String getAmountPaid() {
		return amountPaid;
	}
	public String getTxnDate() {
		return txnDate;
	}
	public String getBillTxnId() {
		return billTxnId;
	}

}
