package Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import Controller.MailHandler;
import Controller.TempKey;
import Dao.artDao;
import Dao.lectureDao;
import Dao.memberDao;
import Model.art;
import Model.lecture;
import Model.member;
import Model.pay;

@Service
public class memberServiceImpl implements memberService {

	@Autowired
	private memberDao memberDao;

	@Autowired
	private artDao artDao;
	
	@Autowired
	private lectureDao lectureDao;
	
	@Autowired
	private mainService mainService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Override
	public member selectOneMember(String id) {
		return memberDao.selectOneMember(id);
	}

	@Override
	public member selectOneNickname(String nickname) {
		// TODO Auto-generated method stub
		return memberDao.selectOneNickname(nickname);
	}
	
	@Override
	public int login(String id, String pw) {
		// TODO Auto-generated method stub
		member member = memberDao.selectOneMember(id);
		
		if(member != null) {
			if(pw.equals(member.getPw())) {
				if(member.getAuthStatus() == 1) {
					return 1;
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	@Transactional
	@Override
	public int insertMember(member member, MultipartFile Profile, MultipartFile File) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("서비스80");
		String path = "C:/Project/Project/image/";
		File dir = new File(path);
	
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		if(Profile != null) {
			String profileName = Profile.getOriginalFilename();
			File attachProfile = new File(path + profileName);
			try {
				Profile.transferTo(attachProfile);
			} catch (IllegalStateException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			member.setProfile(profileName);
		}
		if(File != null) {
			String fileName = File.getOriginalFilename();
			File attachFile = new File(path + fileName);
			try {
				File.transferTo(attachFile);
			} catch (IllegalStateException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			member.setFile(fileName);
		}
		System.out.println(member.getBirth());
		if (member.getId() != "" && member.getPw() != "" && member.getName() != "" && member.getName() != ""
				&& member.getPhone() != "" && member.getAddr() != "" && member.getEmail() != ""
				&& member.getNickname() != "" && member.getBirth() != null) {
			String key = new TempKey().getKey(50, false); // 인증키 생성
			
			member.setAuthCode(key);
			memberDao.insertMember(member);

			MailHandler sendMail = new MailHandler(mailSender);
			sendMail.setSubject("[작업실 서비스 이메일 인증]");
			sendMail.setText(
					new StringBuffer().append("<h1>메일인증</h1>").append("<a href='http://localhost:8080/Project/emailConfirm.do?email=").append(member.getEmail()).append("&key=").append(key).append("' target='_blenk'>이메일 인증 확인</a>").toString());
			sendMail.setFrom("jo27233@gmail.com", "조영규");
			sendMail.setTo(member.getEmail());
			sendMail.send();
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public List<member> searchMember(String keyWord, String searchWord, int isCheck) {
		// TODO Auto-generated method stub
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		params.put("column", keyWord);
		params.put("text", searchWord);
		params.put("isCheck", isCheck);
		
		return memberDao.searchMember(params);
	}

	@Override
	public HashMap<String, Object> searchApproveArtist(HashMap<String, Object> params, int page) {
		// TODO Auto-generated method stub
		HashMap<String, Object> result = new HashMap<>();
		
		result.put("current", page);
		result.put("start", mainService.getStartPage(page));
		result.put("end", mainService.getEndPage(page));
		result.put("last", mainService.getArtistLastPage(params));
		
		params.put("skip", mainService.getSkip(page, 10));
		params.put("qty", 10);
		result.put("memberList", memberDao.searchApproveArtist(params));
		
		return result;
	}

	@Override
	public HashMap<String, Object> selectAllMember(HashMap<String, Object> params, int page) {
		// TODO Auto-generated method stub
		HashMap<String, Object> result = new HashMap<>();
		
		result.put("current", page);
		result.put("start", mainService.getStartPage(page));
		result.put("end", mainService.getEndPage(page));
		result.put("last", mainService.getMemberLastPage(params));;
		
		params.put("skip", mainService.getSkip(page, 10));
		params.put("qty", 10);
		result.put("memberList", memberDao.selectAllMember(params));

		return result;
	}

	@Override
	public int deleteMember(String id) {
		// TODO Auto-generated method stub
		return memberDao.deleteMember(id);
	}

	@Override
	public int blackList(String id) {
		// TODO Auto-generated method stub
		return memberDao.blackList(id);
	}

	@Override
	public int updateApproveArtist(String id) {
		// TODO Auto-generated method stub
		return memberDao.updateApproveArtist(id);
	}

	@Override
	public int updateRefuseArtist(String id) {
		// TODO Auto-generated method stub
		return memberDao.updateRefuseArtist(id);
	}

	@Override
	public int updateMember(member member) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HashMap<String, Object> selectMyAttendLec(String id, int pageA, HashMap<String, Object> params) {
		HashMap<String, Object> result = new HashMap<>();
		params.put("skipA", mainService.getSkip(pageA, 5));
		params.put("qty", 5);
		params.put("id", id);
		result.put("currentA", pageA);
		result.put("startA", mainService.getStartPage(pageA));
		result.put("endA", mainService.getEndPage(pageA));
		result.put("lastA", mainService.getAttendLecLastPage(id));
		result.put("attendList", memberDao.selectMyAttendLec(params));
		return result;
	}

	@Override
	public HashMap<String, Object> selectGatherLec(String id, int pageG, HashMap<String, Object> params) {
		HashMap<String, Object> result = new HashMap<>();
		params.put("skipG", mainService.getSkip(pageG, 5));
		params.put("qty", 5);
		if (memberDao.selectOneMember(id).getIsCheck()==2) {
			params.put("artistID", id);
		}if (memberDao.selectOneMember(id).getIsCheck()==3) {
			params.put("guestID", id);
		}
		result.put("currentG", pageG);
		result.put("startG", mainService.getStartPage(pageG));
		result.put("endG", mainService.getEndPage(pageG));
		result.put("lastG", mainService.getGatherLecLastPage(id));
		result.put("gatherList", memberDao.selectGatherLec(params));
		return result;
	}
	
	@Override
	public HashMap<String, Object> selectRequestLec(String id, int pageR, HashMap<String, Object> params) {
		HashMap<String, Object> result = new HashMap<>();
		params.put("skipR", mainService.getSkip(pageR, 5));
		params.put("qty", 5);
		params.put("id", id);
		result.put("currentR", pageR);
		result.put("startR", mainService.getStartPage(pageR));
		result.put("endR", mainService.getEndPage(pageR));
		result.put("lastR", mainService.getRequestLecLastPage(id));
		result.put("requestList", memberDao.selectRequestLec(params));
		return result;
	}
	
	@Override
	public HashMap<String, Object> selectApproveLec(String id, int pageAp, HashMap<String, Object> params) {
		HashMap<String, Object> result = new HashMap<>();
		params.put("skipAp", mainService.getSkip(pageAp, 5));
		params.put("qty", 5);
		params.put("id", id);
		result.put("currentAp", pageAp);
		result.put("startAp", mainService.getStartPage(pageAp));
		result.put("endAp", mainService.getEndPage(pageAp));
		result.put("lastAp", mainService.getApproveLecLastPage(id));
		result.put("approveList", memberDao.selectApproveLec(params));
		return result;
	}
	
	@Override
	public int insertLecturePay(pay pay) {
		// TODO Auto-generated method stub
		return memberDao.insertLecturePay(pay);
	}

	@Override
	public List<art> selectArtistArt(String id) {
			return memberDao.selectArtistArt(id);	
	}

	@Override
	public int updateArt(art art, MultipartFile file) {
		
		String path = "C:/Project/Project/WebContent/resources/Thumnail/artImage/";

		File dir = new File(path);
		if(!dir.exists())
			dir.mkdirs(); //지정 경로에 폴더가 없을 시 폴더 생성 요청
		
		String fileName=new Date().getTime() + "_" + file.getOriginalFilename();
		
		File attachFile = new File(path + fileName);
		try {
			file.transferTo(attachFile);
			art.setFile(fileName);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return memberDao.updateArt(art);
	}

	@Override
	public int deleteArt(int no, String id) {
		// TODO Auto-generated method stub
		HashMap<String, Object> params =  new HashMap<String, Object>();
		params.put("no", no);
		params.put("id", id);
		return memberDao.deleteArt(params);
	}

	@Override
	public int updateDelivery(String orderNumber, int state) {
		pay pay = new pay();
		pay.setOrderNumber(orderNumber);
		pay.setState(state);
		return memberDao.updateDelivery(pay);
	}

	@Override
	public HashMap<String, Object> selectBuyingArt(String id, int page, HashMap<String, Object> params) {
			HashMap<String, Object> result = new HashMap<>();
			params.put("skipBuy", mainService.getSkip(page, 5));
			params.put("qty", 5);
			params.put("id", id);
			result.put("currentBuy", page);
			result.put("startBuy", mainService.getStartPage(page));
			result.put("endBuy", mainService.getEndPage(page));
			result.put("lastBuy", mainService.getBuyingArtLastPage(id));
			result.put("buyingList", memberDao.selectBuyingArt(params));
			return result;
	}

	@Override
	public HashMap<String, Object> selectSellingArt(String id, int page, HashMap<String, Object> params) {
		HashMap<String, Object> result = new HashMap<>();
		params.put("skipSell", mainService.getSkip(page, 5));
		params.put("qty", 5);
		params.put("id", id);
		result.put("currentSell", page);
		result.put("startSell", mainService.getStartPage(page));
		result.put("endSell", mainService.getEndPage(page));
		result.put("lastSell", mainService.getSellingArtLastPage(id));
		result.put("sellingList", memberDao.selectSellingArt(params));
		return result;
	}

	@Override
	public HashMap<String, Object> selectSoldArt(String id, int page, HashMap<String, Object> params) {
		HashMap<String, Object> result = new HashMap<>();
		params.put("skipSold", mainService.getSkip(page, 5));
		params.put("qty", 5);
		params.put("id", id);
		result.put("currentSold", page);
		result.put("startSold", mainService.getStartPage(page));
		result.put("endSold", mainService.getEndPage(page));
		result.put("lastSold", mainService.getSoldArtLastPage(id));
		result.put("soldList", memberDao.selectSoldArt(params));
		return result;
	}

	@Override
	public pay selectOnePay(String orderNumber) {
		return memberDao.selectOnePay(orderNumber);
	}

	@Override
	public int insertArt(art art, MultipartFile file) {
		// TODO Auto-generated method stub
		String path = "C:/Project/Project/WebContent/resources/Thumnail/artImage/";

		File dir = new File(path);
		if(!dir.exists())
			dir.mkdirs(); //지정 경로에 폴더가 없을 시 폴더 생성 요청
		
		String fileName=new Date().getTime() + "_" + file.getOriginalFilename();
		
		File attachFile = new File(path + fileName);
		try {
			file.transferTo(attachFile);
			art.setFile(fileName);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return memberDao.insertArt(art);
	}

	@Override
	public File getAttachFile(HashMap<String, Object> params) {
		String fileName;
		String path;
		if (params.get("id")!=null) {
			member member = memberDao.selectOneMember((String)params.get("id"));
			if (params.get("profile")!=null) {
			fileName = member.getProfile();	
			}else {
			fileName = member.getFile();
			}
			path = "C:/Project/Project/image/";
		}else {
			if (params.get("lecture")!=null) {
			lecture lecture = lectureDao.selectOneLecture((int)params.get("no"));
			fileName = lecture.getFile();
			path = "C:/Project/Project/WebContent/resources/Thumnail/lectureImage/";
			}else {
			art art = artDao.selectOneArt((int)params.get("no"));
			fileName = art.getFile();
			path = "C:/Project/Project/WebContent/resources/Thumnail/artImage/";
			}
		}
		return new File(path + fileName);
	}

	@Override
	public Integer updateApproveLec(int no, int state) {
		lecture lecture = new lecture();
		lecture.setNo(no);
		lecture.setState(state);
		return memberDao.updateApproveLec(lecture);
	}

	@Override
	public List<pay> selectPayByNo(HashMap<String, Integer> params) {
		// TODO Auto-generated method stub
		return memberDao.selectPayByNo(params);
	}
	
	@Override
	public int userAuth(String email) throws Exception {
		return memberDao.userAuth(email);
	}

}
