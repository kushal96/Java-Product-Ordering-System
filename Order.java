
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Order {// class which handles functions of ORDER message
    private String Address,Region,Country,City,PostalCode;
    private HashMap<String,String> Orders;
    private int ContentSize;
    public Order(String Address,String Region,String Country,String City,String PostalCode){// function for keeping address info into hashmap
        this.Address = Address;
        this.City = City;
        this.Country = Country;
        this.Region = Region;
        this.PostalCode = PostalCode;
        System.out.println("Address    : "+Address);
        System.out.println("City       : "+City);
        System.out.println("Country    : "+Country);
        System.out.println("Region     : "+Region);
        System.out.println("PostalCode : "+PostalCode);
        this.Orders = new HashMap<>();
    }
    public Boolean UpdateAddress(String CustomerID){// Updates address in DB which is prvided by the arguments of NEW message 
        DBAccess DbObj = new DBAccess();
        try{
            Statement smt = DbObj.GetStatement();
            // query for updating address
            String Query = "Update customers set Address='"+this.Address+"',City='"+this.City+"',Country='"+this.Country+"',Region='"+this.Region+"',PostalCode='"+this.PostalCode+"' where CustomerID='"+CustomerID+"'";
            smt.executeUpdate(Query);
            return true;
        }catch(Exception ex){
        
        }finally{
            DbObj.CloseConnection();
        }
        return false;
    }
    public String GetContentSize(){// returns the byte vaue to the OrderServer
        return String.valueOf(ContentSize);
    }
    public void AddOrder(String ProductId,String Qunatity){//adds the quantity of respected ProductID
        String QString = this.Orders.getOrDefault(ProductId, "");
        if(!QString.equals("")){
            int Qty = Integer.parseInt(QString);        
            Qty = Qty + Integer.parseInt(Qunatity);
            this.Orders.put(ProductId,Integer.toString(Qty));
        }else{
            this.Orders.put(ProductId,Qunatity);
        }
    }
    public String GetTotalOrders(){//returns the order list to Orderserver
        String OutOrderList = "";
        for (Map.Entry<String, String> entrySet : Orders.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            String AppendedString = key + " "+value+"\r\n";
            OutOrderList = OutOrderList + AppendedString;
            ContentSize = OutOrderList.getBytes().length;
        }
        return OutOrderList;
    }
    public int PlaceOrder(String CustomerID,String EmployeeID,String ShipName){// This executes the function of ORDER message by adding the products in DB of respected OrderID
        Date RD,D = new Date();
        Calendar C =Calendar.getInstance();
        C.setTime(D);
        C.add(Calendar.DAY_OF_YEAR, 7);//for adding a week into RequiredDate from OrderDate
        RD = C.getTime();
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //qyery for placing order by inserting values into db
        String Query = "insert into orders(CustomerID,EmployeeID,OrderDate,RequiredDate,ShipName,ShipAddress,ShipCity,ShipRegion,ShipPostalCode,ShipCountry)"
           +"values('"+CustomerID+"','"+EmployeeID+"','"+df.format(D)+"','"+df.format(RD)+"','"+ShipName+"','"+Address+"','"+City+"','"+Region+"','"+PostalCode+"','"+Country+"')";
        DBAccess db = new DBAccess();
        try{
            Statement smt = db.GetStatement();
            smt.executeUpdate(Query,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = smt.getGeneratedKeys();
            String OrderID = "";
            if(rs.next()){
                OrderID = rs.getString(1);
                String Query2 = "Insert into orderdetails (OrderID,ProductID,UnitPrice,Quantity) values";//query for adding product and quantity
                int cnt = 0;
                for (Map.Entry<String, String> entrySet : Orders.entrySet()) {
                    if(cnt != 0){
                        Query2 += ",";
                    }else{
                        cnt = cnt + 1;
                    }
                    String key = entrySet.getKey();
                    String value = entrySet.getValue();
                    float Price = Product.GetProductPrice(key);//keeps prices  
                    float Quantity = Float.parseFloat(value);//keeps the units of product
                    Query2 += "('"+OrderID+"',"+key+","+Price+","+value+")";
                }
                smt.executeUpdate(Query2);
            }
            return Integer.parseInt(OrderID);
        }catch(SQLException ex){
            return -1;
        }finally{
            db.CloseConnection();   
        }
        
    }
    public void DropOrder(){// executes DROP message which drops the products list which are added through NEW Message
        this.Orders.clear();//clears the existing added items
    }
}
