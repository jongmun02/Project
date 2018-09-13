package Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import Dao.lectureDao;
import Model.attendants;
import Model.lecture;
import Model.lectureComment;
import Model.pay;

@Service
public class lectureServiceImpl implements lectureService{

	@Autowired
	private lectureDao lectureDao;
	
	@Autowired
	private mainService mainService;

	@Override
	public HashMap<String, Object> searchLecture(HashMap<String, Object>params, int page) {
		HashMap<String, Object> result = new HashMap<>();
		result.put("current", page);
		result.put("start", mainService.getStartPage(page));
		result.put("end", mainService.getEndPage(page));
		result.put("last", mainService.getSearchLectureLastPage(params));
		params.put("skip", mainService.getSkip(page, 5));
		params.put("qty", 5);
		result.put("lectureList", lectureDao.searchLecture(params));
		return result;
	}

	@Override
	public int insertLecture(lecture lecture, MultipartFile file) {
		// TODO Auto-generated method stub
		String path = "C:/Project/Project/WebContent/resources/Thumnail/lectureImage/";

		File dir = new File(path);
		if(!dir.exists())
			dir.mkdirs(); //지정 경로에 폴더가 없을 시 폴더 생성 요청
		
		String fileName=new Date().getTime() + "_" + file.getOriginalFilename();
		File attachFile = new File(path + fileName);
		try {
			file.transferTo(attachFile);
			lecture.setFile(fileName);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lectureDao.insertLecture(lecture);
	}

	@Override
	public int updateLecture(lecture lecture, MultipartFile file) {
		// TODO Auto-generated method stub
		String path = "C:/Project/Project/WebContent/resources/Thumnail/lectureImage/";

		File dir = new File(path);
		if(!dir.exists())
			dir.mkdirs(); //지정 경로에 폴더가 없을 시 폴더 생성 요청
		
		String fileName=new Date().getTime() + "_" + file.getOriginalFilename();
		File attachFile = new File(path + fileName);
		try {
			file.transferTo(attachFile);
			lecture.setFile(fileName);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lectureDao.updateLecture(lecture);
	}

	@Override
	public int deleteLecture(int no) {
		// TODO Auto-generated method stub
		return lectureDao.deleteLecture(no);
	}

	@Override
	public lecture selectOneLecture(int no) {
		return lectureDao.selectOneLecture(no);
	}

	@Override
	public int insertAttendants(int no, String id) {
		attendants attendants = new attendants();
		attendants.setNo(no);
		attendants.setId(id);
		return lectureDao.insertAttendants(attendants);
	}

	@Override
	public List<lecture> selectRecentLectureA() {
		return lectureDao.selectRecentLectureA();
	}

	@Override
	public List<lecture> selectRecentLectureG() {
		return lectureDao.selectRecentLectureG();
	}

	@Override
	public HashMap<String, Object> selectAllLectureA(int page) {
		HashMap<String, Object> result = new HashMap<>();
		result.put("current", page);
		result.put("start", mainService.getStartPage(page));
		result.put("end", mainService.getEndPage(page));
		result.put("last", mainService.getAllLectureALastPage());
		result.put("lectureList", lectureDao.selectAllLectureA(mainService.getSkip(page, 5)));
		return result;
	}

	@Override
	public HashMap<String, Object> selectAllLectureG(int page) {
		HashMap<String, Object> result = new HashMap<>();
		result.put("current", page);
		result.put("start", mainService.getStartPage(page));
		result.put("end", mainService.getEndPage(page));
		result.put("last", mainService.getAllLectureGLastPage());
		result.put("lectureList", lectureDao.selectAllLectureG(mainService.getSkip(page, 5)));
		return result;
	}

	@Override
	public int insertLectureComment(lectureComment lectureComment) {
		// TODO Auto-generated method stub
		return lectureDao.insertLectureComment(lectureComment);
	}


	@Override
	public List<lectureComment> selectLectureComment(HashMap<String, Object> params) {
		// TODO Auto-generated method stub
		return lectureDao.selectLectureComment(params);
	}
	// 강의 모집인원 숫자 변경 함수 // 추가
	@Override
	public int updateLecturePeople(int no, String id) {
		lecture originLec = lectureDao.selectOneLecture(no);
		int currentPeople = originLec.getNumberPeople();
		lecture lecture = new lecture();
		if (currentPeople + 1 == originLec.getMaxPeople()) {
			lecture.setState(2);
			// 알림 소스
			mainService.insertAlarm("maxPeople", originLec.getArtistID(), originLec.getTitle());		// 모집 완료 알림: 아티스트에게 전송
			if (originLec.getGuestID()!=null) {
				mainService.insertAlarm("maxPeople", originLec.getGuestID(), originLec.getTitle());  // 모집 완료 알림: 개설한 사용자에게 전송
			}
			for (attendants attendants : lectureDao.selectAttendants(no)) {
				mainService.insertAlarm("maxPeople", attendants.getId(), originLec.getTitle());		// 모집 완료 알림 : 강의참석자에게 전송
			}
			mainService.insertAlarm("maxPeople", id, originLec.getTitle());	// 모집 완료 알림 : 현 접속자에게 전송
		}
		lecture.setNumberPeople(currentPeople + 1);
		lecture.setNo(no);
		return lectureDao.updateLecturePeople(lecture);
	}

	@Override
	public lectureComment selectLectureLatestcomment() {
		// TODO Auto-generated method stub
		return lectureDao.selectLectureLatestcomment();
	}

	@Override
	public List<attendants> selectAttendants(int no) {
		return lectureDao.selectAttendants(no);
	}

	@Override
	public int deleteLectureComment(int no) {
		// TODO Auto-generated method stub
		return lectureDao.deleteLectureComment(no);
	}

}
