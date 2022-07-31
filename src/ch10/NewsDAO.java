package ch10;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NewsDAO {
	//h2 database
	final String JDBC_DRIVER = "org.h2.Driver";
	final String JDBC_URL = "jdbc:h2:tcp://localhost/~/jwbookdb";
	
//	final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
//	final String JDBC_URL = "jdbc:mysql://localhost:3306/database1?characterEncoding=UTF-8&serverTimezone=UTC";
	
	
	//DB 연결을 가져오는 메서드
	public Connection open() {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(JDBC_URL, "jwbook", "1234");
			//conn = DriverManager.getConnection(JDBC_URL, "root", "tiger");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	//뉴스 추가 메서드
	public void addNews(News n) throws SQLException {
		Connection conn = open();
		System.out.println(n.toString());
		String sql = "INSERT INTO news(title, img, date, content) values(?,?,CURRENT_TIMESTAMP(),?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		try (conn; pstmt) {
			pstmt.setString(1, n.getTitle());
			pstmt.setString(2, n.getImg());
			pstmt.setString(3, n.getContent());
			pstmt.executeUpdate();
		}
	}

	//뉴스 기사 목록 전체를 가져오는 메서드
	public List<News> getAll() throws SQLException {
		Connection conn = open();
		List<News> newsList = new ArrayList<>();

		String sql = "SELECT aid, title, FORMATDATETIME(date, 'yyyy-MM-dd HH:mm:ss') as cdate from news";
		//String sql = "select aid, title, date from news";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		try (conn; pstmt; rs) {
			while (rs.next()) {
				News n = new News();
				n.setAid(rs.getInt("aid"));
				n.setTitle(rs.getString("title"));
				n.setDate(rs.getString("cdate"));
				//n.setDate(rs.getString("date"));
				newsList.add(n);
			}
		}
		return newsList;
	}
	
	//뉴스 한 개를 클릭했을 때 세부 내용을 보여주는 메서드
	public News getNews(int aid) throws SQLException {
		Connection conn = open();
		System.out.println("getNews 들어옴");
		News n = new News();
		String sql = "SELECT aid, title, img, FORMATDATETIME(date, 'yyyy-MM-dd HH:mm:ss') as cdate, content FROM news WHERE aid=?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, aid);
		ResultSet rs = pstmt.executeQuery();

		rs.next();

		try (conn; pstmt; rs) {
			n.setAid(rs.getInt("aid"));
			n.setTitle(rs.getString("title"));
			n.setImg(rs.getString("img"));
			n.setDate(rs.getString("cdate"));
			n.setContent(rs.getString("content"));
			pstmt.executeQuery();
			return n;
		}
	}
	
	//뉴스 삭제 메서드
	public void delNews(int aid) throws SQLException {
		Connection conn = open();

		String sql = "DELETE FROM news WHERE aid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try (conn; pstmt) {
			pstmt.setInt(1, aid);
			//삭제된 뉴스 기사가 없을 경우
			if (pstmt.executeUpdate() == 0) {
				throw new SQLException("DB에러");
			}
		}
	}
}
