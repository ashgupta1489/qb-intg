package org.sample.qbintg.intg.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class MakePaymentModalView  implements Serializable{

	private static final long serialVersionUID  =1L;
	private String paymentId;
	private String note;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyy")
	private Date paymentDate;
	private String billId;
	
	@JsonIgnore
	private BigDecimal paymentAmount;
	@JsonIgnore
	private String vendorId;
	
	
	
	
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	
	
	@Override
	public String toString() {
		return "MakePaymentModalView [paymentId=" + paymentId + ", note=" + note + ", paymentDate=" + paymentDate
				+ ", billId=" + billId + ", paymentAmount=" + paymentAmount + ", vendorId=" + vendorId+"]";
	}
	
	
	
	
	
}
