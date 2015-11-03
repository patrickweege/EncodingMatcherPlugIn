package com.pweege.plugins.encoding.matcher.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mozilla.universalchardet.UniversalDetector;

public class JUniversalChardet_CharsetDetector implements ICharsetDetecdor {

	private final UniversalDetector detector;

	public JUniversalChardet_CharsetDetector() {
		// (1)
		this.detector = new UniversalDetector(null);
	}

	@Override
	public String detectCharset(String filePath) throws IOException {
		InputStream in = new FileInputStream(filePath);
		String detected = this.detectCharset(in);
		in.close();
		return detected;
	}

	@Override
	public String detectCharset(InputStream content) throws IOException {

		byte[] buf = new byte[1024];

		// (2)
		int nread;
		while ((nread = content.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		// (3)
		detector.dataEnd();

		// (4)
		String encoding = detector.getDetectedCharset();

		// (5)
		detector.reset();

		return encoding;
	}

}
