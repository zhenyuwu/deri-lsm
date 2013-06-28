 
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
 
/** A test project demonstrating the use of BoneCP in a JDBC environment.
 * @author wwadge
 *
 */
public class ExampleJDBC {
 
	/** Start test
	 * @param args none expected.
	 */
	public static void main(String[] args) {
		BoneCP connectionPool = null;
		Connection connection = null;
 
		try {
			// load the database driver (make sure this is in your classpath!)
			Class.forName("virtuoso.jdbc4.Driver");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			// setup the connection pool
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl("jdbc:virtuoso://140.203.155.176:1111/DERI.DBA/log_enable=2"); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
			config.setUsername("dba"); 
			config.setPassword("dba");
			config.setMinConnectionsPerPartition(5);
			config.setMaxConnectionsPerPartition(10);
			config.setPartitionCount(1);
			connectionPool = new BoneCP(config); // setup the connection pool
			
			connection = connectionPool.getConnection(); // fetch a connection
			
			if (connection != null){
				System.out.println("Connection successful!");
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM ssc.dba.users"); // do something with the connection.
				while(rs.next()){
					System.out.println(rs.getString(1)); // should print out "1"'
				}
				System.out.println(connectionPool.getTotalCreatedConnections());
				System.out.println(connectionPool.getTotalFree());
				System.out.println(connectionPool.getTotalLeased());
			}
			connection.close();
			System.out.println(connectionPool.getTotalFree());
			System.out.println(connectionPool.getTotalLeased());
			connectionPool.shutdown(); // shutdown connection pool.
			System.out.println(connectionPool.getTotalFree());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}