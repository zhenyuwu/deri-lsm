import java.sql.*;
import javax.sql.DataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;


/**
 *  This example shows how to programmatically get and directly use
 *  an pool-backed DataSource
 */
public final class UsePoolBackedDataSource
{

    public static void main(String[] argv)
    {
	try
	    {
		// Note: your JDBC driver must be loaded [via Class.forName( ... ) or -Djdbc.properties]
		// prior to acquiring your DataSource!

		// Acquire the DataSource... this is the only c3p0 specific code here
		String driver="virtuoso.jdbc4.Driver";
		
		Class.forName(driver);		
		DataSource unpooled = DataSources.unpooledDataSource("jdbc:virtuoso://140.203.155.176:1111/DERI.DBA/log_enable=2",
								     "dba",
								     "dba");
		DataSource pooled = DataSources.pooledDataSource( unpooled ,"intergalactoApp");

//		 System.out.println( DataS );

		// get hold of a Connection an do stuff, in the usual way
		Connection con  = null;
		Statement  stmt = null;
		ResultSet  rs   = null;
		try
		    {
			con = pooled.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * from ssc.dba.users");
			while (rs.next())
			    System.out.println( rs.getString(1) );
		    }
		finally
		    {
			//i try to be neurotic about ResourceManagement,
			//explicitly closing each resource
			//but if you are in the habit of only closing
			//parent resources (e.g. the Connection) and
			//letting them close their children, all
			//c3p0 DataSources will properly deal.
			PooledDataSource pds = (PooledDataSource)  pooled;
			  System.err.println("num_connections: "      + pds.getNumConnectionsDefaultUser());
			  System.err.println("num_busy_connections: " + pds.getNumBusyConnectionsDefaultUser());
			  System.err.println("num_idle_connections: " + pds.getNumIdleConnectionsDefaultUser());
			  
			attemptClose(rs);
			attemptClose(stmt);
			
			attemptClose(con);
		    }
			if (  pooled instanceof PooledDataSource)
			{
			  PooledDataSource pds = (PooledDataSource)  pooled;
			  System.err.println("num_connections: "      + pds.getNumConnectionsDefaultUser());
			  System.err.println("num_busy_connections: " + pds.getNumBusyConnectionsDefaultUser());
			  System.err.println("num_idle_connections: " + pds.getNumIdleConnectionsDefaultUser());			  
			  System.err.println();
			}
			else
			  System.err.println("Not a c3p0 PooledDataSource!");
	    }
	catch (Exception e)
	    { e.printStackTrace(); }	
    }

    static void attemptClose(ResultSet o)
    {
	try
	    { if (o != null) o.close();}
	catch (Exception e)
	    { e.printStackTrace();}
    }

    static void attemptClose(Statement o)
    {
	try
	    { if (o != null) o.close();}
	catch (Exception e)
	    { e.printStackTrace();}
    }

    static void attemptClose(Connection o)
    {
	try
	    { if (o != null) o.close();}
	catch (Exception e)
	    { e.printStackTrace();}
    }

    private UsePoolBackedDataSource()
    {}
}
