package Controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import Dao.artistDao;
import Model.art;
import Model.artComment;
import Model.attendants;
import Model.board;
import Model.boardComment;
import Model.lecture;
import Model.lectureComment;
import Model.member;
import Model.pay;
import Model.qna;
import Model.qnaComment;
import Service.artService;
import Service.artistService;
import Service.boardService;
import Service.lectureService;
import Service.mainService;
import Service.memberService;
import net.sf.json.JSONArray;

@Controller
public class boardController {

	@Autowired
	private lectureService lectureService;

	@Autowired
	private boardService boardService;

	@Autowired
	private artService artService;

	@Autowired
	private artistService artistService;

	@Autowired
	private memberService memberService;

	@Autowired
	private mainService mainService;
	
	@Autowired
	private artistDao artistDao;

	@RequestMapping("artistForm.do")
	public ModelAndView artistForm() {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> result = new HashMap<>();
		result = artistService.selectTopArtist();
		mav.addAllObjects(result);
		mav.addObject("viewChoice", 1);
		mav.setViewName("artistForm");
		return mav;
	}

	@RequestMapping("searchArtist.do")
	public ModelAndView searchArtist(@RequestParam(defaultValue = "0") int type,
			@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String searchWord) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> params = new HashMap<>();
		params.put("page", page);
		params.put("type", type);
		params.put("searchWord", searchWord);
		if (type == 1) {
			params.put("nickname", searchWord);
		}
		if (type == 2) {
			params.put("name", searchWord);
		}
		if (type == 3) {
			params.put("id", searchWord);
		}
		HashMap<String, Object> result = new HashMap<>();
		result = artistService.searchArtist(params, page);
		mav.addAllObjects(result);
		mav.addAllObjects(params);
		mav.addObject("viewChoice", 2);
		mav.setViewName("artistForm");
		return mav;
	}

	@RequestMapping("genreArtist.do")
	public ModelAndView genreArtist(@RequestParam(defaultValue = "1") int page, String genre) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> result = new HashMap<>();
		result = artistService.selectRecentArtist(genre, page);
		mav.addAllObjects(result);
		mav.addObject("genre", genre);
		mav.addObject("viewChoice", 3);
		mav.setViewName("artistForm");
		return mav;
	}

	@RequestMapping("artForm.do")
	public ModelAndView artForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("originalList", artService.selectTopArt("original"));
		mav.addObject("paintingList", artService.selectTopArt("painting"));
		mav.addObject("sculptureList", artService.selectTopArt("sculpture"));
		mav.addObject("viewChoice", 1);
		mav.setViewName("artForm");
		return mav;
	}

	@RequestMapping("searchArt.do")
	public ModelAndView searchArt(@RequestParam(defaultValue = "0") int type,
			@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String searchWord,
			@RequestParam(required = false) String genre) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> params = new HashMap<>();
		params.put("page", page);
		params.put("type", type);
		params.put("searchWord", searchWord);
		params.put("genre", genre);
		if (type == 1) {
			params.put("title", searchWord);
		}
		if (type == 2) {
			params.put("nickname", searchWord);
		}
		if (type == 3) {
			params.put("content", searchWord);
		}
		if (type == 4) {
			params.put("title", searchWord);
			params.put("content", searchWord);
		}
		HashMap<String, Object> result = new HashMap<>();
		result = artService.searchArt(params, page);
		mav.addAllObjects(params);
		mav.addAllObjects(result);
		mav.addObject("viewChoice", 2);
		mav.setViewName("artForm");
		return mav;
	}

	@RequestMapping("genreArt.do")
	public ModelAndView genreArt(@RequestParam(defaultValue = "1") int page, int genre) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> result = new HashMap<>();
		String genreStr = null;
		if (genre == 1) {
			genreStr = "original";
		}
		if (genre == 2) {
			genreStr = "painting";
		}
		if (genre == 3) {
			genreStr = "sculpture";
		}
		result = artService.selectRecentArt(genreStr, page);
		mav.addAllObjects(result);
		mav.addObject("genre", genreStr);
		mav.addObject("genreInt", genre);
		mav.addObject("viewChoice", 3);
		mav.setViewName("artForm");
		return mav;
	}

	@RequestMapping("selectOneArt.do") // 작품 상세페이지로 이동
	public String selectOneArt(@RequestParam int no, @RequestParam(defaultValue = "0") String deleteText, Model model,
			HttpSession session) {
		// 해당 작품 정보와 댓글 정보를 담고 상세 페이지로 이동함
		// art 객체 : 상세 정보
		// comment 객체 : 해당 작품에 달린 댓글들
		String id = (String) session.getAttribute("id");
		art art = new art();
		HashMap<String, Object> params = new HashMap<>();
		
		params.put("id", id);

		art = artService.selectOneArt(no);
		model.addAttribute(art);
		model.addAttribute("currentId", id);
		model.addAttribute("deleteText", deleteText);
		model.addAttribute("likesCheck", artistService.selectLikesArt(params));

		if (id.equals(art.getId()))
			model.addAttribute("sameId", 1);

		return "artDetail";
	}

	@RequestMapping("artComment.do") // 댓글 입력
	public void artComment(HttpSession session, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setCharacterEncoding("UTF-8");

		int no = Integer.parseInt(request.getParameter("no"));
		int childNode = Integer.parseInt(request.getParameter("childNode"));
		int groupNo = Integer.parseInt(request.getParameter("groupNo"));

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("comment", "lectureComment");
		params.put("no", no);

		if (groupNo == 0) {
			groupNo = artService.getMaxGroupNo(params) + 1;
		} else {
			groupNo = Integer.parseInt(request.getParameter("groupNo"));
		}
		String id = (String) session.getAttribute("id");

		artComment artComment = new artComment();
		artComment artComment1 = new artComment();

		artComment.setNo(no);
		artComment.setId(id);
		artComment.setContent(request.getParameter("content"));
		artComment.setGroupNo(groupNo);
		artComment.setChildNode(childNode);
		artComment.setWriter(request.getParameter("writer"));

		artService.insertArtComment(artComment);

		artComment1 = artService.selectArtLatestcomment();

		JSONArray jsonArray = JSONArray.fromObject(artComment1);

		response.getWriter().print(jsonArray);

		// 알림 소스 추가
		String writerID = artService.selectOneArt(no).getId();
		mainService.insertAlarm("artComment", writerID, id);

	}

	@RequestMapping("artPayForm.do") // 결제 폼 요청
	public String artPayForm(HttpServletRequest request, HttpSession session, Model model) throws ParseException {

		String id = (String) session.getAttribute("id"); // 세션에서 id 가져오기

		// art 자료 객체 생성하여 value 대입
		// String 으로 넘어온 값들 모델 클래스 자료형에 맞게 형변환!
		art art = new art();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		int no = Integer.parseInt(request.getParameter("no"));
		Date date1 = dateformat.parse((request.getParameter("artDate")));
		int price = Integer.parseInt(request.getParameter("price"));

		art.setNo(no);
		art.setContent(request.getParameter("conetent"));
		art.setArtDate(date1);
		art.setPrice(price);
		art.setId("id");
		art.setGenre("genre");
		art.setTitle(request.getParameter("title"));

		// member 객체 생성하여 주문자 정보 가져오기
		member member = new member();
		member = memberService.selectOneMember(id);

		// 주문 번호 생성
		// 고유한 값이 필요하기에 생성
		// 주문품목명 + 구매자 id + 구입한 시간
		String orderNumber = request.getParameter("no") + "_" + id + "_" + new Date().getTime();

		// 각 정보들 모델에 담아서 결제 폼으로 이동!
		model.addAttribute(member);
		model.addAttribute(art);
		model.addAttribute("orderNumber", orderNumber);
		model.addAttribute("payMethod", request.getParameter("payMethod"));

		return "artPayForm";
	}

	@RequestMapping("artPay.do") // 결제 액션 실행
	public void artPay(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 카드 결제 시 수행되는 메소드
		// 결제 성공 시에 해당 정보를 받아 pay 테이블에 삽입하기 위함

		pay pay = new pay();
		int no = Integer.parseInt(request.getParameter("no"));
		int amount = Integer.parseInt(request.getParameter("amount"));
		
		pay.setNo(no);
		pay.setId(request.getParameter("customer_uid"));
		pay.setAddr(request.getParameter("buyer_addr"));
		pay.setPhone(request.getParameter("buyer_tel"));
		pay.setName(request.getParameter("buyer_name"));
		pay.setTotalPrice(amount);
		pay.setPayMethod(1);
		pay.setState(1);
		pay.setOrderNumber(request.getParameter("merchant_uid"));

		int result = artService.insertArtPay(pay);

		System.out.println("result : " + result);

		if (result == 1) {					// updateArt 쿼리 수정으로 인한 코드 변경 (09.11-종문)
			art originalArt = new art();
			originalArt = artService.selectOneArt(no);
			art art = new art();
			art.setSellCheck(-1);			// java에서 int형의 default값을 '0'으로 넘기는 것을 피하기 위해 '-1' 임의 세팅
			art.setPrice(-1);
			art.setIsCheck(-1);
			art.setState(-1);
			art.setTotalCount((originalArt.getTotalCount()-1));
			art.setNo(no);
			memberService.updateArt(art, null);
		}

		// DB입력 성공/실패 여부 확인 후 JSON으로 바꿔주고
		// 다시 페이지로 전달
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("result", result);
		response.getWriter().println(jsonObj);
	}

	@RequestMapping("lecturePayForm.do") // 결제 폼 요청
	public String lecturePayForm(@RequestParam int no, @RequestParam int payMethod, HttpSession session, Model model)
			throws ParseException {

		String id = (String) session.getAttribute("id"); // 세션에서 id 가져오기

		// 넘겨온 no 로 해당 강의 정보 출력
		lecture lecture = new lecture();
		lecture = lectureService.selectOneLecture(no);

		// member 객체 생성하여 주문자 정보 가져오기
		member member = new member();
		member = memberService.selectOneMember(id);

		// 주문 번호 생성
		// 고유한 값이 필요하기에 생성
		// 주문품목명 + 구매자 id + 구입한 시간
		String orderNumber = "lecture" + no + "_" + id + "_" + new Date().getTime();

		// 각 정보들 모델에 담아서 결제 폼으로 이동!
		model.addAttribute(member);
		model.addAttribute(lecture);
		model.addAttribute("orderNumber", orderNumber);
		model.addAttribute("payMethod", payMethod);

		return "lecturePayForm";
	}

	@RequestMapping("lecturePay.do") // 결제 액션 실행
	public void lecturePay(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 카드 결제 시 수행되는 메소드
		// 결제 성공 시에 해당 정보를 받아 pay 테이블에 삽입하기 위함

		pay pay = new pay();
		int no = Integer.parseInt(request.getParameter("no"));
		pay.setNo(no);
		pay.setId(request.getParameter("customer_uid"));
		// int isCheck = Integer.parseInt(request.getParameter("isCheck"));
		// pay.setIsCheck(isCheck);
		pay.setIsCheck(1);
		pay.setAddr(request.getParameter("buyer_addr"));
		pay.setPhone(request.getParameter("buyer_tel"));
		pay.setName(request.getParameter("buyer_name"));
		int amount = Integer.parseInt(request.getParameter("amount"));
		pay.setTotalPrice(amount);
		pay.setPayMethod(1);
		pay.setState(1);
		pay.setOrderNumber(request.getParameter("merchant_uid"));

		int result = memberService.insertLecturePay(pay);
		if(result==1) {	
			memberService.updateApproveLec(no,6);
		}
		// DB입력 성공/실패 여부 확인 후 JSON으로 바꿔주고
		// 다시 페이지로 전달
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("result", result);
		response.getWriter().println(jsonObj);
	}

	@RequestMapping("lectureForm.do") // lecture 게시판으로 이동
	public ModelAndView lectureForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("recentLectureA", lectureService.selectRecentLectureA());
		mav.addObject("recentLectureG", lectureService.selectRecentLectureG());
		mav.addObject("viewChoice", 1);
		mav.setViewName("lectureForm");
		return mav;
	}

	@RequestMapping("allLectureA.do")
	public ModelAndView allLectureA(@RequestParam(defaultValue = "1") int page) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> result = new HashMap<>();
		result = lectureService.selectAllLectureA(page);
		mav.addAllObjects(result);
		mav.addObject("viewChoice", 3);
		mav.addObject("who", "A");
		mav.setViewName("lectureForm");
		return mav;
	}

	@RequestMapping("allLectureG.do")
	public ModelAndView allLectureG(@RequestParam(defaultValue = "1") int page) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> result = new HashMap<>();
		result = lectureService.selectAllLectureG(page);
		mav.addAllObjects(result);
		mav.addObject("viewChoice", 3);
		mav.addObject("who", "G");
		mav.setViewName("lectureForm");
		return mav;
	}

	@RequestMapping("searchLecture.do")
	public ModelAndView searchLecture(@RequestParam(defaultValue = "0") int type,
			@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String searchWord,
			@RequestParam(required = false) String genre) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> params = new HashMap<>();
		params.put("page", page);
		params.put("type", type);
		params.put("searchWord", searchWord);
		params.put("genre", genre);
		if (type == 1) {
			params.put("title", searchWord);
		}
		if (type == 2) {
			params.put("nickname", searchWord);
		}
		if (type == 3) {
			params.put("content", searchWord);
		}
		if (type == 4) {
			params.put("title", searchWord);
			params.put("content", searchWord);
		}
		HashMap<String, Object> result = new HashMap<>();
		result = lectureService.searchLecture(params, page);
		mav.addAllObjects(result);
		mav.addAllObjects(params);
		mav.addObject("viewChoice", 2);
		mav.setViewName("lectureForm");
		return mav;
	}

	@RequestMapping("addLectureForm.do") // 강의 등록 폼 요청
	public void addLectureForm(HttpSession session, Model model) {
		String id = (String) session.getAttribute("id");
		member member = new member();
		member = memberService.selectOneMember(id);

		List<member> artistList = new ArrayList<member>();

		artistList = memberService.searchMember(null, null, 2);

		if (member.getIsCheck() == 3)
			model.addAttribute("artistList", artistList);

		model.addAttribute("isCheck", member.getIsCheck());
	}

	@RequestMapping("addLecture.do") // lecture 테이블에 insert 요청
	public String addLecture(HttpServletRequest request, @RequestParam MultipartFile ufile, HttpSession session) {

		String id = (String) session.getAttribute("id");
		lecture lecture = new lecture();
		if (request.getParameter("artistID").isEmpty()) {
			lecture.setState(1);
			lecture.setArtistID(id);
			lecture.setGuestID(null);
		} else {
			lecture.setState(3);
			lecture.setArtistID(request.getParameter("artistID"));
			lecture.setGuestID(id);
		}

		lecture.setNumberPeople(0);
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd"); // String으로 넘어오기 때문에 형 변환!
		lecture.setTitle(request.getParameter("title"));
		lecture.setPlace(request.getParameter("place"));
		lecture.setGenre(request.getParameter("genre"));
		try {
			// String으로 넘어오기 때문에 형 변환!
			Date date1;
			date1 = dateformat.parse((request.getParameter("startDate")));
			lecture.setStartDate(date1);
			Date date2 = dateformat.parse((request.getParameter("endDate"))); // String으로 넘어오기 때문에 형 변환!
			lecture.setEndDate(date2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int num = Integer.parseInt(request.getParameter("maxPeople")); // String으로 넘어오기 때문에 형 변환!
		lecture.setMaxPeople(num);
		lecture.setContent(request.getParameter("content"));
		int price = Integer.parseInt(request.getParameter("price"));
		lecture.setPrice(price);
		lectureService.insertLecture(lecture, ufile);

			//		알림 소스 (09.10 수정) 
			HashMap<String, Object> params = new HashMap<>();
			params.put("id", id);
			List<String> followerList = artistDao.selectFollower(params);
			for (String str : followerList) {			// following하는 아티스트가 글 작성시, follower들에게 알림 보내기
				mainService.insertAlarm("writeLecture", str, id);
			}

		return "redirect:myLectureFormA0.do";
	}

	@RequestMapping("selectOneLecture.do") // 강의 상세페이지로 이동
	public String selectOneLecture(@RequestParam int no, Model model, HttpSession session,
			@RequestParam(required = false) Integer msg) {
		lecture lecture = lectureService.selectOneLecture(no);
		model.addAttribute(lecture);
		if (msg != null) {
			model.addAttribute("msg", msg);
		}
		return "lectureDetail";
	}

	@RequestMapping("lectureComment.do")
	public void lectureComment(HttpServletResponse response, HttpServletRequest request, HttpSession session)
			throws IOException {
		response.setCharacterEncoding("UTF-8");

		int no = Integer.parseInt(request.getParameter("no"));
		int childNode = Integer.parseInt(request.getParameter("childNode"));
		int groupNo = Integer.parseInt(request.getParameter("groupNo"));

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("comment", "lectureComment");
		params.put("no", no);

		if (groupNo == 0) {
			groupNo = artService.getMaxGroupNo(params) + 1;
		} else {
			groupNo = Integer.parseInt(request.getParameter("groupNo"));
		}
		String id = (String) session.getAttribute("id");

		lectureComment lectureComment = new lectureComment();
		lectureComment lectureComment1 = new lectureComment();

		lectureComment.setNo(no);
		lectureComment.setId(id);
		lectureComment.setContent(request.getParameter("content"));
		lectureComment.setGroupNo(groupNo);
		lectureComment.setChildNode(childNode);

		lectureService.insertLectureComment(lectureComment);

		lectureComment1 = lectureService.selectLectureLatestcomment();

		JSONArray jsonArray = JSONArray.fromObject(lectureComment1);

		response.getWriter().print(jsonArray);
	}

	@RequestMapping("updateLectureForm.do")
	public void updateLectureForm(HttpSession session, @RequestParam int no, Model model) {
		String id = (String) session.getAttribute("id");

		lecture lecture = new lecture();
		lecture = lectureService.selectOneLecture(no);

		model.addAttribute("lecture", lecture);
		model.addAttribute("currentId", id);
	}

	@RequestMapping("updateLecture.do")
	public String updateLecture(HttpServletRequest request, HttpSession session, @RequestParam MultipartFile ufile) {
		String id = (String) session.getAttribute("id");
		member member = new member();
		member = memberService.selectOneMember(id);

		int no = Integer.parseInt(request.getParameter("no"));
		lecture lectureOrigin = new lecture();
		lectureOrigin = lectureService.selectOneLecture(no);
		int state = 0;
		int numberPeople = 0;
		if (lectureOrigin.getArtistID().equals(request.getParameter("artistID"))) {
			state = Integer.parseInt(request.getParameter("state"));
			numberPeople = Integer.parseInt(request.getParameter("numberPeople"));
		} else {
			state = 3;
			numberPeople = 0;
		}
		lecture lecture = new lecture();
		lecture.setNo(no);
		lecture.setState(state);
		lecture.setArtistID(request.getParameter("artistID"));
		lecture.setGuestID(id);
		lecture.setNumberPeople(numberPeople);
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd"); // String으로 넘어오기 때문에 형 변환!
		lecture.setTitle(request.getParameter("title"));
		lecture.setPlace(request.getParameter("place"));
		lecture.setGenre(request.getParameter("genre"));
		try {
			// String으로 넘어오기 때문에 형 변환!
			Date date1;
			date1 = dateformat.parse((request.getParameter("startDate")));
			lecture.setStartDate(date1);
			Date date2 = dateformat.parse((request.getParameter("endDate"))); // String으로 넘어오기 때문에 형 변환!
			lecture.setEndDate(date2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int num = Integer.parseInt(request.getParameter("maxPeople")); // String으로 넘어오기 때문에 형 변환!
		lecture.setMaxPeople(num);
		lecture.setContent(request.getParameter("content"));
		int result = lectureService.updateLecture(lecture, ufile);

		if (result == 1) {
			if (member.getIsCheck() == 2) return "redirect:myLectureFormA0.do";
			else return "redirect:myLectureFormG0.do";
		}
		else return "redirect:selectOneLecture.do?no=" + no;
	}

	@RequestMapping("deleteLecture.do")
	public String deleteLecture(@RequestParam int no, HttpSession session) {
		String id = (String) session.getAttribute("id");
		String url;

		lecture lecture = new lecture();
		lecture = lectureService.selectOneLecture(no);

		if (lecture.getArtistID().equals(lecture.getGuestID()))
			url = "redirect:myLectureFormA0.do";
		else
			url = "redirect:myLectureFormA0.do";

		int result = lectureService.deleteLecture(no);

		if (result == 1) {
			lectureService.deleteLectureComment(no);
			return url;
		} else
			return "redirect:selectOneLecture.do?no=" + no;

	}

	@RequestMapping("lectureAttendForm.do")
	public void lectureAttendForm() {
	}

	@RequestMapping("lectureAttend.do")
	public ModelAndView lectureAttend(int no, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		String id = (String) session.getAttribute("id");
		mav.addObject("no", no);
		mav.setViewName("redirect:selectOneLecture.do");
		List<attendants> attList = new ArrayList<>();
		attList = lectureService.selectAttendants(no);
		int dif = 0;
		for (int i = 0; i < attList.size(); i++) {
			if (attList.get(i).getId().equals(id)) {
				dif = 1;
				break;
			}
		}
		if (dif == 1) { // 이미 신청한 강의일 경우
			mav.addObject("msg", 1);
			return mav;
		} else { // 신청 완료
			mav.addObject("msg", 0);
			lectureService.updateLecturePeople(no, id);
			lectureService.insertAttendants(no, id);
			return mav;
		}
	}

	@RequestMapping("boardForm.do") // 자유게시판 이동
	public void boardForm(Model model) {
		List<board> boardList = new ArrayList<board>();
		boardList = boardService.selectBoard();

		model.addAttribute("boardList", boardList);
	}

	@RequestMapping("searchBoard.do")
	public void searchBoard() {

	}

	@RequestMapping("writeBoardForm.do") // 자유게시판 글쓰기 이동
	public void writeBoardForm() {
	}

	@RequestMapping("writeBoard.do")
	public String writeBoard(HttpServletRequest request, HttpSession session) {
		// 입력된 데이터들을 art 객체로 받아서 테이블에 insert
		// 아이디 체크 후 함수 호출
		// 관리자 : boardService.selectNotice
		// 게스트 : boardService.selectBoard

		String id = (String) session.getAttribute("id");

		board board = new board();
		board.setTitle(request.getParameter("title"));
		board.setContent(request.getParameter("content"));
		board.setId(id);
		boardService.insertBoard(board);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("table", "board");
		params.put("id", board.getId());
		int num = boardService.selectOneById(params);
		return "redirect:selectOneBoard.do?no=" + num;
	}

	@RequestMapping("selectOneBoard.do") // 자유게시글 상세페이지
	public String selectOneBoard(@RequestParam int no, Model model, HttpSession session) {
		String id = (String) session.getAttribute("id");
		board board = new board();
		board = boardService.selectOneBoard(no);

		model.addAttribute("board", board);
		model.addAttribute("currentId", id);
		return "boardDetail";
	}

	@RequestMapping("updateBoardForm.do")
	public void updateBoardForm() {
	}

	@RequestMapping("updateBoard.do")
	public void updateBoard() {
	}

	@RequestMapping("deleteBoard.do")
	public String deleteBoard(@RequestParam int no) {
		boardService.deleteBoard(no);
		boardService.deleteBoardComment(no);
		return "redirect:boardForm.do";
	}

	@RequestMapping("boardComment.do")
	public void boardComment(HttpServletResponse response, HttpServletRequest request, HttpSession session)
			throws IOException {
		response.setCharacterEncoding("UTF-8");

		int no = Integer.parseInt(request.getParameter("no"));
		int childNode = Integer.parseInt(request.getParameter("childNode"));
		int groupNo = Integer.parseInt(request.getParameter("groupNo"));

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("comment", "boardComment");
		params.put("no", no);

		if (groupNo == 0) {
			groupNo = artService.getMaxGroupNo(params) + 1;
		} else {
			groupNo = Integer.parseInt(request.getParameter("groupNo"));
		}
		String id = (String) session.getAttribute("id");

		boardComment boardComment = new boardComment();
		boardComment boardComment1 = new boardComment();

		boardComment.setNo(no);
		boardComment.setId(id);
		boardComment.setContent(request.getParameter("content"));
		boardComment.setGroupNo(groupNo);
		boardComment.setChildNode(childNode);

		boardService.insertBoardComment(boardComment);

		boardComment1 = boardService.selectBoardLatestcomment();

		JSONArray jsonArray = JSONArray.fromObject(boardComment1);

		response.getWriter().print(jsonArray);
	}

	@RequestMapping("qnaForm.do") // QnA게시판 이동
	public void qnaForm(Model model, HttpSession session) {
		String id = (String) session.getAttribute("id");
		List<qna> qnaList = new ArrayList<qna>();
		qnaList = boardService.selectQna();
		model.addAttribute("qnaList", qnaList);
		model.addAttribute("currentId", id);
	}

	@RequestMapping("selectOneQna.do") // qna 상세페이지 이동
	public String selectOneQna(@RequestParam int no, Model model, HttpSession session) {

		String id = (String) session.getAttribute("id");
		qna qna = new qna();
		qna = boardService.selectOneQna(no);
		model.addAttribute("qna", qna);
		model.addAttribute("currentId", id);
		return "qnaDetail";
	}

	@RequestMapping("searchQna.do")
	public void searchQna(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {

		String id = (String) session.getAttribute("id");

		if (id.equals("admin"))
			response.getWriter().print(1);
		else {
			qna qna = new qna();

			int no = Integer.parseInt(request.getParameter("no"));

			qna = boardService.selectOneQna(no);

			if (qna.getPw().equals(request.getParameter("pw"))) {
				response.getWriter().print(1);
			} else
				response.getWriter().print(0);
		}

	}

	@RequestMapping("writeQnaForm.do") // QnA게시판 글쓰기 이동
	public void writeQnaForm() {
	}

	@RequestMapping("writeQna.do")
	public String writeQna(HttpServletRequest request, HttpSession session) {
		qna qna = new qna();
		String id = (String) session.getAttribute("id");

		qna.setTitle(request.getParameter("title"));
		qna.setId(id);
		qna.setContent(request.getParameter("content"));
		qna.setPw(request.getParameter("pw"));
		boardService.insertQna(qna);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("table", "qna");
		params.put("id", qna.getId());
		int num = boardService.selectOneById(params);
		return "redirect:selectOneQna.do?no=" + num;
	}

	@RequestMapping("updateQnaForm.do")
	public void updateQnaForm() {
	}

	@RequestMapping("updateQna.do")
	public void updateQna() {
	}

	@RequestMapping("deleteQna.do")
	public String deleteQna(@RequestParam int no) {
		boardService.deleteQna(no);
		boardService.deleteQnaComment(no);
		return "redirect:qnaForm.do";
	}

	@RequestMapping("qnaComment.do")
	public void qnaComment(HttpServletResponse response, HttpServletRequest request, HttpSession session)
			throws IOException {
		response.setCharacterEncoding("UTF-8");

		int no = Integer.parseInt(request.getParameter("no"));
		int childNode = Integer.parseInt(request.getParameter("childNode"));
		int groupNo = Integer.parseInt(request.getParameter("groupNo"));

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("comment", "qnaComment");
		params.put("no", no);

		if (groupNo == 0) {
			groupNo = artService.getMaxGroupNo(params) + 1;
		} else {
			groupNo = Integer.parseInt(request.getParameter("groupNo"));
		}
		String id = (String) session.getAttribute("id");

		qnaComment qnaComment = new qnaComment();
		qnaComment qnaComment1 = new qnaComment();

		qnaComment.setNo(no);
		qnaComment.setId(id);
		qnaComment.setContent(request.getParameter("content"));
		qnaComment.setGroupNo(groupNo);
		qnaComment.setChildNode(childNode);

		boardService.insertQnaComment(qnaComment);

		qnaComment1 = boardService.selectQnaLatestcomment();

		JSONArray jsonArray = JSONArray.fromObject(qnaComment1);

		response.getWriter().print(jsonArray);
	}

	// 추가 매핑///////////////////////////////////////////////////////////
	@RequestMapping("selectArtComment.do")
	public void selectArtComment(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		response.setCharacterEncoding("UTF-8");

		int no = Integer.parseInt(request.getParameter("no"));
		int skip = Integer.parseInt(request.getParameter("skip"));
		int startComment = Integer.parseInt(request.getParameter("startComment"));

		HashMap<String, Object> params = new HashMap<String, Object>();

		params.put("no", no);
		params.put("skip", skip + 1);
		params.put("startComment", startComment);

		List array = new ArrayList<artComment>();
		array = artService.selectArtComment(params);

		JSONArray jsonArray = JSONArray.fromObject(array);
		response.getWriter().println(jsonArray);
	}

	@RequestMapping("selectLectureComment.do")
	public void selectLectureComment(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		response.setCharacterEncoding("UTF-8");

		int no = Integer.parseInt(request.getParameter("no"));
		int skip = Integer.parseInt(request.getParameter("skip"));
		int startComment = Integer.parseInt(request.getParameter("startComment"));

		HashMap<String, Object> params = new HashMap<String, Object>();

		params.put("no", no);
		params.put("skip", skip + 1);
		params.put("startComment", startComment);

		List array = new ArrayList<lectureComment>();
		array = lectureService.selectLectureComment(params);

		JSONArray jsonArray = JSONArray.fromObject(array);
		response.getWriter().println(jsonArray);
	}

	@RequestMapping("selectBoardComment.do")
	public void selectBoardComment(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		response.setCharacterEncoding("UTF-8");

		int no = Integer.parseInt(request.getParameter("no"));
		int skip = Integer.parseInt(request.getParameter("skip"));
		int startComment = Integer.parseInt(request.getParameter("startComment"));

		HashMap<String, Object> params = new HashMap<String, Object>();

		params.put("no", no);
		params.put("skip", skip + 1);
		params.put("startComment", startComment);
		List array = new ArrayList<boardComment>();
		array = boardService.selectBoardComment(params);

		JSONArray jsonArray = JSONArray.fromObject(array);
		response.getWriter().println(jsonArray);
	}

	@RequestMapping("selectQnaComment.do")
	public void selectQnaComment(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		response.setCharacterEncoding("UTF-8");

		int no = Integer.parseInt(request.getParameter("no"));
		int skip = Integer.parseInt(request.getParameter("skip"));
		int startComment = Integer.parseInt(request.getParameter("startComment"));

		HashMap<String, Object> params = new HashMap<String, Object>();

		params.put("no", no);
		params.put("skip", skip + 1);
		params.put("startComment", startComment);

		List array = new ArrayList<qnaComment>();
		array = boardService.selectQnaComment(params);

		JSONArray jsonArray = JSONArray.fromObject(array);
		response.getWriter().println(jsonArray);
	}

	@RequestMapping("deleteComment.do")
	public void deleteComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");

		int no = Integer.parseInt(request.getParameter("no"));
		int commentNo = Integer.parseInt(request.getParameter("commentNo"));
		String text = request.getParameter("text") + "Comment";

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("no", no);
		params.put("content", "삭제된 댓글입니다.");
		params.put("commentNo", commentNo);
		params.put("comment", text);

		artService.deleteComment(params);
		response.getWriter().println("success");
	}

	@RequestMapping("modifyComment.do")
	public void modifyComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");

		int no = Integer.parseInt(request.getParameter("no"));
		int commentNo = Integer.parseInt(request.getParameter("commentNo"));
		String text = request.getParameter("text") + "Comment";

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("no", no);
		params.put("content", request.getParameter("context"));
		params.put("commentNo", commentNo);
		params.put("comment", text);

		artService.updateComment(params);

		response.getWriter().println("success");
	}

	@RequestMapping("payResult.do")
	public ModelAndView payResult(int no, String orderNumber, int dif) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("pay", memberService.selectOnePay(orderNumber));
		if (dif == 1) { // art정보 요청시
			mav.addObject("art", artService.selectOneArt(no));
		}
		if (dif == 2) { // lecture정보 요청시
			mav.addObject("lecture", lectureService.selectOneLecture(no));
		}
		mav.addObject("dif", dif);
		mav.setViewName("payResult");
		return mav;
	}

}// public class의 끝.
