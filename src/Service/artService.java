package Service;

import java.util.HashMap;
import java.util.List;

import Model.art;
import Model.artComment;
import Model.pay;

public interface artService {

	public	HashMap<String, Object>	searchArt	(HashMap<String , Object>params, int page); //수정
	public	List<art>	selectTopArt	(String genre);
	public	HashMap<String, Object>	selectRecentArt	(String genre, int page); //수정

	public	art	selectOneArt	(int no);
	public	int	insertArtPay	(pay pay);
	public	int	insertArtComment	(artComment artComment);

	public	List<artComment>	selectArtComment	(HashMap<String, Object>params);
	public	artComment	selectArtLatestcomment();

	//추가
	public int getMaxGroupNo(HashMap<String, Object> params); //댓글 묶음 번호 확인
	public int deleteComment(HashMap<String, Object> params);
	public int updateComment(HashMap<String, Object> params);
	
	//조건에 따라 해당 댓글 그룹 안의 그룹 넘버 확인
	public int selectGroupNo(HashMap<String, Object> params);
	
	//그룹 넘버가 null 일 때 수행
	public int maxGroupOrder(HashMap<String, Object> params);

	//그룹 넘버가 null 이 아닐 때 수행 -> 댓글, 답글 입력 시 그룹 내 순서 번호 수정
	public int updateGroupOrder(HashMap<String, Object> params); 
	
	public int deleteArtComment(int no);
	
}
