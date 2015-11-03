package test.com.pweege.plugins.encoding.matcher;

import com.pweege.plugins.encoding.matcher.util.ICharsetDetecdor;
import com.pweege.plugins.encoding.matcher.util.JUniversalChardet_CharsetDetector;

public class TestDetector {
	public static void main(String[] args) throws java.io.IOException {
		byte[] buf = new byte[1024];

		ICharsetDetecdor detector = new JUniversalChardet_CharsetDetector();

		System.out.println(detector.detectCharset(
				"/Users/patrickweege/Documents/development/GitRepos/EncodingMatcherPlugIn-Repo/EncodingMatcherPlugIn/src/test/com/pweege/plugins/encoding/matcher/TestMain.java"));

		System.out.println(detector.detectCharset(
				"/Users/patrickweege/Documents/development/GitRepos/EncodingMatcherPlugIn-Repo/EncodingMatcherPlugIn/src/test/com/pweege/plugins/encoding/matcher/TestMain2.java"));

		System.out.println(detector.detectCharset(
				"/Users/patrickweege/Documents/development/eclipse-workspaces/TesteISO/ISOTeste/src/ISOFile.java"));

	}
}