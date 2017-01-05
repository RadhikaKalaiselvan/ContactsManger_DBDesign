/*
 * Contact is a class with all the attributes and with getter and setter for the attributes. 
 */
public class Contact {
	int contact_id, phone_number;
	String fname,lname,email,birthday,sex,meeting_date;
	String line1,line2,state,city,zipcode,country;
    public Contact()
      {       
         this.fname = "";
         this.lname="";
         this.contact_id=0;
         this.email = "";
         this.phone_number= 0;
         this.birthday="";
         this.sex="";
         this.line1="";
         this.line2="";
         this.state="";
         this.country="";
         this.zipcode="";
         this.city="";
         this.meeting_date="";
        		 
      }
	public Contact(int contact_id, String fname,String lname,
			String email,int phone_number,String birthday,String sex,
			String line1,String line2,String city,String state,String country,String zipcode,String meeting_date)
	{
		this.contact_id=contact_id;
	    this.fname = fname;
        this.lname=lname;
        this.email =email;
        this.phone_number= phone_number;
        this.birthday=birthday;
        this.sex=sex;
        this.line1=line1;
        this.line2=line2;
        this.state=state;
        this.country=country;
        this.zipcode=zipcode;
        this.city=city;
        this.meeting_date=meeting_date;
	}
	public String getMeeting_date() {
		return meeting_date;
	}
	public void setMeeting_date(String meeting_date) {
		this.meeting_date = meeting_date;
	}
	public String getLine1() {
		return line1;
	}
	public void setLine1(String line1) {
		this.line1 = line1;
	}
	public String getLine2() {
		return line2;
	}
	public void setLine2(String line2) {
		this.line2 = line2;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public int getContact_id() {
		return contact_id;
	}
	public void setContact_id(int contact_id) {
		this.contact_id = contact_id;
	}
	public int getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(int phone_number) {
		this.phone_number = phone_number;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
 
	
	



}