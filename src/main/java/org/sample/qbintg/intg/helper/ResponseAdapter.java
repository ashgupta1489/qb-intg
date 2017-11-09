package org.sample.qbintg.intg.helper;

import static org.sample.qbintg.intg.model.MessageEnum.GENERIC_ERROR;
import static org.sample.qbintg.intg.model.MessageEnum.NO_DATA_FOUND;
import static org.sample.qbintg.intg.model.MessageEnum.RETRIEVE_BILLPAYMENT_ERROR;
import static org.sample.qbintg.intg.model.MessageEnum.RETRIEVE_BILL_ERROR;
import static org.sample.qbintg.intg.model.MessageEnum.RETRIEVE_VENDOR_ERROR;
import static org.sample.qbintg.intg.util.Utilities.getMessage;
import static org.sample.qbintg.intg.util.Utilities.getResultJsonString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.sample.qbintg.intg.model.BillModelView;
import org.sample.qbintg.intg.model.BillPaymentModelView;
import org.sample.qbintg.intg.model.VendorDto;
import org.sample.qbintg.repository.IDataRepository;
import org.sample.qbintg.repository.VendorDataRepository;

import com.intuit.ipp.data.Bill;
import com.intuit.ipp.data.BillPayment;
import com.intuit.ipp.data.Vendor;
import com.intuit.ipp.services.QueryResult;

@SuppressWarnings("unchecked")
public class ResponseAdapter {

	private static final Logger logger = Logger.getLogger(ResponseAdapter.class);


	public static String processVendorResponse(QueryResult queryResult,IDataRepository dataRepository) {

		if (!queryResult.getEntities().isEmpty() && queryResult.getEntities().size() > 0) {
			try {
				List<Vendor> data = (List<Vendor>)queryResult.getEntities();
				logger.info("Total number of data: " + data.size());
				VendorDataRepository vendorDataRepository = (VendorDataRepository)dataRepository;
				List<VendorDto> vendorDtos =  vendorDataRepository.findAll();
				Map<String, VendorDto> vendorsById = null;
				if(CollectionUtils.isNotEmpty(vendorDtos)){
					vendorsById = vendorDtos.stream().collect(Collectors.toMap(VendorDto::getVendorId, Function.identity()));
				}
				List<VendorDto> views = new ArrayList<>();
				if(CollectionUtils.isNotEmpty(data)){
					for (Vendor vendor : data) {
						VendorDto vendorDto = new VendorDto(vendor);
						if(vendorsById!=null && vendorsById.containsKey(vendor.getId())){
							VendorDto tempVendorDto = vendorsById.get(vendor.getId());
							vendorDto.setId(tempVendorDto.getId());
							vendorDto.setAccountNumber(tempVendorDto.getAccountNumber());
							vendorDto.setRoutingNumber(tempVendorDto.getRoutingNumber());
						}
						views.add(vendorDto);
					}
					
				}
				else{
					return getMessage(RETRIEVE_VENDOR_ERROR);
				}
				return getResultJsonString(views);
			} catch (Exception e) {
				logger.error(GENERIC_ERROR.desc(), e);
				return getMessage(GENERIC_ERROR);
			}
		}
		return getMessage(NO_DATA_FOUND);
	}


	public static  String processBillResponse(QueryResult queryResult) {
		if (!queryResult.getEntities().isEmpty() && queryResult.getEntities().size() > 0) {
			try {
				List<Bill> data = (List<Bill>)queryResult.getEntities();
				logger.info("Total number of data: " + data.size());
				List<BillModelView> views = new ArrayList<>();
				if(CollectionUtils.isNotEmpty(data)){
					data.stream().forEach(record -> views.add(new BillModelView(record)));
				}
				else{
					return getMessage(RETRIEVE_BILL_ERROR);
				}
				return getResultJsonString(views);
			} catch (Exception e) {
				logger.error(GENERIC_ERROR.desc(), e);
				return getMessage(GENERIC_ERROR);
			}
		}
		return getMessage(NO_DATA_FOUND);
	}

	public static String processBillPaymentResponse(QueryResult queryResult) {
		if (!queryResult.getEntities().isEmpty() && queryResult.getEntities().size() > 0) {
			try {
				List<BillPayment> data = (List<BillPayment>)queryResult.getEntities();
				logger.info("Total number of data: " + data.size());
				List<BillPaymentModelView> views = new ArrayList<>();
				if(CollectionUtils.isNotEmpty(data)){
					data.stream().forEach(record -> views.add(new BillPaymentModelView(record)));
				}
				else{
					return getMessage(RETRIEVE_BILLPAYMENT_ERROR);
				}
				return getResultJsonString(views);
			} catch (Exception e) {
				logger.error(GENERIC_ERROR.desc(), e);
				return getMessage(GENERIC_ERROR);
			}
		}
		return getMessage(NO_DATA_FOUND);
	}

}
