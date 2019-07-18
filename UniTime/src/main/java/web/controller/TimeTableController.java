package web.controller;

import java.util.List;

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

import web.service.face.TimeTableService;

@Controller
public class TimeTableController {
	
	private static final Logger logger = LoggerFactory.getLogger(TimeTableController.class);
	
	@Autowired TimeTableService timeTableService;
	
	@RequestMapping(value="/timetable/lecturelist", method=RequestMethod.GET)
	public void lectureAll(Model model, HttpSession session) { 
		logger.info("시간표 메인페이지 - 강의 목록");
		
		List list = timeTableService.list();
		
		model.addAttribute("lectureList",list);
		
		String id= (String) session.getAttribute("email");
		List mylist = timeTableService.myList(id);
		
		model.addAttribute("myList", mylist);
		
	}
	
	@RequestMapping(value="/timetable/lecturelist", method=RequestMethod.POST)
	public String containMylist(Model model, @RequestParam("checkRow") String names, HttpSession session) {
		
		System.out.println(names);
		
		timeTableService.myListInsert(names);
		
		return "redirect:/timetable/containmylist";
	}
	
	@RequestMapping(value="/timetable/containmylist", method=RequestMethod.GET)
	public String viewMylist(Model model, @RequestParam("checkRow") String names, HttpSession session) {
		
		String id= (String) session.getAttribute("email");
		List mylist = timeTableService.myList(id);
	
		
		model.addAttribute("myList", mylist);
		
		
		return "redirect:/timetable/lecturelist";
	}
	
	
	@RequestMapping(value="/timetable/deletemylist", method=RequestMethod.POST)
	public String deleteMylist(Model model, HttpServletRequest req, HttpSession session) {
		
		String names= req.getParameter("names");
		
		timeTableService.myListDelete(names);
		
		String id= (String) session.getAttribute("email");
		List mylist = timeTableService.myList(id);
		
		model.addAttribute("myList", mylist);
		
		
		return "redirect:/timetable/lecturelist";
	}
	
	@RequestMapping(value="/timetable/recommend", method=RequestMethod.GET)
	public void selectLecture() {
		
	}
	
	@RequestMapping(value="/timetable/recommend", method=RequestMethod.POST)
	public String recommend() {
		
		return "";
	}
	
}
