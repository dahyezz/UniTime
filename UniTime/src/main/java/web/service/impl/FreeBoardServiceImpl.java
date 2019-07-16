package web.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import web.dao.face.FreeBoardDao;
import web.dto.FreeBoard;
import web.dto.FreeFile;
import web.service.face.FreeBoardService;
import web.dto.FreeComment;
import web.util.Paging;

@Service
public class FreeBoardServiceImpl implements FreeBoardService{

	@Autowired FreeBoardDao freeBoardDao;

	@Override
	public List list(Paging paging) {
		
		return freeBoardDao.selectAll(paging);
	}
	
	@Override
	public Paging getCurPage(HttpServletRequest req) {
		
		//전달 파라미터 curPage 파싱
		String param=req.getParameter("curPage");
		int curPage=0;
		if(param!=null && !"".equals(param)) {
			curPage=Integer.parseInt(param);
		}
		
		//전체 게시글 수
		int totalCount=freeBoardDao.selectCntAll();
		
		//페이징 객체 생성
		Paging paging=new Paging(totalCount,curPage);
//		System.out.println(paging);
		
		return paging;
	}

	@Override
	public FreeBoard view(int boardno) {
		
		freeBoardDao.updateHit(boardno);
		
		return freeBoardDao.selectBoardByBoardno(boardno);
	}
	
	@Override
	public void write(FreeBoard freeBoard, MultipartFile file, ServletContext context) {
		
		//파일이 저장될 경로
		String storedPath=context.getRealPath("freeUpload");
		
		//UUID
		String uId=UUID.randomUUID().toString().split("-")[4];
		
		//저장될 파일의 이름(원본 이름 + UUID)
		String name=file.getOriginalFilename()+"_"+uId;
		
		//저장될 파일 객체
		File dest=new File(storedPath,name);
		
		//파일 저장
		try {
			file.transferTo(dest);	//실제 저장
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//게시글 시퀀스 번호 조회
		int boardno=freeBoardDao.selectBoardno();
		
		//게시글 작성(데이터베이스 삽입)
		if(freeBoard!=null) {
			freeBoard.setBoardno(boardno);
			
			if(freeBoard.getTitle()==null || "".equals(freeBoard.getTitle())) {
				freeBoard.setTitle("(제목없음)");
			}
			
			freeBoardDao.insertBoard(freeBoard);
		}
		
		//DB에 저장(업로드 정보 기록)
		FreeFile freeFile=new FreeFile();
		freeFile.setOriginname(file.getOriginalFilename());
		System.out.println("원본 이름 : "+freeFile.getOriginname());
		
		freeFile.setStoredname(name);
		freeFile.setFilesize((int)file.getSize());
		
		//파일 업로드(데이터 베이스 삽입)
		if(freeFile.getOriginname() != null && !"".equals(freeFile.getOriginname())) {
			freeFile.setBoardno(boardno);
			freeBoardDao.insertFile(freeFile);
		}
	}

	@Override
	public void update(FreeBoard freeBoard) {
		
		//게시글 수정
		freeBoardDao.updateBoard(freeBoard);
	}
	
	@Override
	public void delete(int boardno) {
		
		//게시글의 파일첨부 삭제
		freeBoardDao.deleteFileByBoardno(boardno);
		
		//게시글의 댓글 삭제
		freeBoardDao.deleteCommentByBoardno(boardno);
		
		//게시글 삭제		
		freeBoardDao.deleteBoard(boardno);
	}

	@Override
	public FreeFile viewFile(int boardno) {
		
		return freeBoardDao.selectFileByBoardno(boardno);
	}

	@Override
	public FreeFile getFile(int fileno) {
		
		return freeBoardDao.selectFileByFileno(fileno);
	}
	
	@Override
	public List<FreeComment> commentList(int boardno) {
		return freeBoardDao.selectCommentByBoardno(boardno);
	}

	@Override
	public void commentWrite(FreeComment freeComment) {
		
		freeBoardDao.insertComment(freeComment);		
	}

	@Override
	public void commentDelete(int commentno) {
		
		freeBoardDao.deleteComment(commentno);		
	}
}