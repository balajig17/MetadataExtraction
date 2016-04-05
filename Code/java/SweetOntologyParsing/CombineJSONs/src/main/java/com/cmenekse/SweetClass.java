package main.java.com.cmenekse;
import java.util.ArrayList;


public class SweetClass implements Comparable
{

	private String name="";
	private ArrayList<String> SubClassesOf;
	private String equivalentClass="";
	private String comment = "";
	private ArrayList<String> restrictions;
	
	public SweetClass(String name)
	{
		this.name = name;
	    this.restrictions = new ArrayList<String>();
	    this.SubClassesOf = new ArrayList<String>();
	}
	public void appendRestriction(String restriction)
	{
		this.restrictions.add(restriction);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getSubClasses() {
		return SubClassesOf;
	}
	public void appendToSubClasses(String subClass) {
		this.SubClassesOf.add(subClass);
	}
	public String getEquivalentClass() {
		return equivalentClass;
	}
	public void setEquivalentClass(String equivalentClass) {
		this.equivalentClass = equivalentClass;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public void printInfo()
	{
		System.out.println("/////////////////////////////");
		System.out.println("NAME : " + this.name + " EQ: " + this.equivalentClass +" COMMENT: " + this.comment);
		System.out.print("SBC: ");
		for(String subClass : this.SubClassesOf)
		{
			System.out.print(subClass+",");
		}
		System.out.println("");
		System.out.println("RESTR: ");
		for(String restriction:this.restrictions)
		{
			System.out.print(restriction+" , ");
		}
		System.out.println("");
		
	}
	@Override
	public int compareTo(Object o) {
		
		SweetClass sweetClass = (SweetClass) o;
		int compared=-50;
		try
		{
			if(sweetClass.name==null || this.name==null)
			{
				
				if(this.name==null)
				{
					return 50;
				}
				else
				{
					return -50;
				}
			}
			//System.out.println(sweetClass.name);
			compared = this.name.compareTo(sweetClass.name);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return compared;
	}
	public ArrayList<String> getRestrictions() {
		return restrictions;
	}
	public void setRestrictions(ArrayList<String> restrictions) {
		this.restrictions = restrictions;
	}
}

