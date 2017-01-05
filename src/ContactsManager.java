/*
 * 
 * This class contains methods to add, delete, update and search contact from DB.
 * It contains methods to validate inputs from UI
 * and it fetches DBConnection from DBConnector class and performs the above mentioned operations.
 * Accepted input formats for Birthday is YYYY-MM-DD and phone number requires 10 digits on numbers only
 * and email id must contain @ symbol.
 * All the mandatory fields are marked by * symbol. 
 * Search operation fetches many records. To move through the contacts make use of Forward and back buttons.
 * Reset All button is used to clear all the available fields.
 * Update operation will also validate the inputs
 * Delete operation will delete the contact and clear all the fields.
 * 
 * 
 * 
 */

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
public class ContactsManager implements ActionListener {
 	String fname,lname,email,sex,birthday,meetingDate;
 	String line1,line2,country,state,zipcode,city;
 	ArrayList<Contact> contactsList;
 	JFrame cframe;
 	int contactnum;
 	private final static Logger LOGGER = Logger.getLogger(DBConnector.class.getName());
   	JLabel fnamejl,lnamejl,birthdayjl,sexjl,phonejl, emailjl,meetingDatejl;
   	JLabel line1jl,line2jl,countryjl,statejl,zipcodejl,cityjl;
   	JTextField fnametf,lnametf,birthdaytf,meetingDatetf,phonetf,emailtf,line1tf,line2tf,countrytf,statetf,zipcodetf,citytf,sextf;
   	JButton addb, deleteb, updateb, searchb,forwardb, backb, resetAllb;
   	JRadioButton rbtMale,rbtFemale;
   	ButtonGroup sexBgroup;
   	int phone;
   	Container cPane;
   	Connection dbConn;
   	DBConnector  dbConnector;
   	
	ContactsManager(){
		fname="";lname="";email="";sex="";birthday="";
		line1="";line2="";country="";state="";zipcode="";
		city="";
		meetingDate="";
		phone=-1;
		contactsList=new ArrayList<Contact>();
		createUI();
		dbConnector=new DBConnector();
		dbConn=dbConnector.getConnection();
	}
	public static void main(String[] args) {
new ContactsManager();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource () == addb){
            addContact();
       } else if (e.getSource () == searchb){
           searchContact();
       } else  if (e.getSource () == deleteb){
          deleteContact();
          resetAll();
       }else if (e.getSource () == updateb){
         updateContact();
       } else if (e.getSource() == forwardb){
        displayNextSearchContact(); 
       } else if (e.getSource() == backb){
       displayPreviousSearchContact();
       } else if (e.getSource() == resetAllb){
           resetAll();
           }
		
	}
	
	public void addContact(){  
		String fname = fnametf.getText();
        String lname = lnametf.getText();
        String line1 = line1tf.getText();
        String line2 = line2tf.getText();
        String birthday = birthdaytf.getText();
        String meetingDate= meetingDatetf.getText();
        String sex = getSexValueFromRadioButton();
        String zipcode = zipcodetf.getText();
        String country = countrytf.getText();
        String state = statetf.getText();
        String email = emailtf.getText();
        String errMsg=validateInput();
        if(!errMsg.equals("")){
        	JOptionPane.showMessageDialog(null,errMsg);
        	return;
        }
        int phonenum=Integer.parseInt(phonetf.getText()+""); 
	   	if(fname.equals("")){
	   		JOptionPane.showMessageDialog(null, "Please enter contact name.");
	   	} else{
	   		Contact contactObj=new Contact(0,fname,lname,email,phonenum,
					birthday,sex,
					line1,line2,city,state,country,zipcode,meetingDate);
           	  dbConnector.addContact(dbConn,contactObj);
		   	  JOptionPane.showMessageDialog(null, "Contact Saved");
	      }
     }

	
	  public void deleteContact(){
		  Contact contact = (Contact)contactsList.get(contactnum);
          int id = contact.getContact_id();
          LOGGER.info("ID  "+id);
          
		   	String fname = fnametf.getText();	
		   	if(fname.equals("") || id==0){
		   		JOptionPane.showMessageDialog(null,"Please enter first name and last name to delete.");
		   	}
		   	else{
		   		int contactsdeleted = dbConnector.deleteContact(dbConn,fname,id);
		   		JOptionPane.showMessageDialog(null, contactsdeleted + " Contact(s) deleted !");
		   	}
	     }
	
	  public String getSexValueFromRadioButton(){
		  String input="f";
		  if(rbtMale.isSelected()){
	            input="m";
	        } 
		  return input;
	  }
	public void updateContact(){
		String errMsg=validateInput();
		 if(!errMsg.equals("")){
	        	JOptionPane.showMessageDialog(null,errMsg);
	        	return;
	        }
		
		if (contactnum >= 0 && contactnum < contactsList.size())
        {
           Contact contact = (Contact)contactsList.get(contactnum);
           int id = contact.getContact_id();            
           String fname = fnametf.getText();
           String lname = lnametf.getText();
           String line1 = line1tf.getText();
           String line2 = line2tf.getText();
           String birthday = birthdaytf.getText();
           String meetingDate = meetingDatetf.getText();
           String sex = getSexValueFromRadioButton();
           if(!checkIfSexInputIsValid(sex)){
        	   JOptionPane.showMessageDialog(null, "Invalid Input for Sex. Accepts ( f or m )"); 
            return;
           }
           String zipcode = zipcodetf.getText();
           String country = countrytf.getText();
           String state = statetf.getText();
           String email = emailtf.getText();
           
           int phonenum=Integer.parseInt(phonetf.getText());
           
           Contact contactObj=new Contact(id,fname,lname,email,phonenum,
					birthday,sex,
					line1,line2,city,state,country,zipcode,meetingDate);
           	  dbConnector.updateContact(dbConn,contactObj);

  	   JOptionPane.showMessageDialog(null, "contact info record updated successfully.");         
        }
        else
        {   
             JOptionPane.showMessageDialog(null, "No record to Update");  
        }
		
	}
	
	public void searchContact() { 
	   String fname = fnametf.getText();
	   contactnum=0;
	   	if(fname.equals("")){
	   		JOptionPane.showMessageDialog(null,"Please enter contact first name to search.");
	   	}
	   	else{
	   		contactsList = dbConnector.searchContact(dbConn,fname);
	   		if(contactsList.size() == 0){
	   			JOptionPane.showMessageDialog(null, "No records found.");
	   		}
	   		else
	   		{
	   			Contact contact = (Contact) contactsList.get(contactnum);
	   			fnametf.setText(contact.getFname());
	   			lnametf.setText(contact.getLname());
	   			phonetf.setText(((Integer)contact.getPhone_number()).toString()); 
	   			line1tf.setText(contact.getLine1());
	   			line2tf.setText(contact.getLine2());
	   			citytf.setText(contact.getCity());
	   			statetf.setText(contact.getState());
	   			birthdaytf.setText(contact.getBirthday());
	   			meetingDatetf.setText(contact.getMeeting_date());
	   			emailtf.setText(contact.getEmail());
	   			countrytf.setText(contact.getCountry());
	   			zipcodetf.setText(contact.getZipcode());
	   			setSexValueInRadioButton(contact.getSex());
	   			
	   			
	   		 }
	   		}
	   	}

	public void displayPreviousSearchContact(){                  
	   	contactnum--;
	
	   	if(contactnum < 0 ){
	        JOptionPane.showMessageDialog(null, "You have reached begining " +
	        		"of search results"); 
	   		forwardb.setEnabled(true);
	   		backb.setEnabled(false);
              contactnum++;
	   	}else{
	   	    forwardb.setEnabled(true);
	   	    Contact contact = (Contact) contactsList.get(contactnum);
			fnametf.setText(contact.getFname());
			lnametf.setText(contact.getLname());
			phonetf.setText(((Integer)contact.getPhone_number()).toString()); 
			line1tf.setText(contact.getLine1());
			line2tf.setText(contact.getLine2());
			citytf.setText(contact.getCity());
			statetf.setText(contact.getState());
			birthdaytf.setText(contact.getBirthday());
			meetingDatetf.setText(contact.getMeeting_date());
			LOGGER.fine("sex "+contact.getSex());
			setSexValueInRadioButton(contact.getSex());
			emailtf.setText(contact.getEmail());
			countrytf.setText(contact.getCountry());
			zipcodetf.setText(contact.getZipcode());
	   	}

    }
	
	void createUI(){
		cframe = new JFrame("Contacts Manager");
        cPane = cframe.getContentPane();
   		cPane.setLayout(new GridBagLayout());
        cframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cframe.setSize(700,700);
   		addUIComponents();
   		cframe.setResizable(false);
   		cframe.setVisible(true);
   		
	}
	
	   public void displayNextSearchContact(){                  
		   	contactnum++;
		   	if(contactnum >= contactsList.size()){
		        JOptionPane.showMessageDialog(null, "You have reached end of " +
		        		"search results"); 
		   		forwardb.setEnabled(false);
		   		backb.setEnabled(true);
		       contactnum -- ; 
		   	}
		   	else{
		   		backb.setEnabled(true);
		   	    Contact contact = (Contact) contactsList.get(contactnum);
				fnametf.setText(contact.getFname());
				lnametf.setText(contact.getLname());
				phonetf.setText(((Integer)contact.getPhone_number()).toString()); 
				line1tf.setText(contact.getLine1());
				line2tf.setText(contact.getLine2());
				citytf.setText(contact.getCity());
				statetf.setText(contact.getState());
				birthdaytf.setText(contact.getBirthday());
				meetingDatetf.setText(contact.getMeeting_date());
				setSexValueInRadioButton(contact.getSex());
				emailtf.setText(contact.getEmail());
				countrytf.setText(contact.getCountry());
				zipcodetf.setText(contact.getZipcode());
		   	 }
		   	}
	
	   void setSexValueInRadioButton(String input){
		   if (input.equals("m")) {
		        rbtMale.setSelected(true); 
		    } else {
		        rbtFemale.setSelected(true); 
		    } 
	   }
	void addUIComponents() {
		
		addb   = new JButton("Save");
   		deleteb = new JButton("Delete");
        updateb = new JButton("Update");
   		searchb = new JButton("Search");
   		backb=new JButton("Back");
   		forwardb=new JButton("Forward");
   		resetAllb=new JButton("Reset All");
   		
   		
        rbtMale = new JRadioButton("Male");
        rbtFemale = new JRadioButton("Female");
        sexBgroup = new ButtonGroup();
        sexBgroup.add(rbtMale);
        sexBgroup.add(rbtFemale);
        
   		
   		addb.addActionListener(this);
   		deleteb.addActionListener(this);
   	    updateb.addActionListener(this);
   		searchb.addActionListener(this);
   		backb.addActionListener(this);
   		forwardb.addActionListener(this);
   	resetAllb.addActionListener(this);
   	
		fnamejl=new JLabel("First Name*");
		fnametf=new JTextField(20);
		lnamejl=new JLabel("Last Name*");
		lnametf=new JTextField(20);
		
		birthdayjl=new JLabel("Birthday*");
        birthdaytf=new JTextField(10);
        
        meetingDatejl=new JLabel("Last Met Date*");
        meetingDatetf=new JTextField(10);
        
		emailjl=new JLabel("Email*");
		emailtf=new JTextField(25);
		
		phonejl=new JLabel("Phone No.*");
		phonetf=new JTextField(20);
		
		sexjl=new JLabel("Sex*");
		
		
		line1jl=new JLabel("Address line1*");
		line1tf=new JTextField(20);
		line2jl=new JLabel("Address line2");
		line2tf=new JTextField(20);
		
		cityjl=new JLabel("City*");
		citytf=new JTextField(20);
		statejl=new JLabel("State*");
		statetf=new JTextField(20);
		
		zipcodejl=new JLabel("ZipCode*");
		zipcodetf=new JTextField(10);
		countryjl=new JLabel("Country*");
		countrytf=new JTextField(20);
		
		GridBagConstraints g1 = new GridBagConstraints();
        g1.gridx = 0;
        g1.gridy = 0;
      g1.insets = new Insets(5,5,5,5); 
        cPane.add(fnamejl, g1);
        
        GridBagConstraints g1tf = new GridBagConstraints();
        g1tf.gridx = 1;
      g1tf.insets = new Insets(5,5,5,5); 
        g1tf.gridy = 0;
       g1tf.gridwidth = 2;
       g1tf.fill = GridBagConstraints.BOTH;
        cPane.add(fnametf, g1tf);
        
        GridBagConstraints g3 = new GridBagConstraints();
        g3.gridx = 0;
       g3.insets = new Insets(5,5,5,5); 
        g3.gridy = 1;
        cPane.add(lnamejl, g3);
        
        GridBagConstraints g4 = new GridBagConstraints();
        g4.gridx = 1;
       g4.insets = new Insets(5,5,5,5); 
        g4.gridy = 1;
        g4.gridwidth = 2;
      g4.fill = GridBagConstraints.BOTH;
        cPane.add(lnametf, g4);
        
        GridBagConstraints g5 = new GridBagConstraints();
        g5.gridx = 0;
        g5.insets = new Insets(5,5,5,5); 
        g5.gridy = 2;
        cPane.add(phonejl, g5);
        
        GridBagConstraints g6 = new GridBagConstraints();
        g6.gridx = 1;
        g6.gridy = 2;
       g6.insets = new Insets(5,5,5,5); 
      g6.gridwidth = 2;
     g6.fill = GridBagConstraints.BOTH;
        cPane.add(phonetf, g6);
        
        GridBagConstraints g7 = new GridBagConstraints();
        g7.gridx = 0;
       g7.insets = new Insets(5,5,5,5); 
        g7.gridy = 3;
        cPane.add(emailjl, g7);
        
        GridBagConstraints g8 = new GridBagConstraints();
        g8.gridx = 1;
        g8.gridy = 3;
       g8.gridwidth = 2;
        g8.insets = new Insets(5,5,5,5); 
      g8.fill = GridBagConstraints.BOTH;
        cPane.add(emailtf, g8);
        
        GridBagConstraints g15 = new GridBagConstraints();
        g15.gridx = 0;
        g15.gridy = 4;
       g15.gridwidth = 2;
       g15.insets = new Insets(5,5,5,5); 
       g15.fill = GridBagConstraints.BOTH;
        cPane.add(birthdayjl, g15);
        
        GridBagConstraints g16 = new GridBagConstraints();
        g16.gridx = 1;
        g16.gridy = 4;
        g16.gridwidth = 2;
      g16.insets = new Insets(5,5,5,5); 
       g16.fill = GridBagConstraints.BOTH;
        cPane.add(birthdaytf, g16);
        
        GridBagConstraints g34 = new GridBagConstraints();
        g34.gridx = 0;
        g34.gridy = 5;
       g34.gridwidth = 2;
       g34.insets = new Insets(5,5,5,5); 
       g34.fill = GridBagConstraints.BOTH;
        cPane.add(meetingDatejl, g34);
        
        GridBagConstraints g35 = new GridBagConstraints();
        g35.gridx = 1;
        g35.gridy = 5;
        g35.gridwidth = 2;
      g35.insets = new Insets(5,5,5,5); 
       g35.fill = GridBagConstraints.BOTH;
        cPane.add(meetingDatetf, g35);
        
      GridBagConstraints g17 = new GridBagConstraints();
        g17.gridx = 0;
        g17.gridy = 6;
       g17.gridwidth = 2;
       g17.insets = new Insets(5,5,5,5); 
      g17.fill = GridBagConstraints.BOTH;
        cPane.add(sexjl, g17);
        
        
        GridBagConstraints g18 = new GridBagConstraints();
        g18.gridx = 1;
        g18.gridy = 6;
        g18.gridwidth = 2;
        g18.insets = new Insets(5,5,5,5);
        g18.fill = GridBagConstraints.CENTER;
        cPane.add(rbtFemale, g18);
        
        GridBagConstraints g33 = new GridBagConstraints();
        g33.gridx = 2;
        g33.gridy = 6;
        g33.gridwidth = 2;
        g33.insets = new Insets(5,5,5,5); 
        cPane.add(rbtMale, g33);
        
        
        GridBagConstraints g19 = new GridBagConstraints();
        g19.gridx = 0;
        g19.gridy = 7;
       g19.gridwidth = 2;
       g19.insets = new Insets(5,5,5,5); 
       g19.fill = GridBagConstraints.BOTH;
        cPane.add(line1jl, g19);
        
        
        GridBagConstraints g20 = new GridBagConstraints();
        g20.gridx = 1;
        g20.gridy = 7;
        g20.gridwidth = 2;
        g20.insets = new Insets(5,5,5,5); 
        g20.fill = GridBagConstraints.BOTH;
        cPane.add(line1tf, g20);
        
        GridBagConstraints g21 = new GridBagConstraints();
        g21.gridx = 0;
        g21.gridy = 8;
       g21.gridwidth = 2;
        g21.insets = new Insets(5,5,5,5); 
        g21.fill = GridBagConstraints.BOTH;
        cPane.add(line2jl, g21);
        
        GridBagConstraints g25 = new GridBagConstraints();
        g25.gridx = 1;
        g25.gridy = 8;
       g25.gridwidth = 2;
        g25.insets = new Insets(5,5,5,5); 
        g25.fill = GridBagConstraints.BOTH;
        cPane.add(line2tf, g25);
        
        
        GridBagConstraints g27 = new GridBagConstraints();
        g27.gridx = 0;
        g27.gridy = 9;
       g27.gridwidth = 2;
        g27.insets = new Insets(5,5,5,5); 
        g27.fill = GridBagConstraints.BOTH;
        cPane.add(cityjl, g27);
        
        GridBagConstraints g28 = new GridBagConstraints();
        g28.gridx = 1;
        g28.gridy = 9;
       g28.gridwidth = 2;
        g28.insets = new Insets(5,5,5,5); 
        g28.fill = GridBagConstraints.BOTH;
        cPane.add(citytf, g28);
        
        GridBagConstraints g26 = new GridBagConstraints();
        g26.gridx = 0;
        g26.gridy = 10;
       g26.gridwidth = 2;
        g26.insets = new Insets(5,5,5,5); 
        g26.fill = GridBagConstraints.BOTH;
        cPane.add(statejl, g26);
        
        GridBagConstraints g22 = new GridBagConstraints();
        g22.gridx = 1;
        g22.gridy = 10;
       g22.gridwidth = 2;
        g22.insets = new Insets(5,5,5,5); 
        g22.fill = GridBagConstraints.BOTH;
        cPane.add(statetf, g22);
        
        GridBagConstraints g23 = new GridBagConstraints();
        g23.gridx = 0;
        g23.gridy = 11;
       g23.gridwidth = 2;
        g23.insets = new Insets(5,5,5,5); 
        g23.fill = GridBagConstraints.BOTH;
        cPane.add(countryjl, g23);
        
        GridBagConstraints g24 = new GridBagConstraints();
        g24.gridx = 1;
        g24.gridy = 11;
       g24.gridwidth = 2;
        g24.insets = new Insets(5,5,5,5); 
        g24.fill = GridBagConstraints.BOTH;
        cPane.add(countrytf, g24);
        
        GridBagConstraints g29 = new GridBagConstraints();
        g29.gridx = 0;
        g29.gridy = 12;
       g29.gridwidth = 2;
        g29.insets = new Insets(5,5,5,5); 
        g29.fill = GridBagConstraints.BOTH;
        cPane.add(zipcodejl, g29);
        
        GridBagConstraints g30 = new GridBagConstraints();
        g30.gridx = 1;
        g30.gridy = 12;
       g30.gridwidth = 2;
        g30.insets = new Insets(5,5,5,5); 
        g30.fill = GridBagConstraints.BOTH;
        cPane.add(zipcodetf, g30);
        
        GridBagConstraints g9 = new GridBagConstraints();
        g9.gridx = 0;
        g9.gridy = 13;
        g9.insets = new Insets(5,5,5,5); 
        cPane.add(addb, g9);
        
        GridBagConstraints g10 = new GridBagConstraints();
        g10.gridx = 1;
        g10.gridy = 13;
        g10.insets = new Insets(5,5,5,5); 
        cPane.add(updateb, g10);
        
        GridBagConstraints g11 = new GridBagConstraints();
        g11.gridx = 2;
        g11.gridy = 13;
        g11.insets = new Insets(5,5,5,5); 
        cPane.add(deleteb, g11);
        
        GridBagConstraints g12 = new GridBagConstraints();
        g12.gridx = 0;
        g12.gridy = 14;
        g12.insets = new Insets(5,5,5,5);
        cPane.add(forwardb, g12);
        
        GridBagConstraints g13 = new GridBagConstraints();
        g13.gridx = 1;
        g13.gridy = 14;
        g13.insets = new Insets(5,5,5,5); 
        cPane.add(searchb, g13);
        
        GridBagConstraints g14 = new GridBagConstraints();
        g14.gridx = 2;
        g14.gridy = 14;
        g14.insets = new Insets(5,5,5,5); 
        cPane.add(backb, g14);
        
        GridBagConstraints g31 = new GridBagConstraints();
        g31.gridx = 3;
        g31.gridy = 14;
        g31.insets = new Insets(5,5,5,5); 
        cPane.add(resetAllb, g31);
	
	}
	
	public void resetAll() {
		fnametf.setText("");
		lnametf.setText("");
		phonetf.setText(""); 
			line1tf.setText("");
			line2tf.setText("");
			citytf.setText("");
			statetf.setText("");
			birthdaytf.setText("");
			meetingDatetf.setText("");
			emailtf.setText("");
			countrytf.setText("");
			zipcodetf.setText("");
		contactnum = -1;
		contactsList.clear();
		forwardb.setEnabled(true);
		backb.setEnabled(true); 
	}

	public boolean checkIfSexInputIsValid(String input){
		if(input.equals("f") || input.equals("m")){
			return true;
		}
		return false;
	}
	
	public String validateInput(){
		String fname = fnametf.getText();
        String lname = lnametf.getText();
        String line1 = line1tf.getText();
     
        String birthday = birthdaytf.getText();
        String meetingDate = meetingDatetf.getText();
        String zipcode = zipcodetf.getText();
        String country = countrytf.getText();
        String state = statetf.getText();
        String email = emailtf.getText();
        String num=phonetf.getText()+"";
        String errMsg="";
        
        if(fname.equals("") || lname.equals("") || line1.equals("") || birthday.equals("") || 
        		zipcode.equals("") || country.equals("") || 
        		state.equals("") || email.equals("") || meetingDate.equals("") ) {
        	
        	errMsg+="Please enter all the mandatory fields *\n"; 
        } 
        
        if(!email.contains("@")){
        	errMsg+="Please enter a valid email id \n";
        }
        
       

	Pattern phnumpattern = Pattern.compile("\\d{10}");
	Matcher pmatcher = phnumpattern.matcher(num); 
	 if (!pmatcher.matches()|| num.equals("") || num.length()!=10) {
		 errMsg+="Please enter a valid phone number\n";
	 }
	

	Pattern bdaypattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
	Matcher bdaymatcher = bdaypattern.matcher(birthday); 
	 if (!bdaymatcher.matches()) {
		 errMsg+="Please enter a valid birthday of format YYYY-MM-DD\n";
	 }
	 Matcher meetingmatcher = bdaypattern.matcher(meetingDate); 
	 if (!meetingmatcher.matches()) {
		 errMsg+="Please enter a valid last met date of format YYYY-MM-DD\n";
	 }
	 LOGGER.fine("After validation : "+errMsg);
	 return errMsg;

	}
	}


