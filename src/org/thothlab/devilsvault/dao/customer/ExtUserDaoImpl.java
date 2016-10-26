package org.thothlab.devilsvault.dao.customer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.thothlab.devilsvault.db.model.Customer;
@Repository ("ExtUserDaoImpl")
public class ExtUserDaoImpl{
	@SuppressWarnings("unused")
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	@Autowired
	public void setdataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	public Double getSavingsBalance(Customer user)
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql ="SELECT balance FROM  bank_accounts WHERE external_users_id="+user.getId()+" and account_type='SAVINGS'";
		Double balance = 0.0d;
		try{
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				balance = rs.getDouble("balance");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				ps.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return balance;
     }
	public Double getCheckingBalance(Customer user)
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql ="SELECT balance FROM  bank_accounts WHERE external_users_id="+user.getId()+" and account_type='CHECKINGS'";
		Double balance = 0.0d;
		try{
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				balance = rs.getDouble("balance");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				ps.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return balance;
     }
	
	public Customer setExternalUser(String name,String address,Integer phone,String email, Date date_of_birth,String ssn)
    {
        Customer userDetails = new Customer();
        userDetails.setName(name);
        userDetails.setAddress(address);
        userDetails.setDate_of_birth(date_of_birth);
        userDetails.setEmail(email);
        userDetails.setPhone(phone);
        userDetails.setSsn(ssn);
        return userDetails;
    }
	
	public Integer createUser(Customer userdetails)
    {
        String query = "INSERT INTO external_users ( id , name , address ,city,state,country,pincode, phone , email , date_of_birth , ssn ) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        Connection con = null;
        PreparedStatement ps = null;
        try{
            con = dataSource.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, userdetails.getId());
            ps.setString(2, userdetails.getName());
            ps.setString(3, userdetails.getAddress());
            ps.setString(4, "sdsdsad");
            ps.setString(5, "sdsdsad");
            ps.setString(6, "sdsdsad");
            ps.setInt(7, 123);
            ps.setInt(8, userdetails.getPhone());
            ps.setString(9, userdetails.getEmail());
            ps.setDate(10, userdetails.getDate_of_birth());
            ps.setString(11, userdetails.getSsn());
            int out = ps.executeUpdate();
            if(out !=0){
                 String queryID = "SELECT id from external_users where email= '" + userdetails.getEmail() + "'"; 
                    Integer id = jdbcTemplate.queryForList(queryID, Integer.class).get(0);
                    return id;
            }else return 0;
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try {
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
