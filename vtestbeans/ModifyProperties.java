package vtestbeans;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyProperties {
	public static void  modify(LinkedHashMap<String, String> properties,String cp) {
		String regex="CP[1-9]{1}:(.*{1,})";
		String regex2="CP[1-9]{1}(.*){1,}:(.*{1,})";
		Pattern pattern=Pattern.compile(regex);
		Pattern pattern2=Pattern.compile(regex2);
		Set<String> propertieSet=properties.keySet();
		for (String propertie : propertieSet) {
			if (propertie.startsWith("@")) {
				String valueSum=properties.get(propertie);
				String[] values=valueSum.split(";");
				String finValue=properties.get(propertie);
				for (String  value: values) {
					Matcher matcher=pattern.matcher(value);
					Matcher matcher2=pattern2.matcher(value);
					if (matcher.find()) {
						if (value.contains(cp)) {
							finValue=matcher.group(1);
						}
					}
					if (matcher2.find()) {
						if (value.contains(cp)) {
							finValue=matcher.group(1);
						}
					}
				}
				properties.put(propertie, finValue);
			}
		}
	}
}
