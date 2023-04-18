package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DAO {
	public DAO() {

	}

	public static byte[] byteimage;
	public static int imageid;

	public static Server server;

	public static Connection getConnection() {
		Connection c = null;

		try {
			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
			String url = "jdbc:mysql://localhost:3306/newdatabase";
			String user = "root";
			String password = "";

			c = DriverManager.getConnection(url, user, password);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return c;
	}

	public static void closeConnection(Connection c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addData(String username, String password, String nickname) {
		// tao ra doi tuong statement
		try {
			Connection connection = DAO.getConnection();

			String sql = "INSERT INTO user (username, password, nickname) VALUES (?, ?, ?)";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, nickname);

			pstmt.executeUpdate();
			System.out.println("Dang ky thanh cong");
			pstmt.close();
			DAO.closeConnection(connection);
		} catch (SQLException e) {
			System.out.println("Lỗi khi ghi dữ liệu xuống data");
			e.printStackTrace();
			System.out.println("-----------");
		}
	}

	public static boolean isDuplicateValue(String username, String nicknage) {
		try {
			Connection connection = DAO.getConnection();
			String sql = "SELECT * FROM user WHERE username = ? OR nickname = ?";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nicknage);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return true;
			}
			rs.close();
			pstmt.close();
			DAO.closeConnection(connection);
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getData(String username, String password) {
		// tao ra doi tuong statement
		try {
			Connection connection = DAO.getConnection();

			String sql = "SELECT * FROM user WHERE username = ?";

			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String retrievedPassword = rs.getString("password");
				String retrievedNickname = rs.getString("nickname");
				if (password.equals(retrievedPassword)) {
					return retrievedNickname;
				}
			}
			rs.close();
			pstmt.close();
			DAO.closeConnection(connection);
			return "";
		} catch (SQLException e) {
			System.out.println("Lỗi khi truy xuất dữ liệu");
			e.printStackTrace();
			System.out.println("-----------");
			return "";
		}
	}
}
