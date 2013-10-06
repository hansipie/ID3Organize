package net.chrysalide.test;

import org.apache.log4j.*;

public class TestBasicConfigurator {
	// static Category cat =
	// Category.getInstance(TestBasicConfigurator.class.getName()) ;

	static Logger logger = Logger.getLogger(TestBasicConfigurator.class);
	
	public static void main(String[] args) {
		logger.info("Mon message");
	}
}
