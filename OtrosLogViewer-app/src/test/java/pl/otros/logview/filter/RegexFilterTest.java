package pl.otros.logview.filter;

import static org.testng.AssertJUnit.assertFalse;
import org.testng.annotations.Test;
import pl.otros.logview.api.model.LogData;
import pl.otros.logview.api.model.LogDataBuilder;

public class RegexFilterTest {

	public RegexFilter createFilter(String text,boolean ignoreCase) {
		RegexFilter regexFilter = new RegexFilter();
		regexFilter.setEnable(true);
		regexFilter.setIgnoreCase(ignoreCase);
		regexFilter.setFilteringText(text);
		regexFilter.performPreFiltering();
		return regexFilter;
		
	}

	@Test
	public void testMessageNull(){
		assertFalse(createFilter("A", false).accept(new LogData(), 1));
	}
	
	@Test
	public void testMessageAcceptCaseSensitive(){
		assertFalse(createFilter("ala\\*a", false).accept(new LogDataBuilder().withMessage("ala ma kota").build(), 1));
	}
	
	@Test
	public void testMessageAcceptCaseInsensitive(){
		assertFalse(createFilter("ala\\*a", false).accept(new LogDataBuilder().withMessage("Ala ma kota").build(), 1));
	}
	
	@Test
	public void testMessageRejectCaseSensitive(){
		assertFalse(createFilter("ala\\*a", false).accept(new LogDataBuilder().withMessage("Ala ma kota").build(), 1));
	}
	
	@Test
	public void testMessageRejectCaseInsensitive(){
		assertFalse(createFilter("ala\\*a", false).accept(new LogDataBuilder().withMessage("Kot ma ale").build(), 1));
	}
	
}
