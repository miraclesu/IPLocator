package com.glodon.miracle.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgreSQL {
	private String username = "postgres";
	private String password = "postgres";
	protected String driverClassName = "org.postgresql.Driver";
	private String jdbcUrl = "jdbc:postgresql://127.0.0.1:5432/ipsdb";
	private static PostgreSQL instance = null;
	
	public synchronized static PostgreSQL getInstance() {
		if (null == instance)
			instance = new PostgreSQL();
		return instance;
	}

	public Connection getConnection() throws SQLException {
		Connection conn;
		if (username != null)
			conn = DriverManager.getConnection(jdbcUrl, username, password);
		else
			conn = DriverManager.getConnection(jdbcUrl);
		return conn;
	}

	public boolean isExistTalbe (Connection conn, String tableName) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn
					.prepareStatement("select tablename from pg_tables where tablename = ? and schemaname='public'");
			pst.setString(1, tableName);
			rs = pst.executeQuery();
			return rs.next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public boolean createTable(Connection conn, String tableName) {
		PreparedStatement pst = null;
		String sql = "create table " + tableName + " (id serial not null, ip_start inet, ip_end inet, country varchar(255), area varchar(255), " +
				"constraint \"" + tableName + "_pkey\" primary key (id))";
		try {
			conn = getConnection();
			pst = conn
					.prepareStatement(sql);
			
			return pst.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}
