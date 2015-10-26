package com.pweege.plugins.encoding.matcher.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.springframework.util.AntPathMatcher;

import com.pweege.plugins.encoding.matcher.ativator.Activator;

public class PreferencesPathMatcher {

	private final String[] includesPattern;
	private final String[] excludesPattern;
	private final AntPathMatcher pathMatcher;

	public PreferencesPathMatcher() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String[] includes = new String[] { "/**/*.*" };;
		String[] excludes = new String[0];
		if (preferenceStore != null) {
			
			String includesStr = preferenceStore.getString("includesFileListEditor");
			if (!StringUtils.isBlank(includesStr)) {
				includes = StringUtils.split(includesStr, ";");
			}
			
			String excludesStr = preferenceStore.getString("excludesFileListEditor");
			if (!StringUtils.isBlank(excludesStr)) {
				excludes = StringUtils.split(excludesStr, ";");
			}
		}
		
		this.includesPattern = includes;
		this.excludesPattern = excludes;
		this.pathMatcher = new AntPathMatcher();
		this.pathMatcher.setCachePatterns(true);
	}
	
	public boolean matchesPreferences(IResource resource) {
		
		boolean matches = false;
		
		for (String incPattern : includesPattern) {
			matches = matches || pathMatcher.match(incPattern, resource.getFullPath().toString());
			if(matches) {
				for (String excludePattern : excludesPattern) {
					matches = matches &&  !pathMatcher.match(excludePattern, resource.getFullPath().toString());
					if(!matches) return false;
				}
				break;
			}
		}
		return matches;
	}
	
	
	

}
