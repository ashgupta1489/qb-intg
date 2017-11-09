package org.sample.qbintg.intg.helper;

import static org.sample.qbintg.intg.model.MessageEnum.GENERIC_ERROR;
import static org.sample.qbintg.intg.util.Utilities.getMessage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sample.qbintg.intg.model.DataType;
import org.sample.qbintg.intg.model.MakePaymentModalView;
import org.sample.qbintg.intg.model.MessageEnum;
import org.sample.qbintg.intg.model.VendorDto;
import org.sample.qbintg.intg.util.Utilities;
import org.sample.qbintg.repository.IDataRepository;
import org.sample.qbintg.repository.VendorDataRepository;

import com.intuit.ipp.data.Bill;
import com.intuit.ipp.data.BillPayment;
import com.intuit.ipp.data.BillPaymentCheck;
import com.intuit.ipp.data.BillPaymentCreditCard;
import com.intuit.ipp.data.BillPaymentTypeEnum;
import com.intuit.ipp.data.CheckPayment;
import com.intuit.ipp.data.EmailAddress;
import com.intuit.ipp.data.Error;
import com.intuit.ipp.data.Line;
import com.intuit.ipp.data.LinkedTxn;
import com.intuit.ipp.data.PrintStatusEnum;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.TelephoneNumber;
import com.intuit.ipp.data.TxnTypeEnum;
import com.intuit.ipp.data.Vendor;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;

public class DataHelper {

	private static final Logger logger = Logger.getLogger(DataHelper.class);
	public static Vendor getVendor(DataService service,String vendorId) throws FMSException, ParseException {
		Vendor vendor = new Vendor();
		vendor.setId(vendorId);
		vendor= (Vendor) service.findById(vendor);
		return vendor;
	}

	
	
	/*
	 * move to factory
	 */
	public static String processResponseForType(QueryResult queryResult, DataType dataType, IDataRepository dataRepository) throws Exception{
		switch (dataType) {
		case BILL:
			return ResponseAdapter.processBillResponse(queryResult);
		case BILL_PAYMENT:
			return ResponseAdapter.processBillPaymentResponse(queryResult);
		case VENDOR:
				
			return ResponseAdapter.processVendorResponse(queryResult,dataRepository);
		default:
			return getMessage(GENERIC_ERROR);
		}
	}
	

	public static ReferenceType getVendorRef(Vendor vendor) {
		ReferenceType vendorRef = new ReferenceType();
		vendorRef.setName(vendor.getDisplayName());
		vendorRef.setValue(vendor.getId());
		return vendorRef;
	}
	public static  boolean createBillPayment(DataService dataService, MakePaymentModalView payment) {
		try {
			BillPayment account = getBillPaymentFields(dataService,payment);
			BillPayment savedBillPayment = dataService.add(account);
			logger.info("BillPayment created: " + savedBillPayment.getId());

		} catch (FMSException e) {
			List<Error> list = e.getErrorList();
			list.forEach(error -> logger.error(MessageEnum.UNABLE_PAYMENT_ERROR.desc() + error.getMessage()+ error.getDetail()));	
			return false;
		}
		catch (ParseException e) {
			logger.error(MessageEnum.UNABLE_PAYMENT_ERROR.desc(),e);
			return false;
		}
		return true;

	}

	public static BillPayment getBillPaymentFields(DataService service, MakePaymentModalView payment) throws FMSException, ParseException {
		// add bill payment with minimum mandatory fields
		Bill bill = new Bill();
		bill.setId(payment.getBillId());
		Bill billObj = service.findById(bill);
		payment.setPaymentAmount(billObj.getBalance());
		BillPayment billPayment = new BillPayment();

		//Vendor vendor = DataHelper.getVendor(service,payment.getVendorId());
		billPayment.setVendorRef(billObj.getVendorRef());


		billPayment.setTxnDate(payment.getPaymentDate());
		billPayment.setPrivateNote(payment.getNote());
		billPayment.setTotalAmt(payment.getPaymentAmount());

		Line line1 = new Line();
		line1.setAmount(payment.getPaymentAmount());
		List<LinkedTxn> linkedTxnList1 = new ArrayList<>();
		LinkedTxn linkedTxn1 = new LinkedTxn();
		/*	Bill bill = getBill(service);*/
		linkedTxn1.setTxnId(payment.getBillId());
		linkedTxn1.setTxnType(TxnTypeEnum.BILL.value());
		linkedTxnList1.add(linkedTxn1);
		line1.setLinkedTxn(linkedTxnList1);
		List<Line> lineList = new ArrayList<Line>();
		lineList.add(line1);
		billPayment.setLine(lineList);



		BillPaymentTypeEnum paymentType = BillPaymentTypeEnum.CHECK; //TODO harcoded
		if(paymentType.equals(BillPaymentTypeEnum.CHECK)){
			billPayment.setCheckPayment(getCheckData(service));
		}
		else{
			billPayment.setCreditCardPayment(getCreditCardData(service));
		}
		billPayment.setPayType(paymentType);
		return billPayment;
	}


	private static BillPaymentCreditCard getCreditCardData(DataService service){
		BillPaymentCreditCard billPaymentCreditCard = new BillPaymentCreditCard();
		ReferenceType ccReferenceType = new ReferenceType();
		ccReferenceType.setName("Mastercard");
		ccReferenceType.setValue("41");
		billPaymentCreditCard.setCCAccountRef(ccReferenceType);
		return billPaymentCreditCard;
	}


	private static CheckPayment getCheckPayment() throws FMSException {
		String uuid = RandomStringUtils.randomAlphanumeric(8);
		CheckPayment checkPayment = new CheckPayment();
		checkPayment.setAcctNum("AccNum" + uuid);
		checkPayment.setBankName("BankName" + uuid);
		checkPayment.setCheckNum("CheckNum" + uuid);
		checkPayment.setNameOnAcct("Name" + uuid);
		checkPayment.setStatus("Status" + uuid);
		return checkPayment;

	}

	private static BillPaymentCheck getCheckData(DataService service) throws FMSException{
		BillPaymentCheck billPaymentCheck = new BillPaymentCheck();
		billPaymentCheck.setPrintStatus(PrintStatusEnum.NEED_TO_PRINT);

		ReferenceType checkReferenceType = new ReferenceType();
		checkReferenceType.setName("Checking");
		checkReferenceType.setValue("35");
		billPaymentCheck.setBankAccountRef(checkReferenceType);
		billPaymentCheck.setCheckDetail(getCheckPayment());		
		//billPaymentCheck.setPayeeAddr(Address.getPhysicalAddress());
		return billPaymentCheck;
	}


	private  static EmailAddress getEmailAddress(String emailAddress) {
		EmailAddress emailAddr = new EmailAddress();
		//Validate email here. 
		emailAddr.setAddress(emailAddress);
		return emailAddr;
	}

	private  static TelephoneNumber getTelephoneNum(String phoneNum) {
		TelephoneNumber primaryNum = new TelephoneNumber();
		//Validate phnNum here. 
		primaryNum.setFreeFormNumber(Utilities.convertToPhnNum(phoneNum));
		primaryNum.setDefault(true);
		primaryNum.setTag("Business");
		return primaryNum;
	}

	

	public static boolean saveVendor(DataService dataService, VendorDataRepository vendorDataRepository, VendorDto vendorDto) throws FMSException, ParseException, IllegalArgumentException {
		if(vendorDto==null){
			throw new IllegalArgumentException("Vendor Object is null");
		}
		try{
			Vendor vendor = null;
			if(StringUtils.isNotBlank(vendorDto.getVendorId())){
				logger.info("Inside Vendor dto for EDIT for vendor Id :" + vendorDto.getVendorId());
				vendor = getVendor(dataService, vendorDto.getVendorId());
				if(vendor==null){
					throw new IllegalArgumentException("Unable to find vendor with given id "+ vendorDto.getVendorId());
				}
				vendor.setAcctNum(vendorDto.getAccountNumber());
				vendor.setNotes("Account Number :" + vendorDto.getAccountNumber() 
				+ " Routing Number :" + vendorDto.getRoutingNumber());
				dataService.update(vendor);
				VendorDto tempVendorDto = vendorDataRepository.findByVendorId(vendorDto.getVendorId());
				if(tempVendorDto!=null){
					tempVendorDto.setDisplayName(vendor.getDisplayName());
					tempVendorDto.setAccountNumber(vendorDto.getAccountNumber());
					tempVendorDto.setRoutingNumber(vendorDto.getRoutingNumber());
				}
				
				vendorDataRepository.saveAndFlush(tempVendorDto);
				logger.info("VendorDto created: " + vendorDto.getId() + " ::vendor name: " + vendorDto.getVendorId());

			}
			else{
				logger.info("inside vendor creation");
				vendor = new Vendor();
				// Mandatory Fields
				vendor.setDisplayName(vendorDto.getDisplayName());
				vendor.setCompanyName(vendorDto.getCompanyName());
				vendor.setTaxIdentifier("1111111");
				vendor.setPrimaryEmailAddr(getEmailAddress(vendorDto.getPrimaryEmail()));
				vendor.setPrimaryPhone(getTelephoneNum(vendorDto.getPhnNum()));
				vendor.setDomain("QBO");
				vendor.setAcctNum(vendorDto.getAccountNumber());
				vendor.setNotes("Account Number :" + vendorDto.getAccountNumber() 
				+ " Routing Number :" + vendorDto.getRoutingNumber());
				Vendor savedVendor = dataService.add(vendor);
				logger.info("Vendor created: " + savedVendor.getId() + " ::vendor name: " + savedVendor.getDisplayName());
				vendorDto.setVendorId(savedVendor.getId());
				vendorDto = vendorDataRepository.saveAndFlush(vendorDto);
				logger.info("VendorDto created: " + vendorDto.getId() + " ::vendor name: " + vendorDto.getVendorId());
			}
		}
		catch (FMSException e) {
			List<Error> list = e.getErrorList();
			list.forEach(error -> logger.error(MessageEnum.UNABLE_VENDOR_SAVE_ERROR.desc() + error.getMessage()+ error.getDetail()));	
			return false;
		}
		catch (ParseException e) {
			logger.error(MessageEnum.UNABLE_VENDOR_SAVE_ERROR.desc(),e);
			return false;
		}
		return true;

	}


}
