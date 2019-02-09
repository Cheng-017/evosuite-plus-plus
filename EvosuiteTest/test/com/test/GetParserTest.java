package com.test;

import java.io.File;
import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

public class GetParserTest extends AbstractETest{
	
	@Test
	public void test() {
		Class<?> clazz = com.momed.parser.MParserFactory.class;
		String methodName = "getParser";
		int parameterNum = 0;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = getTragetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes" + File.pathSeparator +"lib/imsmart.jar";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000;

//		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 200;
		GetParserTest t = new GetParserTest();
		t.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}

	

}