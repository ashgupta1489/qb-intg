package org.sample.qbintg.intg.model;

import static org.sample.qbintg.intg.util.Utilities.convertDateToString;
import static org.sample.qbintg.intg.util.Utilities.toCurrency;

import java.io.Serializable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.intuit.ipp.data.Bill;

public class BillModelView implements Serializable {

	private static final long serialVersionUID  =1L;
	private String txnDate;
	private String payee;
	private String category;
	private String dueDate;
	private String balance;
	private String total;
	private String txnNo;

	private static final Logger logger = Logger.getLogger(BillModelView.class);


	public BillModelView(Bill bill){
		if(bill==null){
			throw new IllegalArgumentException("Wrong or null input");
		}
		this.txnDate = convertDateToString(bill.getTxnDate());
		this.dueDate= convertDateToString(bill.getDueDate());
		this.balance = toCurrency(bill.getBalance(),bill.getCurrencyRef().getValue());
		this.total = toCurrency(bill.getTotalAmt(),bill.getCurrencyRef().getValue());
		this.txnNo = bill.getId();
		this.payee= bill.getVendorRef().getName();
		String category= "";
		if(bill.getLine()!=null  && bill.getLine().size()>0){
			if(bill.getLine().size()>1){
				category = "Multiple";
			}
			else{
				if(CollectionUtils.isNotEmpty(bill.getLine()) && bill.getLine().get(0).getAccountBasedExpenseLineDetail()!=null 
						&& bill.getLine().get(0).getAccountBasedExpenseLineDetail().getAccountRef()!=null
						){
					category = bill.getLine().get(0).getAccountBasedExpenseLineDetail().getAccountRef().getName();
				}
			}
		}
		if(StringUtils.isEmpty(category)){
			category = "N/A";
		}
		this.category = category;

	}

	public String getTxnDate() {
		return txnDate;
	}

	public static Logger getLogger() {
		return logger;
	}

	public String getPayee() {
		return payee;
	}

	public String getCategory() {
		return category;
	}

	public String getDueDate() {
		return dueDate;
	}

	public String getBalance() {
		return balance;
	}

	public String getTotal() {
		return total;
	}

	public String getTxnNo() {
		return txnNo;
	}

	@Override
	public String toString() {
		return "BillModelView [date=" + txnDate + ", payee=" + payee + ", category=" + category + ", dueDate=" + dueDate
				+ ", balance=" + balance + ", total=" + total + ", txnNo=" + txnNo + "]";
	}


}
