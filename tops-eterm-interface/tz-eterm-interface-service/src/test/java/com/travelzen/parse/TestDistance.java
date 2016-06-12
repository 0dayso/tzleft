package com.travelzen.parse;


public class TestDistance {

	/**
	 * 找城市距离的起始位置
	 */
	private final static String description = "CTY  TPM   CUM";
	/**
	 * 获取城市距离
	 * @param rtStr
	 * @return
	 */
	private static String getDistance(String rtStr) {
		if (null == rtStr) {
			return null;
		}
		String[] lines = rtStr.replaceAll("\r", "\n").split("\n+");

		int index = 0;
		for (index = 0; index < lines.length - 1; index++) {
			if (lines[index].trim().startsWith(description)) {
				break;
			}
		}

		index = index + 2;
		if(index < lines.length){
			String[] strs = lines[index].split("\\s+");
			if(null != strs && strs.length >1 && strs[0].trim().length() == 3){
				String tpm = strs[1].trim();
				if(tpm.matches("\\d+")){
					return tpm;
				}else if(tpm.matches("\\*\\d+")){
					return tpm.substring(1);
				}
			}
		}
		return null;
	}
	

	public static void main(String[] args){
		String str = "FSM   SHA .YY. BJS  \r";
		str+="CTY  TPM   CUM   *****     DOMESTIC ITINERARY    ********    \r";
        str+="SHA                                                                          \r";
        str+="BJS  *676   676  \r";
        str+="RFSONLN/1E /EFEP_7/FCC=D/PAGE 1/1   ";
        
        System.out.println(getDistance(str));
	}
}
