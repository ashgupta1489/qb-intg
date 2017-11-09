package org.sample.qbintg.intg.util;

import static org.sample.qbintg.intg.model.MessageEnum.JSON_CONV_ERROR;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.sample.qbintg.intg.model.MessageEnum;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class Utilities {

	private static final Logger logger = Logger.getLogger(Utilities.class);
	public static final String convertDateToString(Date date){
		if(date==null){
			return "N/A";
		}
		try{
			return DateFormatUtils.format(date, "MM/dd/yyyy");
		}
		catch(Exception e){
			logger.error("Unable to parse date -->"+ date.toString());
			return "N/A";
		}

	}


	//Should be only used during mock development
	public static String convertToPhnNum(String phoneRawString) throws IllegalArgumentException {
		MessageFormat phoneMsgFmt=new MessageFormat("({0}) {1}-{2}");
		//grouping of 3-3-4
		String[] phoneNumArr={phoneRawString.substring(0, 3),
				phoneRawString.substring(3,6),
				phoneRawString.substring(6)};
		return phoneMsgFmt.format(phoneNumArr);
	}

	public static final String getResultJsonString(Object object){
		try{
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writeValueAsString(object);
			return jsonInString;
		}
		catch(Exception e){
			logger.error(JSON_CONV_ERROR.desc());
			return getMessage(JSON_CONV_ERROR);
		}
	}

	public static final String getMessage(MessageEnum message){
		if(message!=null){
			return new JSONObject().put("response",message.desc()).toString();
		}
		return "No Message";
	}


	public static final String toCurrency(BigDecimal value, String currency) {
		if (value == null) {
			return "N/A";
		}
		try{
			return NumberFormat.getCurrencyInstance(getLocaleFromCurrency(currency)).format(value);
		}
		catch(Exception e){
			logger.error("Unable to parse currency -->"+ value.toString());
			return "N/A";
		}
	}

	public static Locale getLocaleFromCurrency(String strCode) {

		for (final Locale locale : NumberFormat.getAvailableLocales()) {
			final String code = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
			if (strCode.equals(code)) {
				return locale;
			}
		}  
		return null;
	}

}
