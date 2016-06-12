package com.travelzen.etermface.service.abe_imitator.ticket.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ItineraryReceiptConfig {
    /**
     * 配置文件
     */
    private final static String CONFIG_FILE = "properties/itinerary-receipt.properties";

    private final static String RECEIPT_WIDTH = "receipt.width";
    private final static String RECEIPT_HEIGHT = "receipt.height";

    private final static String PASSENGER_NAME_X_LENGHT_SHORT = "passenger.name.x.length.short";
    private final static String PASSENGER_NAME_X_LENGHT_LANG = "passenger.name.x.length.lang";
    private final static String PASSENGER_NAME_Y = "passenger.name.y";

    private final static String CARDID_X = "cardId.x";
    private final static String CARDID_Y = "cardId.y";

    private final static String EI_X = "ei.x";
    private final static String EI_Y = "ei.y";

    private final static String PNR_X = "pnr.x";
    private final static String PNR_Y = "pnr.y";

    private final static String TICKET_FLIGTH_START_Y = "ticket.flight.start.y";
    private final static String TICKET_FLIGTH_START_Y_DISTANCE = "ticket.flight.start.y.distance";

    private final static String AIRPORT_X = "airport.x";
    private final static String CARRIER_X = "carrier.x";
    private final static String FLIGHT_NUMBER_X = "flight.number.x";
    private final static String CLASS_X = "class.x";
    private final static String DATE_X = "date.x";
    private final static String TIME_X = "time.x";
    private final static String FARE_BASIS_X = "fare.basis.x";
    private final static String NOTVALIDBEF_X = "notValidBef.x";
    private final static String NOTVALIDAFT_X = "notValidAft.x";
    private final static String ALLOW_X = "allow.x";

    private final static String FARE_X = "fare.x";
    private final static String FARE_Y = "fare.y";

    private final static String FUND_X = "fund.x";
    private final static String FUND_Y = "fund.y";

    private final static String FUEL_X = "fuel.x";
    private final static String FUEL_Y = "fuel.y";

    private final static String OTHER_TAX_X = "other.tax.x";
    private final static String OTHER_TAX_Y = "other.tax.y";

    private final static String TOTAL_X = "total.x";
    private final static String TOTAL_Y = "total.y";

    private final static String TICKETNO_X = "ticketNo.x";
    private final static String TICKETNO_Y = "ticketNo.y";

    private final static String CK_X = "ck.x";
    private final static String CK_Y = "ck.y";

    private final static String INFO_X = "info.x";
    private final static String INFO_Y = "info.y";

    private final static String INSURANCE_X = "insurance.x";
    private final static String INSURANCE_Y = "insurance.y";

    private final static String OFFICENUM_X = "officeNum.x";
    private final static String OFFICENUM_Y = "officeNum.y";

    private final static String AGENTNUM_X = "agentNum.x";
    private final static String AGENTNUM_Y = "agentNum.y";

    private final static String OFFICE_NAME_X = "office.name.x";
    private final static String OFFICE_NAME_Y = "office.name.y";

    private final static String ISSUE_DATE_X = "issue.date.x";
    private final static String ISSUE_DATE_Y = "issue.date.y";

    private static ItineraryReceiptConfig itineraryReceiptConfig;
    static {
        try {
            itineraryReceiptConfig = new ItineraryReceiptConfig();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
    private Configuration config;

    /**
     * 
     * @throws ConfigurationException
     */
    private ItineraryReceiptConfig() throws ConfigurationException {
        config = new PropertiesConfiguration(CONFIG_FILE);
    }

    /**
     * single object model
     * 
     * @return
     */
    public static ItineraryReceiptConfig getInstance() {
        return itineraryReceiptConfig;
    }

    private int getInegerByString(String rs) {
        try {
            if (rs == null) {
                throw new Exception("properties file wrong");
            } else {
                rs = rs.trim();
            }
            int i = Integer.parseInt(rs);
            return i;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return 0;
    }

    public int getReceiptWidth() {
        String rs = config.getString(RECEIPT_WIDTH);
        return getInegerByString(rs);
    }

    public int getReceiptHeight() {
        String rs = config.getString(RECEIPT_HEIGHT);
        return getInegerByString(rs);
    }

    public int getPassengerNameXLenghtShort() {
        String rs = config.getString(PASSENGER_NAME_X_LENGHT_SHORT);
        return getInegerByString(rs);
    }

    public int getPassengerNameXLenghtLang() {
        String rs = config.getString(PASSENGER_NAME_X_LENGHT_LANG);
        return getInegerByString(rs);
    }

    public int getPassengerNameY() {
        String rs = config.getString(PASSENGER_NAME_Y);
        return getInegerByString(rs);
    }

    public int getCardidX() {
        String rs = config.getString(CARDID_X);
        return getInegerByString(rs);
    }

    public int getCardidY() {
        String rs = config.getString(CARDID_Y);
        return getInegerByString(rs);
    }

    public int getEiX() {
        String rs = config.getString(EI_X);
        return getInegerByString(rs);
    }

    public int getEiY() {
        String rs = config.getString(EI_Y);
        return getInegerByString(rs);
    }

    public int getPnrX() {
        String rs = config.getString(PNR_X);
        return getInegerByString(rs);
    }

    public int getPnrY() {
        String rs = config.getString(PNR_Y);
        return getInegerByString(rs);
    }

    public int getTicketFligthStartY() {
        String rs = config.getString(TICKET_FLIGTH_START_Y);
        return getInegerByString(rs);
    }

    public int getTicketFligthStartYDistance() {
        String rs = config.getString(TICKET_FLIGTH_START_Y_DISTANCE);
        return getInegerByString(rs);
    }

    public int getAirportX() {
        String rs = config.getString(AIRPORT_X);
        return getInegerByString(rs);
    }

    public int getCarrierX() {
        String rs = config.getString(CARRIER_X);
        return getInegerByString(rs);
    }

    public int getFlightNumberX() {
        String rs = config.getString(FLIGHT_NUMBER_X);
        return getInegerByString(rs);
    }

    public int getClassX() {
        String rs = config.getString(CLASS_X);
        return getInegerByString(rs);
    }

    public int getDateX() {
        String rs = config.getString(DATE_X);
        return getInegerByString(rs);
    }

    public int getTimeX() {
        String rs = config.getString(TIME_X);
        return getInegerByString(rs);
    }

    public int getFareBasisX() {
        String rs = config.getString(FARE_BASIS_X);
        return getInegerByString(rs);
    }

    public int getNotvalidbefX() {
        String rs = config.getString(NOTVALIDBEF_X);
        return getInegerByString(rs);
    }

    public int getNotvalidaftX() {
        String rs = config.getString(NOTVALIDAFT_X);
        return getInegerByString(rs);
    }

    public int getAllowX() {
        String rs = config.getString(ALLOW_X);
        return getInegerByString(rs);
    }

    public int getFareX() {
        String rs = config.getString(FARE_X);
        return getInegerByString(rs);
    }

    public int getFareY() {
        String rs = config.getString(FARE_Y);
        return getInegerByString(rs);
    }

    public int getFundX() {
        String rs = config.getString(FUND_X);
        return getInegerByString(rs);
    }

    public int getFundY() {
        String rs = config.getString(FUND_Y);
        return getInegerByString(rs);
    }

    public int getFuelX() {
        String rs = config.getString(FUEL_X);
        return getInegerByString(rs);
    }

    public int getFuelY() {
        String rs = config.getString(FUEL_Y);
        return getInegerByString(rs);
    }

    public int getOtherTaxX() {
        String rs = config.getString(OTHER_TAX_X);
        return getInegerByString(rs);
    }

    public int getOtherTaxY() {
        String rs = config.getString(OTHER_TAX_Y);
        return getInegerByString(rs);
    }

    public int getTotalX() {
        String rs = config.getString(TOTAL_X);
        return getInegerByString(rs);
    }

    public int getTotalY() {
        String rs = config.getString(TOTAL_Y);
        return getInegerByString(rs);
    }

    public int getTicketnoX() {
        String rs = config.getString(TICKETNO_X);
        return getInegerByString(rs);
    }

    public int getTicketnoY() {
        String rs = config.getString(TICKETNO_Y);
        return getInegerByString(rs);
    }

    public int getCkX() {
        String rs = config.getString(CK_X);
        return getInegerByString(rs);
    }

    public int getCkY() {
        String rs = config.getString(CK_Y);
        return getInegerByString(rs);
    }

    public int getInfoX() {
        String rs = config.getString(INFO_X);
        return getInegerByString(rs);
    }

    public int getInfoY() {
        String rs = config.getString(INFO_Y);
        return getInegerByString(rs);
    }

    public int getInsuranceX() {
        String rs = config.getString(INSURANCE_X);
        return getInegerByString(rs);
    }

    public int getInsuranceY() {
        String rs = config.getString(INSURANCE_Y);
        return getInegerByString(rs);
    }

    public int getOfficenumX() {
        String rs = config.getString(OFFICENUM_X);
        return getInegerByString(rs);
    }

    public int getOfficenumY() {
        String rs = config.getString(OFFICENUM_Y);
        return getInegerByString(rs);
    }

    public int getAgentnumX() {
        String rs = config.getString(AGENTNUM_X);
        return getInegerByString(rs);
    }

    public int getAgentnumY() {
        String rs = config.getString(AGENTNUM_Y);
        return getInegerByString(rs);
    }

    public int getOfficeNameX() {
        String rs = config.getString(OFFICE_NAME_X);
        return getInegerByString(rs);
    }

    public int getOfficeNameY() {
        String rs = config.getString(OFFICE_NAME_Y);
        return getInegerByString(rs);
    }

    public int getIssueDateX() {
        String rs = config.getString(ISSUE_DATE_X);
        return getInegerByString(rs);
    }

    public int getIssueDateY() {
        String rs = config.getString(ISSUE_DATE_Y);
        return getInegerByString(rs);
    }

}
