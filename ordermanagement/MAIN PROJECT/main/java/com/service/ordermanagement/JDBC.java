package com.service.ordermanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {

	public static Connection connection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			// here sonoo is database name, root is username and password
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/orders", "root", "password01");
			return con;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static ResultSet query(Connection con) {

		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from emp2");
			return rs;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
