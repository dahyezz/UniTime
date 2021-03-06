package web.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;
import com.google.code.geocoder.model.LatLng;

import web.dao.face.TastyBoardDao;
import web.dto.BadReport;
import web.dto.TastyBoard;
import web.dto.TastyComment;
import web.dto.TastyFile;
import web.service.face.TastyBoardService;
import web.util.Paging;

@Service
public class TastyBoardServiceImpl implements TastyBoardService{

	@Autowired TastyBoardDao tastyBoardDao;
	
	@Override
	public Paging getcurPage(Map<String, Object> map) {

		int totalCount = tastyBoardDao.selectCntAll();
		int curPage = Integer.parseInt(map.get("curPage").toString());
		
		int listCount = 9;
		Paging paging = new Paging(totalCount, curPage, listCount);
		
		paging.setSearchType((String) map.get("searchType"));
		paging.setKeyword((String) map.get("keyword"));
		paging.setListSelect((String) map.get("listSelect"));
		
		return paging;
	}
	
	@Override
	public List<TastyBoard> list(Paging paging) {
		
		if(paging.getListSelect()==null || paging.getListSelect().equals(""))
			paging.setListSelect("date");
		
		return tastyBoardDao.selectAll(paging);
	}
	
	@Override
	public TastyBoard getBoard(TastyBoard tastyBoard) {
		tastyBoardDao.updateHit(tastyBoard);
		
		return tastyBoardDao.selectBoardByBoardno(tastyBoard);
	}
	
	@Override
	public TastyBoard getBoardWriter(TastyBoard tastyBoard) {
		return tastyBoardDao.selectBoardByBoardno(tastyBoard);
	}
	
	@Override
	public void write(TastyBoard tastyBoard) {

		tastyBoardDao.insertBoard(tastyBoard);
		
		//tastyfile에 boardno=0 인거 update해주기
		tastyBoardDao.updateBoardnoToFile(tastyBoard);

	}
	
	@Override
	public void delete(TastyBoard tastyBoard) {
		
		tastyBoardDao.deleteCommentByBoardno(tastyBoard);
		tastyBoardDao.deleteFileByboardno(tastyBoard);
		tastyBoardDao.deleteBoardByBoardno(tastyBoard);
	}
	
	@Override
	public void update(TastyBoard tastyBoard) {
		
		tastyBoardDao.updateBoard(tastyBoard);
	}
	
	@Override
	public TastyFile uploadFile(TastyBoard tastyBoard, MultipartFile fileupload, ServletContext context) {
		
		//파일이 저장될 경로
		String storedPath = context.getRealPath("tastyUpload");
		
		//UUID
		String uId = UUID.randomUUID().toString().split("-")[4];
		
		//저장될 파일의 이름(원본이름 + UUID)
		String name = fileupload.getOriginalFilename()+"_"+uId;
		
		//저장될 파일 객체
		File dest = new File(storedPath, name);
		
		try {
			fileupload.transferTo(dest); //실제 저장
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int boardno = 0;
		
		if(tastyBoard.getBoardno()!=0)
			boardno = tastyBoard.getBoardno();
		
		//DB에 저장(업로드 정보 기록)
		TastyFile tastyfile = new TastyFile();
		tastyfile.setBoardno(boardno);
		tastyfile.setOriginName(fileupload.getOriginalFilename());
		tastyfile.setStoredName(name);
		tastyfile.setFilesize((int)fileupload.getSize());
		
		tastyBoardDao.insertFile(tastyfile);
		
		return tastyfile;
	}
	
		
	@Override
	public List<TastyComment> getComment(TastyBoard tastyBoard) {
		return tastyBoardDao.selectAllCommentByBoardno(tastyBoard);
	}
	
	@Override
	public void writeComment(TastyComment tastyComment) {
		if(tastyComment.getCommentno()!=0) {
			tastyComment.setRefCommentno(tastyComment.getCommentno());
			tastyComment.setDept(tastyComment.getDept()+1);
			tastyBoardDao.insertComment(tastyComment);
		} else {
			tastyBoardDao.insertComment(tastyComment);
		}
		
	}
	
	@Override
	public int deleteComment(TastyComment tastyComment, String loginUser) {
		
		if(loginUser.equals("admin")) {
			tastyBoardDao.deleteComment(tastyComment);
			return 0;
		}
		
		if(tastyBoardDao.selectCntRecomment(tastyComment) > 0) {
			tastyBoardDao.updateDeletedComment(tastyComment);
			return 1;
			
		} else {
			tastyBoardDao.deleteComment(tastyComment);
			return 0;
		}
	}
	
	@Override
	public TastyComment getBoardno(TastyComment tastyComment) {
		return tastyBoardDao.selectBoardnoByCommentno(tastyComment);
	}
	
	@Override
	public TastyFile getFile(TastyFile tastyfile) {
		return tastyBoardDao.selectFileByFileno(tastyfile.getFileno());
	}
	
	@Override
	public TastyComment getComment(TastyComment tastyComment) {
		return tastyBoardDao.selectBoardnoByCommentno(tastyComment);
	}
	
	@Override
	public void deleteList(String names) {
		
		String[] nameList = names.split(",");
		int deleteList[] = new int[nameList.length];
		TastyBoard tastyBoard = new TastyBoard();
		
		for(int i=0; i<nameList.length; i++) {
			deleteList[i] = Integer.parseInt(nameList[i]);
			
			tastyBoard.setBoardno(deleteList[i]);
			
			tastyBoardDao.deleteCommentByBoardno(tastyBoard);
			tastyBoardDao.deleteFileByboardno(tastyBoard);
			tastyBoardDao.deleteBoardByBoardno(tastyBoard);
		}
		
	}
	
	@Override
	public void updateComment(TastyComment tastyComment) {
	
		tastyBoardDao.updateComment(tastyComment);
	}
	
	@Override
	public boolean declareBoard(BadReport badReport) {
		
		if(tastyBoardDao.selectCntBadReport(badReport)>0) {
			return false;
		}
		else {
			tastyBoardDao.insertBadByBoard(badReport);
			return true;
		}
			
	}
	
	@Override
	public void deleteImage(TastyFile file) {
		
		TastyBoard tastyBoard = new TastyBoard();
		tastyBoard.setBoardno(file.getBoardno());
		tastyBoardDao.deleteFileByfileno(file);
	}
	
	@Override
	public boolean checkReclare(BadReport badReport) {
		
		if(tastyBoardDao.selectCntBadReport(badReport)>0)
			return true;
		else
			return false;
	}
	
	@Override
	public float[] geoCoding(Map<String, String> map) {
		
//		Map<String, String> map = new HashMap<String, String>();
		String storeName = map.get("storeName");
		System.out.println(storeName);
		
		if(storeName == null)
			return null;
		
		Geocoder geocoder = new Geocoder();
		
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(storeName).setLanguage("ko").getGeocoderRequest();
		GeocodeResponse geocoderResponse;
		
//		
		
		try {
			geocoderResponse = geocoder.geocode(geocoderRequest);
			
//			System.out.println("----------------------");
			System.out.println(geocoderResponse.getStatus());
			System.out.println(geocoderResponse.getResults().isEmpty());
			
			if(geocoderResponse.getStatus() == GeocoderStatus.OK & !geocoderResponse.getResults().isEmpty()) {
				
				GeocoderResult geocoderResult = geocoderResponse.getResults().iterator().next();
				LatLng latitudeLongitude = geocoderResult.getGeometry().getLocation();
				
//				map.put("y_point", latitudeLongitude.getLat().toString());
//				map.put("x_point", latitudeLongitude.getLng().toString());
//				
//				System.out.println(latitudeLongitude.getLat().toString());
//				System.out.println(latitudeLongitude.getLng().toString());
				float[] coords =new float[2];
				coords[0] = latitudeLongitude.getLat().floatValue();
				coords[1] = latitudeLongitude.getLng().floatValue();
				
				System.out.println(coords[0]);
				System.out.println(coords[1]);
				
				return coords;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
