package Test.bw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class SeoulBike {
	// Field
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	private String loginId;
	private int bikeNum = 0;

	final String driver = "oracle.jdbc.driver.OracleDriver";
	final String url = "jdbc:oracle:thin:@localhost:1521:xe";
	final String userid = "testuser";
	final String passwd = "test1234";
//	final String driver = "oracle.jdbc.driver.OracleDriver";
//	final String url = "jdbc:oracle:thin:@eyc526ouwzdk9qci_medium?TNS_ADMIN=D:/Wallet_EYC526OUWZDK9QCI";
//	final String userid = "ADMIN";
//	final String passwd = "BBBjjang8945";

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
			Class.forName(driver);

			// 연결하기
			conn = DriverManager.getConnection(url, userid, passwd);
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	// 시작 페이지
	public void initialPage() {
		System.out.println("          [시작 페이지]");
		System.out.println("----------------------------------");
		System.out.println("   시민과 함께하는 친환경 교통수단 따릉이");
		System.out.println("----------------------------------");
		System.out.println("       1.로그인 | 2.회원가입");
		System.out.print("선택: ");
		String menuNo = scanner.nextLine();
		if ("1".equals(menuNo)) {
			login();
		} else if ("2".equals(menuNo)) {
			join();
		} else {
			System.out.println("올바른 메뉴를 선택해주세요");
			initialPage();
		}
	}

	// 로그인 메서드
	public void login() {
		Users users = new Users();
		System.out.println("[로그인]");
		System.out.print("아이디: ");
		users.setUserId(scanner.nextLine());
		System.out.print("비밀번호: ");
		users.setUserPassword(scanner.nextLine());
		System.out.println("------------------");
		System.out.println("  1.로그인| 2.취소");
		System.out.print("선택: ");
		String menuNo = scanner.nextLine();
		if ("1".equals(menuNo)) {
			// user 테이블에 사용자 정보 저장
			try {
				String sql = "SELECT pwd FROM users WHERE user_id=? and state=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, users.getUserId());
				pstmt.setInt(2, 1);
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
			mainMenu();
		} else {
			System.out.println("올바른 메뉴를 선택해주세요");
			initialPage();
		}
	}

	// 회원가입 메서드
	public void join() {
		// 입력 받기
		Users user = new Users();
		System.out.println("[회원가입]");
		System.out.print("아이디: ");
		user.setUserId(scanner.nextLine());
		checkId(user);
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
				String sql = "" + "INSERT INTO users (user_id, pwd, email, phone,state) " + "VALUES (?, ?, ?, ?,?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user.getUserId());
				pstmt.setString(2, user.getUserPassword());
				pstmt.setString(3, user.getUserEmail());
				pstmt.setString(4, user.getUserPhone());
				pstmt.setInt(5, 1);
				pstmt.executeUpdate();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		System.out.println("따릉이 회원이 되신 것을 축하드립니다~!");
		System.out.println("로그인을 하세요");
		System.out.println();
		initialPage();
	}

	// 메인메뉴 메서드
	public void mainMenu() {
		System.out.println();
		System.out.println("[메인 페이지] > 사용자: " + loginId);
		System.out.println();
		System.out.println("[대여 가능 자전거 현황] ");
		System.out.println("-------------------------------------------------------------------------");
		System.out.printf("%-10s%-14s%-10s \n", "대여소 ID", "대여소 위치", "대여 가능 자전거 수");
		System.out.println("-------------------------------------------------------------------------");
		try {
			String sql = "" + "SELECT \r\n" + "    R.rental_place_id,\r\n" + "    R.RENTAL_PLACE_LOC,\r\n"
					+ "    SUM(DECODE(B.STATE,1,1)) BIKENUM\r\n" + "FROM BIKE B\r\n" + "JOIN rental_place R\r\n"
					+ "ON B.RENTAL_PLACE_ID = R.RENTAL_PLACE_ID\r\n"
					+ "GROUP BY R.rental_place_id,R.RENTAL_PLACE_LOC\r\n" + "ORDER BY R.rental_place_id DESC";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Rental_place board = new Rental_place();
				board.setRental_place_id(rs.getInt("rental_place_id"));
				board.setRental_place_loc(rs.getString("rental_place_loc"));
				bikeNum = rs.getInt("bikeNum");
				System.out.printf("%-11s%-15s%-3d \n", board.getRental_place_id(), board.getRental_place_loc(),
						bikeNum);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}
		System.out.println();
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println(" 메인 메뉴: 1.자전거 대여 | 2.자전거 반납 | 3. 마이페이지 | 4. 회원탈퇴 | 5. 로그아웃 | 6. 종료");
		System.out.print("메뉴 선택: ");
		String menuNo = scanner.nextLine();
		System.out.println();

		switch (menuNo) {
		case "1" -> rental();
		case "2" -> end();
		case "3" -> mypage();
		case "4" -> withdrawal();// 회원탈퇴 메서드(될지모르겠읍)
		case "5" -> logout();
		case "6" -> exit();
		default -> {
			System.out.println("없는 메뉴입니다.");
			mainMenu();
		}
		}
	}

	// 회원탈퇴 메서드
	public void withdrawal() {
		System.out.println();
		System.out.println("[회원탈퇴 페이지] > 사용자: " + loginId);
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("1. 회원탈퇴 | 2. 취소");
		System.out.print("선택: ");
		String menuNo = scanner.nextLine();
		if ("1".equals(menuNo)) {
			// user 테이블에 state 1 -> 0 으로 변경
			check();
			try {
				String sql = "" + "UPDATE USERS SET STATE=? WHERE USER_ID=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, 0);
				pstmt.setString(2, loginId);
				pstmt.executeUpdate();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
			System.out.println("***회원 탈퇴 완료되었습니다.***");
			initialPage();
		} else if ("2".equals(menuNo)) {
			mainMenu();
		}

	}

	// 렌탈 메서드
	private void rental() {

		check();

		Rental_list board = new Rental_list();
		board.setUser_id(loginId);

		System.out.print("따릉이를 빌릴 대여소 ID를 입력해주세요 : ");
		board.setRental_place_id(scanner.nextLine());
		checkRentalPlace(board);

		// 대여 가능한 자전거 출력
		System.out.println();
		System.out.println("[대여 가능 자전거 ID] > 사용자: " + loginId);
		System.out.println("-------------------------------------------------------------------------");
		System.out.printf("%-8s%-13s \n", "자전거 ID", "자전거 종류");
		System.out.println("-------------------------------------------------------------------------");

		try {
			String sql = "" + "SELECT BIKE_ID, BIKE_TYPE " + "FROM BIKE " + "WHERE STATE=? "
					+ "AND RENTAL_PLACE_ID = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, 1);
			pstmt.setString(2, board.getRental_place_id());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.printf("%-10s%-13s \n", rs.getInt("BIKE_ID"), rs.getString("BIKE_TYPE"));
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}

		// bike_id로 bike state 0로 update, history insert
		System.out.print("빌리고자 하는 따릉이 ID를 입력해주세요 : ");
		board.setBike_id(scanner.nextLine());
		checkBikeId(board);

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
			String sql = "" + "INSERT INTO RENTAL_LIST (USER_ID, BIKE_ID, RENTAL_PLACE_ID, START_TIME, RENTAL_NO) "
					+ "VALUES ( ?, ?, ?, TO_CHAR(sysdate, 'YYYY-MM-DD HH24:MI:SS'),SEQ_RENTNO.NEXTVAL)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, board.getUser_id());
			pstmt.setString(2, board.getBike_id());
			pstmt.setString(3, board.getRental_place_id());

			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		System.out.println();

		try {
			String sql = "" + "SELECT RENTAL_PLACE_LOC " + "FROM RENTAL_PLACE " + "WHERE RENTAL_PLACE_ID = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, board.getRental_place_id());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				board.setRental_place_loc(rs.getString("RENTAL_PLACE_LOC"));
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}

		System.out.println(board.getRental_place_loc() + "(" + board.getRental_place_id() + ") 지점에서 "
				+ board.getBike_id() + "번 자전거를 대여하였습니다.");
		System.out.println("---------- 안전운행 하세요:) ----------");

		mainMenu();
	}

	private void checkRentalPlace(Rental_list board) {
		// 따릉이 대여소 리스트로 뽑기
		ArrayList<String> placeIdList = new ArrayList<>();
		try {
			String sql = "" + "SELECT RENTAL_PLACE_ID " + "FROM RENTAL_PLACE ";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				placeIdList.add(rs.getString("RENTAL_PLACE_ID"));
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}

		while (!placeIdList.contains(board.getRental_place_id())) {

			System.out.println("대여소 ID를 확인해주세요");
			board.setRental_place_id(scanner.nextLine());

		}

	}

	private void checkBikeId(Rental_list board) {
		// 대여 가능한 따릉이 뽑기
		ArrayList<String> list = new ArrayList<>();
		try {
			String sql = "" + "SELECT BIKE_ID " + "FROM BIKE " + "WHERE STATE=? " + "AND RENTAL_PLACE_ID = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, 1);
			pstmt.setString(2, board.getRental_place_id());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(rs.getString("BIKE_ID"));
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}

		while (!list.contains(board.getBike_id())) {

			System.out.println("따릉이 ID를 확인해주세요");
			board.setBike_id(scanner.nextLine());

		}

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
			String sql = ""
					+ "UPDATE RENTAL_LIST SET END_TIME=TO_CHAR(sysdate, 'YYYY-MM-DD HH24:MI:SS') WHERE BIKE_ID=?";
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
		System.out.printf("%-10s%-13s%-14s%-27s%-18s \n", "ID", "따릉이 ID", "대여장소", "대여시간", "반납시간");
		System.out.println(
				"---------------------------------------------------------------------------------------------------");

		// Rental_list 테이블에서 사용자의 이용내역을 가져와서 출력하기
		try {
			String sql = "" + "SELECT user_id, bike_id, rental_place_id, start_time, end_time " + "FROM rental_list "
					+ "WHERE user_id = " + "'" + loginId + "'" + " ORDER BY end_time DESC";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Rental_list board = new Rental_list();
				board.setUser_id(rs.getString("user_id"));
				board.setBike_id(rs.getString("bike_id"));
				board.setRental_place_id(rs.getString("rental_place_id"));
				board.setStart_time(rs.getString("start_time"));
				board.setEnd_time(rs.getString("end_time"));
				System.out.printf("%-10s%-15s%-15s%-29s%-20s \n", board.getUser_id(), board.getBike_id(),
						board.getRental_place_id(), board.getStart_time(), board.getEnd_time());
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

	private void checkId(Users user) {
		// 따릉이 대여소 리스트로 뽑기
		ArrayList<String> list = new ArrayList<>();
		try {
			String sql = "" + "SELECT USER_ID " + "FROM USERS ";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(rs.getString("USER_ID"));
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}

		while (list.contains(user.getUserId())) {

			System.out.println("중복된 ID 입니다. 다른 ID를 입력해주세요");
			System.out.println("ID : ");
			user.setUserId(scanner.nextLine());

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
