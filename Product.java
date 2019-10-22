
import java.sql.ResultSet;
import java.sql.Statement;


public class Product {//class which maintains the function of productlist
    private DBAccess DbObj;
    private int ContentSize;
    private String OutProductList = "";// stores the generated productlist
    public Product(){
        DbObj = new DBAccess();
        this.ContentSize = 0;
        this.OutProductList = "";
    }
    public String GetProductList(){//returns the productlist to OrderServer
        return OutProductList;
    }
    public String GetContentSize(){//returns the byte size of productlist
        return String.valueOf(ContentSize);
    }
    public static int GetProductPrice(String ProductID){// fetches Unitprice of respected product from database
        DBAccess TempObj = new DBAccess();  
        try{
            String Query = "select UnitPrice from products where ProductID = "+ProductID;//query for getting product list
            Statement smt = TempObj.GetStatement();
            ResultSet rs = smt.executeQuery(Query);
            if(rs.next()){
                return rs.getInt("UnitPrice");
            }else{
                return -1;
            }
        }catch(Exception ex){
            return -1;
        }finally{
            TempObj.CloseConnection();
        }
    }
    public static int CheckProductExist(String ProductId){//which checks whether inputted product is not dicontinued or exists in database
        DBAccess dbCheck = new DBAccess();
        try{
            Statement smt = dbCheck.GetStatement();
            ResultSet rs = smt.executeQuery("Select * from products where ProductID = "+ProductId);//query for getting product list
            if(rs.next()){
                boolean Discontinued = rs.getBoolean("Discontinued");
                if(Discontinued == true){
                    return -1;
                }else{
                    return 0;
                }
            }else{
                return 1;
            }
        }catch(Exception ex){
            return 1;
        }finally{
            dbCheck.CloseConnection();
        }
    }
    public void GenerateCustomerList(){//fetches product list from DB
        Statement smt = DbObj.GetStatement();
        try{
        ResultSet rs = smt.executeQuery("select ProductID,ProductName from products order by ProductID"); //query for getting product list ordering by ID
            while(rs.next()){
                String ID = rs.getString("ProductID");
                String Name = rs.getString("ProductName");
                String AppendedString = ID + " "+Name+"\r\n";// used for appending new string into OutProductList
                OutProductList = OutProductList + AppendedString;
                ContentSize = OutProductList.getBytes().length;
            }
        }catch(Exception ex){
            ContentSize = 0;
        }finally{
            DbObj.CloseConnection();
        }
    }
}
