package com.travelzen.etermface.service.nlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeLimitParser {
	
	private static Logger logger = LoggerFactory.getLogger(TimeLimitLexer.class);
	
	String  str; 
	
	public TimeLimitParser(String str) {
		super();
		
		this.str  = str ;
	}
	
	public TimeLimitTokenRepo parse(){
		
		TimeLimitLexer lexer  = new TimeLimitLexer() ;
		TimeLimitTokenRepo repo = lexer.lex(str) ;
		//logger.debug(repo.toString());
		lexer.parse(repo);
		
		return repo;
	}
	

	

}
