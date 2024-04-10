package Test.bw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SeoulBike {
	// Field
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	private String loginId;
	private String rent_place;
	private String rent_bike;

	public String getloginId() {
		return loginId;
	}

	public void setloginId(String loginId) {
		this.loginId = loginId;
	}

	// Constructor
	public SeoulBike() {
		try {
			// JDBC Driver 등록
			Class.forName("oracle.jdbc.OracleDriver");

			// 연결하기
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "testuser", "test1234");
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	// 시작 페이지
	public void initialPage() {
		System.out.println("[시작 페이지]");
		System.out.println("--------------------------");
		System.out.println("시민과 함께하는 친환경 교통수단 따릉이");
		System.out.println("--------------------------");
		System.out.println("1.로그인 | 2.회원가입");
		System.out.print("선택: ");
		String menuNo = scanner.nextLine();
		if ("1".equals(menuNo)) {
			login();
		} else if ("2".equals(menuNo)) {
			join();
		}
	}

	// 로그인 메서드
	public void login() {
		Users users = new Users();
		System.out.println("[로그인]");
		System.out.print("아이디: ");
		users.setUserId(scanner.nextLine());
		System.out.println(users.getUserId());
		System.out.print("비밀번호: ");
		users.setUserPassword(scanner.nextLine());
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("1.Ok | 2.Cancel");
		System.out.print("선택: ");
		String menuNo = scanner.nextLine();
		if ("1".equals(menuNo)) {
			// user 테이블에 사용자 정보 저장
			try {
				String sql = "SELECT pwd FROM users WHERE user_id=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, users.getUserId());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					String dbPassword = rs.getString("pwd");
					if (dbPassword.equals(users.getUserPassword())) {
						setloginId(users.getUserId());
					} else {
						System.out.println("비밀번호가 일치하지 않습니다.");
						initialPage();
					}
				} else {
					System.out.println("아이디가 존재하지 않습니다.");
					initialPage();
				}
				rs.close();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		mainMenu();
	}

	// 회원가입 메서드
	public void join() {
		// 입력 받기
		Users user = new Users();
		System.out.println("[회원가입]");
		System.out.print("아이디: ");
		user.setUserId(scanner.nextLine());
		System.out.print("비밀번호: ");
		user.setUserPassword(scanner.nextLine());
		System.out.print("이메일: ");
		user.setUserEmail(scanner.nextLine());
		System.out.print("휴대폰: ");
		user.setUserPhone(scanner.nextLine());

		// 보조 메뉴 출력
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("1.Ok | 2.Cancel");
		System.out.print("선택: ");
		String menuNo = scanner.nextLine();
		if (menuNo.equals("1")) {
			// users 테이블에 게시물 정보 저장
			try {
				String sql = "" + "INSERT INTO users (user_id, password, email, phone) " + "VALUES (?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user.getUserId());
				pstmt.setString(2, user.getUserPassword());
				pstmt.setString(3, user.getUserEmail());
				pstmt.setString(4, user.getUserPhone());
				pstmt.executeUpdate();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
	}

	// 메인메뉴 메서드
	public void mainMenu() {
		System.out.println();
		System.out.println("[메인 페이지] > 사용자: " + loginId);
		System.out.println();
		System.out.println("[대여 가능 자전거 현황] ");
		System.out.println("-------------------------------------------------------------------------");
		System.out.printf("%-10s%-13s%-10s \n", "대여소 ID", "대여소 위치", "대여 가능 자전거 수");
		System.out.println("-------------------------------------------------------------------------");
		try {
			String sql = "" + "SELECT rental_place_id, rental_place_loc " + "FROM rental_place "
					+ "ORDER BY rental_place_id DESC";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Rental_place board = new Rental_place();
				board.setRental_place_id(rs.getInt("rental_place_id"));
				board.setRental_place_loc(rs.getString("rental_place_loc"));
				System.out.printf("%-10s%-15s \n", board.getRental_place_id(), board.getRental_place_loc());
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}
		System.out.println();
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("메인 메뉴: 1.자전거 대여 | 2.자전거 반납 | 3. 마이페이지 | 4. 회원탈퇴 | 5. 로그아웃 | 6. 종료");
		System.out.print("메뉴 선택: ");
		String menuNo = scanner.nextLine();
		System.out.println();

		switch (menuNo) {
		case "1" -> rental();
		case "2" -> end();
		case "3" -> mypage();
		case "4" -> System.out.println("회원탈퇴");// 회원탈퇴 메서드(될지모르겠읍)
		case "5" -> logout();
		case "6" -> exit();
		}
	}

	// 렌탈 메서드
	private void rental() {

		check();

		Rental_list board = new Rental_list();
		board.setUser_id(loginId);

		System.out.println("따릉이를 빌릴 대여소 ID를 입력해주세요.");
		board.setRental_place_id(scanner.nextLine());

		// 대여 가능한 자전거 출력
		System.out.println();
		System.out.println("[메인 페이지] > 사용자: " + loginId);
		System.out.println();
		System.out.println("[대여 가능 자전거 ID] ");

		try {
			String sql = "" + "SELECT BIKE_ID " + "FROM BIKE " + "WHERE STATE=? " + "AND RENTAL_PLACE_ID = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, 1);
			pstmt.setString(2, board.getRental_place_id());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getInt("BIKE_ID"));
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}
		// bike_id로 bike state 0로 update, history insert
		System.out.println("빌리고자 하는 따릉이 ID를 입력해주세요.");
		board.setBike_id(scanner.nextLine());

		try {
			String sql = "" + "UPDATE BIKE SET STATE=? WHERE BIKE_ID=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, 0);
			pstmt.setString(2, board.getBike_id());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}

		try {
			String sql = "" + "INSERT INTO RENTAL_LIST (BIKE_ID, RENTAL_PLACE_ID,START_TIME, USER_ID,RENTAL_NO) "
					+ "VALUES ( ?, ?, SYSDATE,?,SEQ_RENTNO.NEXTVAL)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, board.getBike_id());
			pstmt.setString(2, board.getRental_place_id());
			pstmt.setString(3, board.getUser_id());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		System.out.println();
		System.out.println(board.getRental_place_id() + "지점에서 " + board.getBike_id() + "번 자전거를 대여하였습니다.");
		System.out.println("---------- 안전운행 하세요:) ----------");

		mainMenu();
	}

	// 반납 메서드
	// 해당 아이디로 Rental_list 접근 state가 0인 bike를 모두 1으로 update
	private void end() {

		Rental_list board = new Rental_list();
		board.setUser_id(loginId);
		// 로그인 한 아이디가 대여중인 bike_id 가져오기
		try {
			String sql = "" + "SELECT BIKE_ID FROM RENTAL_LIST WHERE USER_ID=? AND END_TIME IS NULL";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, loginId);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			board.setBike_id(rs.getString("BIKE_ID"));
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}
		// rental_list에 entime 기록
		try {
			String sql = "" + "UPDATE RENTAL_LIST SET END_TIME=SYSDATE WHERE BIKE_ID=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, board.getBike_id());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}

		// state가 0인 bike를 모두 1으로 update
		try {
			String sql = "" + "UPDATE BIKE SET STATE=? WHERE BIKE_ID=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, 1);
			pstmt.setString(2, board.getBike_id());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		System.out.println();
		System.out.println("따릉이 반납이 완료되었습니다.");

		mainMenu();
	}

	// 마이페이지 메서드
	public void mypage() {
		// 타이틀 및 컬럼명 출력
		System.out.println();
		System.out.println("[마이페이지] > 사용자: " + loginId);
		System.out.println(
				"---------------------------------------------------------------------------------------------------");
		System.out.printf("%-10s%-15s%-15s%-25s%-20s%-20s \n", "use_id", "user_id", "bike_id", "rental_place_id",
				"start_time", "end_time");
		System.out.println(
				"---------------------------------------------------------------------------------------------------");

		// history 테이블에서 사용자의 이용내역을 가져와서 출력하기
		try {
			String sql = "" + "SELECT use_id, user_id, bike_id, rental_place_id, start_time, end_time "
					+ "FROM rental_list " + "WHERE user_id = " + "'" + loginId + "'" + " ORDER BY use_id DESC";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Rental_list board = new Rental_list();
				board.setUse_id(rs.getInt("use_id"));
				board.setUser_id(rs.getString("user_id"));
				board.setBike_id(rs.getInt("bike_id"));
				board.setRental_place_id(rs.getInt("rental_place_id"));
				board.setStart_time(rs.getDate("start_time"));
				board.setEnd_time(rs.getDate("end_time"));
				System.out.printf("%-10s%-15s%-15s%-25s%-20s%-20s \n", board.getUse_id(), board.getUser_id(),
						board.getBike_id(), board.getRental_place_id(), board.getStart_time(), board.getEnd_time());
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}
		System.out.println();
		System.out.println("---------------------------");
		System.out.println("1. 메인메뉴로 돌아가기 | 2. 종료");
		System.out.print("선택: ");
		String menuNo = scanner.nextLine();
		System.out.println();

		switch (menuNo) {
		case "1" -> mainMenu();
		case "2" -> exit();
		}
	}

	private void check() {

		Rental_list board = new Rental_list();
		board.setUser_id(loginId);

		// 대여중인지 확인

		try {
			String sql = "" + "SELECT BIKE_ID FROM RENTAL_LIST WHERE USER_ID=? AND END_TIME IS NULL";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, loginId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				System.out.println("반납을 먼저 진행해 주세요!");
				mainMenu();
			}

			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}

	}

	// 로그아웃 메서드
	public void logout() {
		// 로그인 아이디 없애기
		loginId = null;

		// 초기화면으로 복귀
		initialPage();
	}

	// 종료 메서드
	public void exit() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
		System.out.println("** 따릉이를 이용해주셔서 감사합니다 **");
		System.exit(0);
	}

}
