package com.travelzen.etermface.service.fare

class NfdRuleOneTest {
    
    public static void main(String[] args) {
	
	NfdRuleOneParser parser =new NfdRuleOneParser();
	
	String str = '''***去程回程***                                                                  
提前预订: 最早 26天   最晚 20天                                                 
                                                                                
去程适用星期限制: MON TUE SAT                                                   
去程适用时刻范围:                                                               
2000H-2359H 0000H-0830H   '''
	
	str ='''去程/回程NFN:06//01 适用规定            乘机者                                 
预订规定             运价组合NFN:06//05 团队规定                               
支付/出票            退票规定            变更规定                               
扩充规定             其他规定NFN:06//11 全部规则NFN:06                        
                                                                                
***去程回程***                                                                  
提前预订: 最晚 7天                                                              
                                                                                
去程适用航班号范围: 5697-5697 9177-9177 9171-9171 5619-5619 5653-5653           
5659-5659 5623-5623 5627-5627 5667-5667 5607-5607                               
                                                                                
最早出票日期:      03MAR2014          最晚出票日期:      31MAR2014              
                                                                                
仅适用于散客销售                                                                
                                                                                
签注:                                                                           
不得退改签/SHA14093                                                             
不得退票                                                                        
不得签转                     '''
	
	parser.parse(str)
	
    }

}
