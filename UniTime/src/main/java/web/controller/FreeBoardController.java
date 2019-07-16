package web.controller;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import web.dto.FreeBoard;
import web.dto.FreeFile;
import web.dto.FreeComment;
import web.service.face.FreeBoardService;
import web.util.Paging;

@Controller
public class FreeBoardController {

	@Autowired private FreeBoardService freeBoardService;
	
	@Autowired ServletContext context;

	private static final Logger logger = LoggerFactory.getLogger(FreeBoardController.class);
	
	@RequestMapping(value="/free/list", method=RequestMethod.GET)
	public void list( HttpServletRequest req, Model model) {
		logger.info("자유게시판 리스트");
		
		//요청 파라미터에서 curPage 얻어오기
		Paging paging=freeBoardService.getCurPage(req);
		
		//MODEL로 paging 객체 넣기
		model.addAttribute("paging", paging);
		
		//board 테이블 전체 조회 결과 얻기
		List list=freeBoardService.list(paging);
		
		model.addAttribute("list",list);
	}
	
	@RequestMapping(value="/free/view", method=RequestMethod.GET)
	public void view(FreeBoard freeBoard, Model model) {
		logger.info("게시글 보기");
		
		//게시글 번호 파싱
		int boardno = freeBoard.getBoardno();
		
		//게시글 조회
		freeBoard=freeBoardService.view(boardno);
		
		//게시글의 파일 조회
		FreeFile file=freeBoardService.viewFile(boardno);
		
		//게시글의 댓글 조회
		List<FreeComment> commentList=freeBoardService.commentList(boardno);
		
		//MODEL로 객체 전달
		model.addAttribute("board", freeBoard);
		model.addAttribute("viewFile", file);
		model.addAttribute("commentList", commentList);
	}
	
	@RequestMapping(value="/free/write", method=RequestMethod.GET)
	public void write() {
		logger.info("게시판 글쓰기");
	}
	
	@RequestMapping(value="/free/write", method=RequestMethod.POST)
	public String writeProc(HttpSession session, FreeBoard freeBoard, @RequestParam(value="file") MultipartFile fileupload) {
		logger.info("게시판 글쓰기 처리");
		
		//세션 정보 넣어주기
		freeBoard.setWriter((String)session.getAttribute("nick"));
		
		//게시글 작성, 첨부파일 저장
		freeBoardService.write(freeBoard, fileupload, context);
		
		return "redirect:/free/list";
	}
	
	@RequestMapping(value="/free/update", method=RequestMethod.GET)
	public void update(FreeBoard freeBoard, Model model) {
		logger.info("게시글 수정");
		
		//게시글 조회
		freeBoard=freeBoardService.view(freeBoard.getBoardno());
		
		model.addAttribute("board",freeBoard);
	}
	
	@RequestMapping(value="/free/update", method=RequestMethod.POST)
	public String updateProc(FreeBoard freeBoard) {
		logger.info("게시글 수정 처리");
		
		//게시글 조회
		freeBoardService.update(freeBoard);
		
		return "redirect:/free/view?boardno="+freeBoard.getBoardno();
	}
	
	@RequestMapping(value="/free/delete", method=RequestMethod.GET)
	public String delete(FreeBoard freeBoard) {
		logger.info("게시글 삭제");
		
		freeBoardService.delete(freeBoard.getBoardno());
		
		return "redirect:/free/list";
	}

	@RequestMapping(value="/free/download", method=RequestMethod.GET)
	public ModelAndView download(int fileno,	//파일번호 전달 파라미터
								ModelAndView mav) {
		logger.info("파일 다운로드");
		
		//파일번호에 해당하는 파일 정보 가져오기
		FreeFile file=freeBoardService.getFile(fileno);
		
		//파일정보 Model로 값 넘기기
		mav.addObject("downFile", file);
		//-> FreeDownloadView 클래스에 Model값이 넘어감
		
		//viewName 지정
		mav.setViewName("freedown");
		//-> servlet-context.xml에 freedown이라는 이름으로 지정한 빈을 viewName으로 지정하면 FreeDownloadView 클래스가 동작
		
		return mav;
	}
	
	@RequestMapping(value="/comment/write", method=RequestMethod.GET)
	public void commentWrite() {
		logger.info("댓글쓰기");
	}
	
	@RequestMapping(value="/comment/write", method=RequestMethod.POST)
	public String commentWriteProc(HttpSession session, FreeBoard freeBoard, FreeComment freeComment) {
		logger.info("댓글쓰기 처리");
		
		freeComment.setBoardno(freeBoard.getBoardno());
		freeComment.setWriter((String)session.getAttribute("nick"));
		
		//댓글 작성
		freeBoardService.commentWrite(freeComment);
		
		return "redirect:/free/view?boardno="+freeBoard.getBoardno();
	}
	
	@RequestMapping(value="/comment/delete", method=RequestMethod.GET)
	public String commentDelete(FreeBoard freeBoard, FreeComment freeComment) {
		logger.info("댓글 삭제");
		
		freeBoardService.commentDelete(freeComment.getCommentno());
		
		return "redirect:/free/view?boardno="+freeBoard.getBoardno();
	}
	
	@RequestMapping(value="/check/delete", method=RequestMethod.GET)
	public String checkDelete(FreeBoard freeBoard, int[] checkDelete) {
		logger.info("댓글 삭제");
		
		for(int boardno : checkDelete) {
			freeBoardService.delete(boardno);
		}
		
		return "redirect:/free/list";
	}
}