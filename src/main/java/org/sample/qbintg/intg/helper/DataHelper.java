package org.sample.qbintg.intg.helper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.sample.qbintg.intg.model.MakePaymentModalView;
import org.sample.qbintg.intg.model.MessageEnum;

import com.intuit.ipp.data.Bill;
import com.intuit.ipp.data.BillPayment;
import com.intuit.ipp.data.BillPaymentCheck;
import com.intuit.ipp.data.BillPaymentCreditCard;
import com.intuit.ipp.data.BillPaymentTypeEnum;
import com.intuit.ipp.data.CheckPayment;
import com.intuit.ipp.data.Error;
import com.intuit.ipp.data.Line;
import com.intuit.ipp.data.LinkedTxn;
import com.intuit.ipp.data.PrintStatusEnum;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.TxnTypeEnum;
import com.intuit.ipp.data.Vendor;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;

public class DataHelper {

	private static final Logger logger = Logger.getLogger(DataHelper.class);
	public static Vendor getVendor(DataService service,String vendorId) throws FMSException, ParseException {
		Vendor vendor = new Vendor();
		vendor.setId(vendorId);
		vendor= (Vendor) service.findById(vendor);
		return vendor;
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


}
