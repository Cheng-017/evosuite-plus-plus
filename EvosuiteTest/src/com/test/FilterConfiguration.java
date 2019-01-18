package com.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class FilterConfiguration {
	private List<Filter> filters;
	
	public FilterConfiguration(String[] args) throws Exception {
		String exclFile = CommonUtility.getOptValue(args, ParameterOptions.EXCLUSIVE_FILE_OPT);
		String inclFile = CommonUtility.getOptValue(args, ParameterOptions.INCLUSIVE_FILE_OPT);
		filters = new ArrayList<FilterConfiguration.Filter>();
		if (StringUtils.isNotEmpty(exclFile)) {
			filters.add(new ExclusiveFilter(exclFile));
		}
		if (StringUtils.isNotEmpty(inclFile)) {
			filters.add(new InclusiveFilter(inclFile));
		}
	}

	public FilterConfiguration(String exclusivesFile, String inclusivesFile) throws IOException {
		
	}


	public boolean isValidProject(String projectName) {
		for (Filter filter : filters) {
			if (!filter.isValidProject(projectName)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isValidMethod(String projectName, String methodId) {
		for (Filter filter : filters) {
			if (!filter.isValidMethod(projectName, methodId)) {
				return false;
			}
		}
		return true;
	}
	
	private static interface Filter {

		boolean isValidProject(String projectName);

		boolean isValidMethod(String projectName, String methodId);
	}
	
	private static class ExclusiveFilter implements Filter {
		private Set<String> exclusives = new HashSet<>();
		
		public ExclusiveFilter(String exclusivesFile) throws IOException {
			File file = new File(exclusivesFile);
			if (!file.exists()) {
				return;
			}
			List<String> lines = FileUtils.readLines(file , Charset.defaultCharset());
			for (String line : lines) {
				if (!line.trim().isEmpty() && !line.startsWith("#")) {
					exclusives.add(line);
				}
			}
		}
		
		@Override
		public boolean isValidProject(String projectName) {
			return !exclusives.contains(projectName);
		}

		@Override
		public boolean isValidMethod(String projectName, String methodId) {
			return false;
		}

	}
	
	private static class InclusiveFilter implements Filter {
		private Map<String, Set<String>> inclusives = new HashMap<>();
		
		public InclusiveFilter(String inclFile) throws IOException {
			List<String> lines = FileUtils.readLines(new File(inclFile), Charset.defaultCharset());
			String curProject = null;
			for (String line : lines) {
				if (line.startsWith("#Project=")) {
					curProject = line.split("=")[1].split(" ")[0];
				} else {
					if (!line.startsWith("#")) {
						CollectionUtils.getSetInitIfEmpty(inclusives, curProject).add(line);
					}
				}
			}
		}

		@Override
		public boolean isValidProject(String projectName) {
			return inclusives.containsKey(projectName);
		}

		@Override
		public boolean isValidMethod(String projectName, String methodId) {
			return inclusives.get(projectName).contains(methodId);
		}
		
	}
}
