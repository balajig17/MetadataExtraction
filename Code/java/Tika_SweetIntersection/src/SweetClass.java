import java.io.Serializable;
import java.util.ArrayList;


public class SweetClass implements Comparable,Serializable
{

	private String name="";
	private ArrayList<String> SubClassesOf;
	private String equivalentClass="";
	private String comment = "";
	private ArrayList<String> restrictions;
	private Integer occurence=0;
	
	
	public Integer getOccured()
	{
		return occurence;
	}
	public void setOccured(Integer occured)
	{
		this.occurence = occured;
	}
	
	
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
		
		SweetClass othr = (SweetClass)o;
		
		int compared=othr.occurence.compareTo(this.occurence);
		
		return compared;
	}
	public ArrayList<String> getRestrictions() {
		return restrictions;
	}
	public void setRestrictions(ArrayList<String> restrictions) {
		this.restrictions = restrictions;
	}
	public String prettyPrint()
	{
		String str = "";
		StringBuilder sb = new StringBuilder();
		sb.append( "word name="+ this.name);
		if(this.equivalentClass!=null && !this.equivalentClass.equals(""))
		{
			sb.append("&"+"equivalent="+this.equivalentClass);
		}
		
		if(!this.comment.equals("") && this.comment!=null)
		{
			sb.append("&comment="+this.comment);
		}
		if(this.restrictions.size()!=0 && restrictions.get(0)!="null")
		{
			sb.append("&restrictions=");
			String delim = "";
			for(String restriction :restrictions)
			{
				sb.append(delim+restriction);
				delim="&";
			}
		}
		str = sb.toString();
		return str;
	}

}
