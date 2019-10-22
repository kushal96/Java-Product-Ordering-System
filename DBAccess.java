
import java.sql.*;
//Database Access Class for Single Gateway Access With Same Configuration
//No need to Configure Connection multiple Times
public class DBAccess {
    //Connection String For Database
    private String url;
    //Store total Numbers of Connection Is Live By Programme
    static int TotalConnections;
    //Store Total Numbers of Connection Happen From the Start of code
    static int OpenedConnections;
    //Connection object which store reference to SqlDatabase
    private Connection con;
    //Used to Store ErrorMessage Given BY Database
    private String ErrorMessage;
    static{
        TotalConnections = 0;
        OpenedConnections = 0;
    }
    public DBAccess() 
    {
        /* 
        Setting All Configuration Parameters
        These Parameters Are getting Through Common Class
        */
        this.url = url;
        this.ErrorMessage = "";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(Common.url,Common.username,Common.password);
            //System.out.println("Connection Successfully");
            TotalConnections++; //Increase the Connection
            OpenedConnections++; // Increment The Counter
        }
        catch(SQLException ex){
            ErrorMessage = "Connection Failed";
            System.out.println(ex.getMessage());
        }catch(Exception ex){
            ErrorMessage = "Object instilization Failed";
        }
    }
    public Statement GetStatement()
    { 
        if(ErrorMessage.equals("")){ // Checking Whether Any Error Occur During Connection
            try{
                if(con.isClosed() == true){//Checking Connection Status
                    con = DriverManager.getConnection(Common.url,Common.username,Common.password);
                    TotalConnections++;//Create New Connection If Connection is Already Closed
                    OpenedConnections++;
                }
                return con.createStatement();
            }catch(SQLException ex){
                return null;
            }
        }
        return null;
    }
    
    public void CloseConnection(){
        try{
            if(this.con.isClosed()== false){ //Check Connection Is Closed Or Not
                this.con.close();//If Connection is Open, Closed it
                TotalConnections --; // Decrease The Connection Counter
            }
        }catch(Exception ex){
            
        }
    }
    
    public static void printTotalConnections(){
        System.out.println("Total Open Connections"+TotalConnections); //Print Total Number of Connection Exists There
    }
}
