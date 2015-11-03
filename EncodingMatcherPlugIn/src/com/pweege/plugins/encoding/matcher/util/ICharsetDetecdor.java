package com.pweege.plugins.encoding.matcher.util;

import java.io.IOException;
import java.io.InputStream;

public interface ICharsetDetecdor {
	
	/**
	 * @param content
	 * 
	 * @return The Detected Charset or null is cannot be detected
	 */
	public String detectCharset(InputStream content) throws IOException;

	
	/**
	 * @param filePath
	 * 
	 * @return The Detected Charset or null is cannot be detected
	 */
	public String detectCharset(String filePath) throws IOException;
	
	
}
