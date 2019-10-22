
import java.sql.*;
public class Customer {//class which handles customer list
    private DBAccess DbObj;
    private int ContentSize;//holds the bytesize
    private String OutCustomerList = "";// Stores Customerlist 
    public String Address,City,Region,PostalCode,Country;
    public Customer(){
        DbObj = new DBAccess();
        this.OutCustomerList = "";
        this.ContentSize = 0;
    }
    public String GetCustomerList(){//returns the customerlist to OrderServer generated from GenerateCustomerList()
        return this.OutCustomerList;
    }
    public String GetContentSize(){ //returns the byte value of list to OrderServer
        return String.valueOf(ContentSize);
    }
    public void GetCustomer(String CustomerID){//fetches the customer list from database
        Statement smt = DbObj.GetStatement();
        try{
            ResultSet rs = smt.executeQuery("Select * from customers where CustomerId = '"+CustomerID+"'"); //query for getting customer list
            if(rs.next()){
                this.Address = rs.getString("Address");
                this.City = rs.getString("City");
                this.Region = rs.getString("Region");
                this.PostalCode = rs.getString("PostalCode");
                this.Country = rs.getString("Country");
            }else{
                this.Address = "";
                this.City = "";
                this.Region = "";
                this.PostalCode = "";
                this.Country = "";
            }
        }catch(Exception ex){
            
        }
    }
    public static int GetCustomerExist(String CustomerID){//checks if customer exists or not in db
        DBAccess DbObj = new DBAccess();
        try{
            String Query = "Select * from customers where CustomerID='"+CustomerID+"'";
            Statement smt = DbObj.GetStatement();
            ResultSet rs = smt.executeQuery(Query);
            if(rs.next()){
                return 1;
            }
            return 0;
        }catch(Exception ex){
            return 0;    
        }finally{
            DbObj.CloseConnection();
        }
    }
    public void GenerateCustomerList(){
    //Generates Customer List from database
        Statement smt = DbObj.GetStatement();
        try{
        ResultSet rs = smt.executeQuery("select CustomerID,CompanyName from customers order by CustomerID");//query for getting customer list ordering by ID
            while(rs.next()){
                String ID = rs.getString("CustomerID");
                String Name = rs.getString("CompanyName");
                String AppendedString = ID + " "+Name+"\r\n";// used for appending the new list item into OutCustomerList
                OutCustomerList = OutCustomerList + AppendedString;
                ContentSize = OutCustomerList.getBytes().length;
            }
        }catch(Exception ex){
            ContentSize = 0;
        }finally{
            DbObj.CloseConnection();// closes database connection
        }
    }
}
