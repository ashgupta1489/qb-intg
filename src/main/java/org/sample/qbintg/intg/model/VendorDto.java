package org.sample.qbintg.intg.model;

import static org.sample.qbintg.intg.util.Utilities.toCurrency;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intuit.ipp.data.Vendor;

@Entity
@Table(name="pnc_vendor")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value={"balance","relatedVendorId","createdTimestamp","updatedTimestamp"})
public class VendorDto implements Serializable{

	private static final long serialVersionUID  =1L;

	@Transient
	private String phnNum;
	
	@Transient
	private String primaryEmail;
	
	@Transient
	private String companyName;
	
	@NotBlank
	@Transient
	private  String displayName;
	
	
	@Column(name = "account_num",length=17)
	private String accountNumber;
	
	@Column(name = "routing_num",length=10)
	private String routingNumber;

	@Column(name = "related_vendor_id", nullable = false)
	private String vendorId;

	@Transient
	private  String balance;
	//private String status;
	
	@Column(name = "created_date", updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
	private Date createdTimestamp;
	
	
	@Column(name = "updated_date")
	@Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
	private Date updatedTimestamp;
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "vendor_id", updatable = false, nullable = false)
	private int id;


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public VendorDto(){}


	public VendorDto(Vendor vendor){
		this.vendorId= vendor.getId();
		this.balance = toCurrency(vendor.getBalance(),vendor.getCurrencyRef().getValue());
		this.displayName = vendor.getDisplayName();
		this.companyName = vendor.getCompanyName();
	//	this.status = vendor.isActive()?"Active":"Inactive";
		this.primaryEmail = vendor.getPrimaryEmailAddr()!=null?vendor.getPrimaryEmailAddr().getAddress():"N/A";
		this.phnNum = vendor.getPrimaryPhone()!=null?vendor.getPrimaryPhone().getFreeFormNumber():"N/A";
	}

	@JsonCreator
	public VendorDto(@JsonProperty("displayName") String displayName, @JsonProperty("companyName") String companyName, @JsonProperty("primaryEmail") String primaryEmail,  
				@JsonProperty("phnNum") String phnNum, @JsonProperty("accountNumber")  String accountNumber, @JsonProperty("routingNumber")  String routingNumber, @JsonProperty("vendorId")  String vendorId ){
		this.displayName = displayName;
		this.companyName = companyName;
		this.primaryEmail = primaryEmail;
		this.phnNum = phnNum;
		this.accountNumber=accountNumber;
		this.routingNumber=routingNumber;
		this.vendorId= vendorId;
	}

	@Override
	public String toString() {
		return "VendorDto [balance=" + balance + ", phnNum=" + phnNum + ", primaryEmail=" + primaryEmail
				+ ", companyName=" + companyName + ", displayName=" + displayName 
				+ ", accountNumber=" + accountNumber + ", routingNumber=" + routingNumber + ", vendorId=" + vendorId
				+ "]";
	}


	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorId() {
		return vendorId;
	}


	public String getDisplayName() {
		return displayName;
	}


	public String getAccountNumber() {
		return accountNumber;
	}


	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	public String getRoutingNumber() {
		return routingNumber;
	}


	public void setRoutingNumber(String routingNumber) {
		this.routingNumber = routingNumber;
	}


	public String getBalance() {
		return balance;
	}


	public String getPhnNum() {
		return phnNum;
	}


	public String getPrimaryEmail() {
		return primaryEmail;
	}


	public String getCompanyName() {
		return companyName;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public Date getUpdatedTimestamp() {
		return updatedTimestamp;
	}

	public void setUpdatedTimestamp(Date updatedTimestamp) {
		this.updatedTimestamp = updatedTimestamp;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((vendorId == null) ? 0 : vendorId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VendorDto other = (VendorDto) obj;
		if (id != other.id)
			return false;
		if (vendorId == null) {
			if (other.vendorId != null)
				return false;
		} else if (!vendorId.equals(other.vendorId))
			return false;
		return true;
	}




}