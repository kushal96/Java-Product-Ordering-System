
import java.sql.*;
public class User {//class which handles authetication or executes the functionality of AUTH message  
    private DBAccess DbObj;
    public User(){
        DbObj = new DBAccess();
    }
    public Integer Authenticate(String LastName,String DateOfBirth){//which checks the username and password values from database
        try{
            Statement smt = DbObj.GetStatement();
            ResultSet  rs = smt.executeQuery("select * from employees where LastName='"+LastName+"' and BirthDate='"+DateOfBirth+"'");//executes query for getting LastName and Date of birth 
            if(rs.next()){
                return rs.getInt("EmployeeID");
            }
        }catch(SQLException ex){
            
        }finally{
            DbObj.CloseConnection();
        }
        return -1;
    }
}
