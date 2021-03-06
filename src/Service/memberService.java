package Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import Model.art;
import Model.member;
import Model.pay;

public interface memberService {

	public	member	selectOneMember	(String id);
	public	member	selectOneNickname (String nickname); // 추가
	public	int login (String id, String pw); // 추가
	public	int	insertMember	(member member, MultipartFile uProfile, MultipartFile uFile) throws Exception; // 파라미터 추가
	public	List<member>	searchMember	(String keyWord, String searchWord, int isCheck);
	public	HashMap<String, Object>		searchApproveArtist	(HashMap<String, Object> params, int page); // 수정(검색값, 페이지)
	public	HashMap<String, Object>	selectAllMember	(HashMap<String, Object> params, int page); // 수정(검색값, 페이지)
	public	int	deleteMember	(String id);
	public	int	blackList	(String id); // 수정
	public	int	updateApproveArtist	(String id);
	public	int	updateRefuseArtist	(String id);
	public	int	updateMember	(member member);
	
	public	HashMap<String, Object>	selectMyAttendLec	(String id, int page, HashMap<String, Object> params);
	public	HashMap<String, Object>	selectGatherLec		(String id, int page, HashMap<String, Object> params);
	public	HashMap<String, Object>	selectRequestLec	(String id, int page, HashMap<String, Object> params);
	public	HashMap<String, Object>	selectApproveLec	(String id, int page, HashMap<String, Object> params);
	public	HashMap<String, Object>	selectBuyingArt	(String id, int page, HashMap<String, Object> params);
	public	HashMap<String, Object>	selectSellingArt(String id, int page, HashMap<String, Object> params);
	public	HashMap<String, Object>	selectSoldArt	(String id, int page, HashMap<String, Object> params);
	public	pay	selectOnePay	(String orderNumber);
	
	public	List<art>	selectArtistArt	(String id);

	public	int	insertLecturePay	(pay pay);

	public	int	insertArt	(art art, MultipartFile file);

	public	int	updateArt	(art art, MultipartFile file);		// 수정 (09.11)
	public	int	deleteArt	(int no, String id);
	public	int	updateDelivery	(String orderNumber, int state);
	
	public File getAttachFile(HashMap<String, Object> params); // 추가 + 수정
	public Integer updateApproveLec(int no, int state);		// 추가

	public int userAuth(String email) throws Exception; // 추가
	public List<pay> selectPayByNo(HashMap<String, Integer> params); // 각 작품 혹은 강의의 no 로 결제 정보 가져오기
	
}
