package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import app.Server;

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
			// TODO Auto-generated catch block
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

	public static void addData(byte[] imageBytes) {
		// tao ra doi tuong statement
		try {
			Connection connection = DAO.getConnection();

			String sql = "INSERT INTO image (length, byteimage) VALUES (?)";

			// Chuẩn bị PreparedStatement để thực thi câu lệnh SQL
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setBytes(2, imageBytes);

			pstmt.executeUpdate();

			pstmt.close();
			DAO.closeConnection(connection);
		} catch (SQLException e) {
			System.out.println("Lỗi khi ghi dữ liệu xuống data");
			e.printStackTrace();
			System.out.println("-----------");
		}
	}

	public static void getData(int imageId) {
		// tao ra doi tuong statement
		try {
			Connection connection = DAO.getConnection();

			String sql = "SELECT * FROM image WHERE imageid = ?";

			// Chuẩn bị PreparedStatement để thực thi câu lệnh SQL
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, imageId);

			ResultSet result = pstmt.executeQuery();

			if (result.next()) {
				byteimage = result.getBytes("byteimage");
			}
			result.close();
			pstmt.close();
			DAO.closeConnection(connection);
		} catch (SQLException e) {
			System.out.println("Lỗi khi truy xuất dữ liệu");
			e.printStackTrace();
			System.out.println("-----------");
		}
	}
}
