package main.java.com;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Extracter 
{
	public static String extractName(String longName)
	{
		String pattern =".*#(.*)>";
		Pattern rgx = Pattern.compile(pattern);
		Matcher m = rgx.matcher(longName);
		if(m.find())
		{
			return m.group(1);
		}
		else
		{
			pattern = ".*/(.*)>";
			rgx = Pattern.compile(pattern);
			m = rgx.matcher(longName);
			if(m.find())
			{
				return m.group(1);
			}
		}
		return null;
		
	}
	public static String extractEquivalenceClass(String classname,String equivInfo)
	{
		String pattern =".*?#(.*?)>";
		Pattern rgx = Pattern.compile(pattern);
		Matcher m = rgx.matcher(equivInfo);
		while(m.find())
		{
			String possibleEquiv = m.group(1);
			if(!classname.equals(possibleEquiv))
			{
				return possibleEquiv;
			}
		}
		return null;
	}
	public static String extractSubClass(String classname,String subClassInfo)
	{
		String pattern =".*?#(.*?)>";
		Pattern rgx = Pattern.compile(pattern);
		Matcher m = rgx.matcher(subClassInfo);
		while(m.find())
		{
			String possibleSubClass = m.group(1);
			if(!classname.equals(possibleSubClass))
			{
				return possibleSubClass;
			}
		}
		return null;
	}
	public static String extractRestrictions(String restrictionInfo)
	{
		String pattern = ".*(?:ObjectHasValue|ObjectAllValuesFrom|ObjectSomeValuesFrom).*#(.*?)>.*#(.*)>";
		Pattern rgx = Pattern.compile(pattern);
		Matcher m = rgx.matcher(restrictionInfo);
		if(m.find())
		{
			return m.group(1) + " " + m.group(2);
			
		}
		return null;
	}
	
}
