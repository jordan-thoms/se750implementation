package swiki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Utility class used to extract information from WikiText
 */
public class ContentProcessor {
	private static final Logger LOG = Logger.getLogger(ContentProcessor.class);
	private static final Pattern RXREDIRECT = Pattern.compile("#REDIRECT \\[\\[(.*?)\\]\\]");
	private static final Pattern RXINFOBOX = Pattern.compile("\\{\\{\\s*Infobox", Pattern.MULTILINE
			+ Pattern.CASE_INSENSITIVE);
	private static final Pattern RXTYPE = Pattern.compile("\\{\\{\\s*Infobox(.*?)(\\||\\}\\})",
			Pattern.MULTILINE + Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
	private static final Pattern RXKEYVALUE = Pattern.compile("^\\|(.*?)=(.*?)$", Pattern.MULTILINE
			+ Pattern.DOTALL);
	private static final Pattern RXCOMMENTS = Pattern.compile("<!--(.*?)-->", Pattern.MULTILINE
			+ Pattern.DOTALL);
	private static final Pattern RXCLEAN = Pattern.compile("\\n|\\{|\\}");

	public static void processContent(PageInfo info, String text) {
		info.redirect = extractRedirect(text);
		
		List<String> ibs = extractInfobox(text);
		for (String ib : ibs) {
			String t = extractType(ib);
			Map<String, String> attrs = extractKeyValuePairs(ib);
			info.addType(t, attrs);
		}
	}

	private static String extractRedirect(String text) {
		Matcher m = RXREDIRECT.matcher(text);
		String redirect = null;
		if (m.find()) {
			redirect = m.group(1).trim();
			LOG.debug("Redirect found => " + redirect);
		}

		return redirect;
	}
	
	private static final List<String> extractInfobox(String text) {
		Matcher m = RXINFOBOX.matcher(text);

		List<String> infoboxes = new ArrayList<String>();
		while (m.find()) {
			// Using bracket counting to get accurate extraction
			int startIx = m.start();
			int ix = startIx + 2;
			int c = 1;

			while (true) {
				int oIx = text.indexOf("{{", ix);
				int cIx = text.indexOf("}}", ix);

				// Check in case the brackets are not well formed
				if (cIx == -1)
					break;

				if (oIx > cIx || oIx == -1) {
					c--;
					ix = cIx + 2;
					if (c == 0) {
						String ib = text.substring(startIx, cIx + 2);

						// Remove comments
						ib = RXCOMMENTS.matcher(ib).replaceAll("");

						infoboxes.add(ib);
						break;
					}
				} else {
					c++;
					ix = oIx + 2;
				}
			}
		}

		return infoboxes;
	}

	private static final String extractType(String infobox) {
		Matcher m = RXTYPE.matcher(infobox);
		m.find();
		String type = m.group(1);
		type = RXCLEAN.matcher(type).replaceAll("").trim();

		LOG.debug("Extracted type => " + type);
		return type;
	}

	private static final Map<String, String> extractKeyValuePairs(String infobox) {
		Matcher m = RXKEYVALUE.matcher(infobox);

		Map<String, String> map = new HashMap<String, String>();
		while (m.find()) {
			String key = m.group(1);
			key = RXCLEAN.matcher(key).replaceAll("").trim();
			String value = m.group(2);
			value = RXCLEAN.matcher(value).replaceAll("").trim();
			
			map.put(key, value);
		}

		LOG.debug("Number of extracted values => " + map.size());
		return map;
	}
}