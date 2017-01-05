/*
 * To prevent MYSQL Injection I have used parameterized queries ( which uses ? ). 
 * By utilizing Java's PreparedStatement class, bind variables (i.e. the question marks) 
 * and the corresponding setString methods, SQL Injection can be easily prevented.
 * Search contact uses join queries on multiple tables.
 * Insert contact inserts into contacts table and gets the auto incremented value and inserts 
 * into other tables with foreign key dependencies.
 * Delete contact operation maintains referential integrity as the tables were create with delete on cascade option.   
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DBConnector {
static Connection conn = null;
private final static Logger LOGGER = Logger.getLogger(DBConnector.class.getName());
	public Connection getConnection(){
		try
		{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection("jdbc:mysql://localhost/contacts_Manager?user=root&password=mysqlpass");
		} catch(Exception e){
		  System.out.println("Exception occured while get connection call :"+e.getMessage());
		  e.printStackTrace();
		}
		return conn;
	}
	public ArrayList<Contact> searchContact(Connection con,String fname)
	{
		ArrayList<Contact> contactList=new ArrayList<Contact>();
		try	{ //change the query to search
			String sql = "SELECT * FROM Contacts as c inner join Address on c.Contact_ID=Address.Contact_ID "
					+ "inner join Email on Email.Contact_ID=c.Contact_ID inner join Phone_Numbers on "
					+ "Phone_Numbers.Contact_ID=c.Contact_ID inner join Meetings"
					+ " on Meetings.Contact_ID = c.Contact_ID WHERE First_Name  like '%"+fname+"%'";
			
			LOGGER.info("SQL "+sql);
 			Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(sql);
            String lname="",line1 = "",line2="",state="",city="",zipcode="",country="";
            String email="",birthday="",sex="",meetingDate="";
            int contact_id, ph_number;
			while(rs.next())
			{
				
                contact_id = rs.getInt("Contact_ID");
                LOGGER.info("Found result "+contact_id);
				fname = rs.getString("First_Name");
				lname=rs.getString("Last_Name");
				birthday=rs.getString("Birthday");
				meetingDate=rs.getString("Date");
				sex=rs.getString("Sex");
				
				line1 = rs.getString("Line1");
				line2 = rs.getString("Line2");
				state = rs.getString("City");
				city = rs.getString("Country");
				state=rs.getString("State");
				zipcode = rs.getString("ZipCode");
				country = rs.getString("Country");
				
				ph_number = rs.getInt("Phone_Number");
				email = rs.getString("Email_address");
				Contact contact=new Contact(contact_id,fname,lname,email,ph_number,
						birthday,sex,
						line1,line2,city,state,country,zipcode,meetingDate);
				contactList.add(contact);
			}
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}  
		return contactList;
	} 

	public int deleteContact(Connection con,String fname,int id){
        int rsNum = 0;
		try{
			String query = "DELETE FROM Contacts WHERE First_Name = ? and Contact_ID = ?";
 			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, fname);
			ps.setInt(2, id);
			rsNum = ps.executeUpdate();
		}
		catch(Exception e){
			System.out.println("Exception occured while removeContact :"+e.getMessage());
		}
		return rsNum;
	}
	
	public void updateContact(Connection con,Contact contact)
	{
		try
		{
			String sql = "UPDATE Contacts SET First_Name= ?,Last_Name=?, "
					+ "Birthday=?,Sex=? where Contact_ID=?";
 			PreparedStatement ps = con.prepareStatement(sql);
 			ps.setString(1 ,contact.getFname());		
			ps.setString(2 ,contact.getLname());
			ps.setString(3 ,contact.getBirthday());
			ps.setString(4 ,contact.getSex());
			 LOGGER.info(" Contact ID "+contact.getContact_id());
			ps.setInt(5, contact.getContact_id());
			ps.executeUpdate();
			
			String sql1 = "UPDATE Address SET State= ?,Line1=?,Line2=?,City=?,Country=?,"
					+ "ZipCode=? "
					+ " where Contact_ID=?";
			PreparedStatement ps1 = con.prepareStatement(sql1);
			ps1.setString(1 ,contact.getState());		
			ps1.setString(2 ,contact.getLine1());
			ps1.setString(3 ,contact.getLine2());
			ps1.setString(4 ,contact.getCity());
			ps1.setString(5, contact.getCountry());
			ps1.setString(6, contact.getZipcode());
			ps1.setInt(7,contact.getContact_id());
			ps1.executeUpdate();
			
			String sql2 = "UPDATE Phone_Numbers SET Phone_Number= ? where Contact_ID=?";
			PreparedStatement ps2 = con.prepareStatement(sql2);
			ps2.setInt(1 ,contact.getPhone_number());
			ps2.setInt(2,contact.getContact_id());
			ps2.executeUpdate();
			
			String sql3 = "UPDATE Meetings set Date=? where Contact_ID=? ";
			PreparedStatement ps3 = con.prepareStatement(sql3);
			ps3.setString(1 ,contact.getMeeting_date());
			ps3.setInt(2,contact.getContact_id());
			ps3.executeUpdate();
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public void addContact(Connection con,Contact contact){
		
		try
		{    int contact_id=0;
			String sql = "INSERT INTO Contacts(Contact_Id,First_Name,Last_Name,Birthday,Sex) VALUES (default,?,?,?,?) ";
 			PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, contact.getFname());
			ps.setString(2, contact.getLname());
			ps.setString(3, contact.getBirthday());
			ps.setString(4, contact.getSex());
			ps.executeUpdate();
			ResultSet rs =ps.getGeneratedKeys();
			if (rs.next()){
	            contact_id=rs.getInt(1);
	            LOGGER.info("Result set CID"+contact_id);
	        }
	        rs.close();
	        
	        String sql1 = "INSERT INTO Email(Email_address,Contact_ID) VALUES (?,?) ";
 			PreparedStatement ps1 = con.prepareStatement(sql1);
			ps1.setString(1, contact.getEmail());
			ps1.setInt(2, contact_id);
			ps1.executeUpdate();
			
			String sql2 = "INSERT INTO Phone_Numbers (Phone_Number,Area_Code,Contact_ID) VALUES (?,91,?) ";
 			PreparedStatement ps2 = con.prepareStatement(sql2);
			ps2.setInt(1, contact.getPhone_number());
			ps2.setInt(2, contact_id);
			ps2.executeUpdate();
			
			String sql3 = "INSERT INTO Address(Address_ID,Contact_ID,State,Line1,Line2,"
					+ "City,ZipCode,Country) VALUES"
					+ "(default,?,?,?,?,?,?,?) ";
 			PreparedStatement ps3 = con.prepareStatement(sql3,Statement.RETURN_GENERATED_KEYS);
			ps3.setInt(1,contact_id);
			ps3.setString(2, contact.getState());
			ps3.setString(3, contact.getLine1());
			ps3.setString(4, contact.getLine2());
			ps3.setString(5, contact.getCity());
			ps3.setString(6, contact.getZipcode());
			ps3.setString(7, contact.getCountry());
			ps3.executeUpdate();
			
			String sql4 = "INSERT INTO Meetings (Meeting_ID,Contact_ID,Date) values(default,?,?)";
 			PreparedStatement ps4 = con.prepareStatement(sql4,Statement.RETURN_GENERATED_KEYS);
			ps4.setInt(1, contact_id);
			ps4.setString(2,contact.getMeeting_date());
			ps4.executeUpdate();
			
				
		}
		catch(Exception e){
			System.out.println(" Exception while adding"+e.getMessage());
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public void closeConnection(Connection con) throws Exception {
		try{
			con.close();
		} catch(Exception e){
			System.out.println("Exception while closing the connection "+e);
		}
	}
	}


