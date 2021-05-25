
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EmbeddedSQL {

   
   private Connection _connection = null;


   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));


   public EmbeddedSQL (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }
   }

  
   public void executeUpdate (String sql) throws SQLException {
     
      Statement stmt = this._connection.createStatement ();

      
      stmt.executeUpdate (sql);

      
      stmt.close ();
   }

  
   public int executeQuery (String query) throws SQLException {

      Statement stmt = this._connection.createStatement ();


      ResultSet rs = stmt.executeQuery (query);

     
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }
      stmt.close ();
      return rowCount;
   }
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }
      }catch (SQLException e){
         
      }
   }

   
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            EmbeddedSQL.class.getName () +
            " <dbname> <port> <user>");
         return;
      }
      
      Greeting();
      EmbeddedSQL esql = null;
      try{

         Class.forName ("org.postgresql.Driver").newInstance ();
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new EmbeddedSQL (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("0. Find the pid of parts with cost lower than $_____ (example)");
            System.out.println("1. Find the total number of parts supplied by each supplier");
            System.out.println("2. Find the total number of parts supplied by each supplier who supplies at least 3 parts");
            System.out.println("3. For every supplier that supplies only green parts, print the name of the supplier and the total number of parts that he supplies");
            System.out.println("4. For every supplier that supplies green part and red part, print the name and the price of the most expensive part that he supplies"); 
            System.out.println("5. Find the name of parts with cost lower than $_____");
            System.out.println("6. Find the address of the suppliers who supply _____________ (pname)");
            System.out.println("9. < EXIT");

            switch (readChoice()){
               case 0: QueryExample(esql); break;
               case 1: Query1(esql); break;
               case 2: Query2(esql); break;
               case 3: Query3(esql); break;
               case 4: Query4(esql); break;
               case 5: Query5(esql); break;
               case 6: Query6(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }
         }
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{

         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }
         }catch (Exception e) {
            
         }
      }
   }
   
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }


   public static int readChoice() {
      int input;
      
      do {
         System.out.print("Please make your choice: ");
         try { 
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }
      }while (true);
      return input;
   }

   public static void QueryExample(EmbeddedSQL esql){
      try{
         String query = "SELECT * FROM Catalog WHERE cost < ";
         System.out.print("\tEnter cost: $");
         String input = in.readLine();
         query += input;

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   
   public static void Query1(EmbeddedSQL esql){
      try{
		  String query = "select COUNT(*)FROM suppliers;";
		  int count = esql.executeQuery(query);
		  System.out.println ("total number of parts supplied by each supplier" + count);
		}catch(Exception e){
         System.err.println (e.getMessage());
      }
	  
   }

   public static void Query2(EmbeddedSQL esql){
     try{
		  String query = "select count(sid) from catalog GROUP BY sid having count(*) > 2;";
		  int count = esql.executeQuery(query);
		  System.out.println ("total number of parts supplied by each supplier who supplies at least 3 parts." + count);
		}catch(Exception e){
         System.err.println (e.getMessage());
      }
	  
   }

   public static void Query3(EmbeddedSQL esql){
      try{
		  
		  String query = "SELECT suppliers.sname, COUNT(*) as PartCount FROM suppliers , catalog , parts  WHERE catalog.sid = suppliers.sid and parts.pid = catalog.pid and parts.color = 'Green' GROUP BY suppliers.sname, suppliers.sid;";
		  int rs = esql.executeQuery(query);
		  System.out.println ("every supplier that supplies only green parts" + rs);
		}catch(Exception e){
         System.err.println (e.getMessage());
      }
	    
	
 }
	  
   

   public static void Query4(EmbeddedSQL esql){
       try{
	   String query = "SELECT suppliers.sname, MAX(catalog.cost) FROM suppliers, catalog, parts WHERE parts.pid = catalog.pid and suppliers.sid =catalog.sid and suppliers.sid IN(SELECT suppliers.sid FROM suppliers, parts, catalog WHERE suppliers.sid = catalog.sid and parts.pid = catalog.pid and parts.color = 'Red') and suppliers.sid IN(SELECT suppliers.sid FROM suppliers, parts,catalog WHERE suppliers.sid = catalog.sid and parts.pid = catalog.pid and parts.color = 'Green') GROUP BY suppliers.sname;";
	   int output = esql.executeQuery(query);
	   System.out.println ("total row(s): " + output);
       }
       catch(Exception e){
	   System.err.println (e.getMessage());
       }     
   }

   public static void Query5(EmbeddedSQL esql){
       try{
		
		 
         String query = "SELECT parts.pname FROM parts, catalog WHERE parts.pid = catalog.pid and catalog.cost < ";
		 System.out.print("\tEnter cost: $");
		 String input = in.readLine();
		 query += input;
         int rs = esql.executeQuery(query);
         System.out.println ("total row(s): " + rs);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }

   }
   public static void Query6(EmbeddedSQL esql){
       try{
	   System.out.print("\tEnter suppliers parts Name: ");
	   String input = in.readLine();
	   String query = "SELECT suppliers.address FROM suppliers, parts, catalog WHERE suppliers.sid = catalog.sid AND parts.pid = catalog.pid AND parts.pname = \'" + input + " \'";
	   int output = esql.executeQuery(query);
	   System.out.println ("total row(s): " + output);
       }
       catch(Exception e){
	   System.err.println (e.getMessage());
       }
   }
}
