package Controller;

import java.io.IOException;

import java.io.PrintWriter;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
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
import Model.follow;
import Model.likes;
import Model.pay;

import Service.artService;
import Service.artistService;
import Service.mainService;
import Service.memberService;

@Controller
public class artistController {

	@Autowired
	memberService memberService;

	@Autowired
	mainService mainService; // 추가

	@Autowired
	artistService artistService; //추가
	
	@Autowired
	artistDao artistDao;
	
	@Autowired
	artService artService;

		
	//아티스트 개인 페이지 이동 (아티스트용)
	@RequestMapping("artistMyPage.do") 
	public ModelAndView artistMyPage(HttpSession session) {
		String id = (String) session.getAttribute("id");
		ModelAndView mav = new ModelAndView();
		if (memberService.selectOneMember(id).getIsCheck() == 2) {
			List<art> list = memberService.selectArtistArt(id);
			mav.addObject("artCount", list.size());
			mav.addObject("artList", list);
			mav.addObject("name", memberService.selectOneMember(id).getName());
			mav.addObject("nickname", memberService.selectOneMember(id).getNickname());
			mav.addObject("content", memberService.selectOneMember(id).getContent());
			mav.setViewName("artistMyPage");
		} else {
			mav.setViewName("main");
		}
		return mav;
	}

	@RequestMapping("myLectureFormA.do")
	public void myLectureFormA(HttpSession session, HttpServletResponse response,
			@RequestParam(defaultValue = "1") int pageA, @RequestParam(defaultValue = "1") int pageG,
			@RequestParam(defaultValue = "1") int pageR, @RequestParam(defaultValue = "1") int pageAp)
			throws ServerException, IOException {
		String id = (String) session.getAttribute("id");
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		HashMap<String, Object> params = new HashMap<>();
		params.put("pageA", pageA);
		params.put("pageG", pageG);
		params.put("pageR", pageR);
		params.put("pageAp", pageAp);
		HashMap<String, Object> resultA = new HashMap<>();
		HashMap<String, Object> resultG = new HashMap<>();
		HashMap<String, Object> resultR = new HashMap<>();
		HashMap<String, Object> resultAp = new HashMap<>();
		resultA = memberService.selectMyAttendLec(id, pageA, params); // 강의신청 map을 resultA 에 담기
		resultG = memberService.selectGatherLec(id, pageG, params); // 모집 중인 강의 map을 resultG 에 담기
		resultR = memberService.selectRequestLec(id, pageR, params); // 요청 받은 강의 map을 resultR 에 담기
		resultAp = memberService.selectApproveLec(id, pageAp, params); // 요청 수락한 강의 map을 resultAp에 담기

		JSONObject jsonObj = new JSONObject(); // map을 담을 JSONObject 생성
		jsonObj.put("attendList", resultA.get("attendList")); // 강의 신청 map을 JSONObject에 담기
		jsonObj.put("currentA", resultA.get("currentA"));
		jsonObj.put("startA", resultA.get("startA"));
		jsonObj.put("endA", resultA.get("endA"));
		jsonObj.put("lastA", resultA.get("lastA"));
		// 모집 중인 강의 map을 JSONObject에 담기
		jsonObj.put("gatherList", resultG.get("gatherList"));
		jsonObj.put("currentG", resultG.get("currentG"));
		jsonObj.put("startG", resultG.get("startG"));
		jsonObj.put("endG", resultG.get("endG"));
		jsonObj.put("lastG", resultG.get("lastG"));
		// 요청 받은 강의 map을 JSONObject에 담기
		jsonObj.put("requestList", resultR.get("requestList"));
		jsonObj.put("currentR", resultR.get("currentR"));
		jsonObj.put("startR", resultR.get("startR"));
		jsonObj.put("endR", resultR.get("endR"));
		jsonObj.put("lastR", resultR.get("lastR"));
		// 요청 수락한 강의 map을 JSONObject에 담기
		jsonObj.put("approveList", resultAp.get("approveList"));
		jsonObj.put("currentAp", resultAp.get("currentAp"));
		jsonObj.put("startAp", resultAp.get("startAp"));
		jsonObj.put("endAp", resultAp.get("endAp"));
		jsonObj.put("lastAp", resultAp.get("lastAp"));
		pw.println(jsonObj); // printWriter로 뷰에 JSONObject 보내기.
	}

	@RequestMapping("mySellFormA.do")
	public void mySellFormA(HttpSession session, HttpServletResponse response,
			@RequestParam(defaultValue = "1") int pageBuy, @RequestParam(defaultValue = "1") int pageSell,
			@RequestParam(defaultValue = "1") int pageSold) throws ServletException, IOException {
		String id = (String) session.getAttribute("id");
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();

		HashMap<String, Object> params = new HashMap<>();
		params.put("pageBuy", pageBuy);
		params.put("pageSell", pageSell);
		params.put("pageSold", pageSold);
		HashMap<String, Object> resultBuy = new HashMap<>();
		HashMap<String, Object> resultSell = new HashMap<>();
		HashMap<String, Object> resultSold = new HashMap<>();
		resultBuy = memberService.selectBuyingArt(id, pageBuy, params);
		resultSell = memberService.selectSellingArt(id, pageSell, params);
		resultSold = memberService.selectSoldArt(id, pageSold, params);

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("buyingList", resultBuy.get("buyingList")); // 구매내역 map을 JSONObject에 담기
		jsonObj.put("currentBuy", resultBuy.get("currentBuy"));
		jsonObj.put("startBuy", resultBuy.get("startBuy"));
		jsonObj.put("endBuy", resultBuy.get("endBuy"));
		jsonObj.put("lastBuy", resultBuy.get("lastBuy"));

		jsonObj.put("sellingList", resultSell.get("sellingList")); // 판매 중인 작품 map을 JSONObject에 담기
		jsonObj.put("currentSell", resultSell.get("currentSell"));
		jsonObj.put("startSell", resultSell.get("startSell"));
		jsonObj.put("endSell", resultSell.get("endSell"));
		jsonObj.put("lastSell", resultSell.get("lastSell"));

		jsonObj.put("soldList", resultSold.get("soldList")); // 판매된 map을 JSONObject에 담기
		jsonObj.put("currentSold", resultSold.get("currentSold"));
		jsonObj.put("startSold", resultSold.get("startSold"));
		jsonObj.put("endSold", resultSold.get("endSold"));
		jsonObj.put("lastSold", resultSold.get("lastSold"));
		pw.println(jsonObj);
	}

	@RequestMapping("updateDelivery.do")
	public String updateDelivery(String orderNumber, int state) {
		memberService.updateDelivery(orderNumber, state);
		return "redirect:mySellFormA0.do";
	}

	// 작품 등록 글 폼 요청
	@RequestMapping("writeArtForm.do")
	public void writeArtForm() {
	}

	// 작품 등록 요청
	@RequestMapping("writeArt.do")
	public String wrtieArt(HttpServletRequest request, @RequestParam("ufile") MultipartFile ufile,
			HttpSession session) {
		// 입력된 데이터들을 art 객체로 받아서 테이블에 insert
		art art = new art();
		int isCheck = Integer.parseInt(request.getParameter("isCheck"));
		art.setIsCheck(isCheck);
		String id = (String) session.getAttribute("id");
		art.setId(id);
		art.setContent(request.getParameter("content"));

		if (isCheck == 1) {
			art.setTitle(request.getParameter("title"));
			int state = 0;
			art.setGenre(request.getParameter("genre"));
			int sellCheck = Integer.parseInt(request.getParameter("sellCheck"));
			art.setSellCheck(sellCheck);
			if (sellCheck == 1) {
				int price = Integer.parseInt(request.getParameter("price"));
				art.setPrice(price);
				art.setTotalCount(Integer.parseInt(request.getParameter("totalCount")));
				if (sellCheck == 0)
					state = 2;
				else
					state = 1;
				art.setState(state);
			}
		}
		else {
			art.setTitle(request.getParameter("boardTitle"));
		}
		memberService.insertArt(art, ufile);

		//	알림 소스 (09.10 수정) 
		HashMap<String, Object> params = new HashMap<>();
		params.put("id", id);
		List<String> followerList = artistDao.selectFollower(params);
		for (String str : followerList) {			// following하는 아티스트가 글 작성시, follower들에게 알림 보내기
			mainService.insertAlarm("writeArt", str, id);
		}
		return "redirect:artistMyPage.do";
	}

	@RequestMapping("insertFollow.do")
	public String insertFollow(HttpSession session, String artistID) {
		String id = (String) session.getAttribute("id");
		follow follow = new follow();

		follow.setFollowing(artistID);
		follow.setId(id);

		artistService.insertFollow(follow);

		follow.setFollower(id);
		follow.setFollowing(null);
		follow.setId(artistID);

		artistService.insertFollow(follow);
		
		return "redirect:artistPage.do?id=" + artistID;
	}

	@RequestMapping("deleteFollow.do")
	public String deleteFollow(HttpSession session, String artistID) {
		String id = (String) session.getAttribute("id");
		follow follow = new follow();

		follow.setFollowing(artistID);
		follow.setId(id);

		artistService.deleteFollow(follow);
		
		return "redirect:artistPage.do?id=" + artistID;
	}
	
	@RequestMapping("followerList.do") 
	public String followerList(HttpServletRequest req, HttpServletResponse resp, String follower, Model model) 
			throws IOException {
		if(req.getParameter("page") == null) {
			model.addAttribute("follower", follower);
			
			return "followerList";
		}

		JSONObject jsonObject = new JSONObject();
		int page = Integer.parseInt(req.getParameter("page"));
		String id = req.getParameter("follower");
		HashMap<String, Object> params = new HashMap<>();
		
		params.put("id", id);
		
		jsonObject.put("follower", artistService.selectFollower(params, page));
		
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter pw = resp.getWriter();
		pw.println(jsonObject);
		
		return null;
	}
	
	@RequestMapping("followingList.do")
	   public String followingList(HttpServletRequest req, HttpServletResponse resp, String following, Model model) 
	         throws IOException {
	      if(req.getParameter("page") == null) {
	         model.addAttribute("following", following);
	         
	         return "followingList";
	      }

	      JSONObject jsonObject = new JSONObject();
	      int page = Integer.parseInt(req.getParameter("page"));
	      String id = req.getParameter("following");
	      HashMap<String, Object> params = new HashMap<>();
	      
	      params.put("id", id);
	      
	      jsonObject.put("following", artistService.selectFollowing(params, page));
	      
	      resp.setContentType("text/html; charset=UTF-8");
	      PrintWriter pw = resp.getWriter();
	      pw.println(jsonObject);
	      
	      return null;
	}

	@RequestMapping("insertLikes.do") 
	public String insertLikes(HttpSession session, int no, int isCheck) {
		String id = (String) session.getAttribute("id");
		likes likes = new likes();

		likes.setNo(no);
		likes.setId(id);
		likes.setIsCheck(isCheck);

		artistService.insertLikes(likes);
		
		return "redirect:selectOneArt.do?no=" + no;
	}

	@RequestMapping("likesList.do") 
	public ModelAndView likesList(String likesID, String check) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> params = new HashMap<>();
		
		params.put("id", likesID);
		
		if(check != null) {
			params.put("check", check);
		}
		
		mav.addObject("likesID", likesID);
		mav.addObject("list", artistService.selectLikesArt(params));
		mav.setViewName("likesList");

		return mav;
	}

	@RequestMapping("deleteLikes.do") 
	public String deleteLikes(HttpSession session, int no, int isCheck) {
		String id = (String) session.getAttribute("id");
		likes likes = new likes();

		likes.setNo(no);
		likes.setId(id);
		likes.setIsCheck(isCheck);

		artistService.deleteLikesArt(likes);
		
		return "redirect:selectOneArt.do?no=" + no;
	}

	@RequestMapping("updateArtForm.do") 
	public ModelAndView updateArtForm(int no) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("art", artService.selectOneArt(no));
		mav.setViewName("updateArtForm");
		return mav;
	}
	
	@RequestMapping("updateArt.do") 
	public String updateArt(HttpServletRequest req, @RequestParam("ufile")MultipartFile ufile, HttpSession session) {
		// 입력된 데이터를 art 객체로 받아서 테이블에 update
		art art = new art();
		int no = Integer.parseInt(req.getParameter("no"));
		art.setNo(no);
		int isCheck = Integer.parseInt(req.getParameter("isCheck"));
		art.setIsCheck(isCheck);
		String id = (String) session.getAttribute("id");
		art.setId(id);
		art.setContent(req.getParameter("content"));
		
		if (isCheck ==1) {
			art.setTitle(req.getParameter("title"));
			int state = 2;
			art.setGenre(req.getParameter("genre"));
			int sellCheck = Integer.parseInt(req.getParameter("sellCheck"));
			art.setSellCheck(sellCheck);
			if (sellCheck == 1) {
				art.setPrice(Integer.parseInt(req.getParameter("price")));
				int totalCount = Integer.parseInt(req.getParameter("totalCount"));
				art.setTotalCount(totalCount);
					state = 1;
				if (totalCount==0) {
					state = 0;
				}
			}
			art.setState(state);
		}
		else {
			art.setTitle(req.getParameter("boardTitle"));
			art.setSellCheck(-1);			// java에서 int형의 default값을 '0'으로 넘기는 것을 피하기 위해 '-1' 임의 세팅
			art.setPrice(-1);
			art.setState(-1);
			art.setTotalCount(-1);
		}
		memberService.updateArt(art, ufile);
		return "redirect:selectOneArt.do?no="+no;
	}

	@RequestMapping("deleteArt.do")
	public String deleteArt(@RequestParam int no, HttpSession session) {
		String id = (String)session.getAttribute("id");
		List<pay> payList = new ArrayList<pay>();

		HashMap<String, Integer> params = new HashMap<String, Integer>();
		params.put("no", no);
		params.put("isCheck", 0);

		payList = memberService.selectPayByNo(params);

		if (payList != null) {
			for (int i = 0; i < payList.size(); i++) {
				if (payList.get(i).getIsCheck() != 5) {
					return "redirect:selectOneArt.do?no=" + no + "&deleteText=1";
				}
			}
		}
		
		int result = memberService.deleteArt(no, id);

		if(result==1) {
			artService.deleteArtComment(no);
			return "redirect:artForm.do";
		}
		else return "redirect:selectOneArt.do?no=" + no;
	}
	
	@RequestMapping("searchID.do") 
	public void searchID() {}
	
  //아티스트 개인 페이지 이동 (사용자용)
  		@RequestMapping("artistPage.do")
  		public ModelAndView artistPage(String id) {
  			ModelAndView mav = new ModelAndView();
  			List<art> list = memberService.selectArtistArt(id);
  			HashMap<String, Object> params = new HashMap<>();
  			
  			params.put("id", id);
  			
  			mav.addObject("artCount", list.size());
  			mav.addObject("artList", list);
  			mav.addObject("artistID", id);
  			mav.addObject("name", memberService.selectOneMember(id).getName());
  			mav.addObject("nickname", memberService.selectOneMember(id).getNickname());
  			mav.addObject("content", memberService.selectOneMember(id).getContent());
  			mav.addObject("followCheck", artistDao.selectFollower(params));
  			mav.setViewName("artistPage");
  			return mav;
  		}

	@RequestMapping("mySellFormA0.do")
	public String mySellFormA0(HttpSession session) {
		String id = (String) session.getAttribute("id");
		if (memberService.selectOneMember(id).getIsCheck() == 2) {
			return "mySellFormA";
		} else {
			return "myOrderFormG";
		}

	}

	@RequestMapping("myLectureFormA0.do")
	public String myLectureFormA0(HttpSession session) {
		String id = (String) session.getAttribute("id");
		if (memberService.selectOneMember(id).getIsCheck() == 2) {
			return "myLectureFormA";
		} else {
			return "myLectureFormG";
		}
	}

	@RequestMapping("updateApproveLec.do")
	public String updateApproveLec(int no, int state) {
		memberService.updateApproveLec(no, state);
		return "redirect:myLectureFormA0.do";
	}
	
	@RequestMapping("followerCount")
	public void followerCount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String id = req.getParameter("id");
		JSONObject jsonObject = new JSONObject();
		HashMap<String, Object> params = new HashMap<>();
		
		params.put("id", id);
		
		jsonObject.put("followerCount", artistDao.getFollowerCount(params));
		
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter pw = resp.getWriter();
		pw.println(jsonObject);
	}
	
	@RequestMapping("followingCount")
	public void followingCount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String id = req.getParameter("id");
		JSONObject jsonObject = new JSONObject();
		HashMap<String, Object> params = new HashMap<>();
		
		params.put("id", id);
		
		jsonObject.put("followingCount", artistDao.getFollowingCount(params));
		
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter pw = resp.getWriter();
		pw.println(jsonObject);
	}

} // public class의 끝
