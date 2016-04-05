package org.apache.tika.parser.sweet;

public class Pair implements Comparable
{
	public Integer occurence;
	public String name;
	
	
	public Pair()
	{
		
	}
	
	public Pair(String name ,Integer occurence)
	{
		this.name=name;
		this.occurence=occurence;
	}
	@Override
	public int compareTo(Object o) {
		
		Pair othr = (Pair) o;
		return othr.occurence.compareTo(this.occurence);
	}
}
