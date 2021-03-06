package web.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.dao.face.MemberDao;
import web.dao.face.TimeTableDao;
import web.dto.Member;
import web.dto.TempTable;
import web.dto.TimeTable;
import web.service.face.TimeTableService;

@Service
public class TimeTableServiceImpl implements TimeTableService {

	@Autowired TimeTableDao timeTableDao;
	@Autowired MemberDao memberDao;
	
	@Override
	public List list() {
		return timeTableDao.selectAll();
	}

	@Override
	public void myListInsert(TempTable temp) {
		timeTableDao.insertMyList(temp);
	}
	
	

	@Override
	public void myListDelete(TempTable temp) {
		timeTableDao.deleteMyList(temp);
	}

	@Override
	public List myList(String id) {
		
		return timeTableDao.selectMyList(id);
	}

	@Override
	public List recommendList(HttpServletRequest req) {
		
		HttpSession session = req.getSession();
		String id=(String)session.getAttribute("email");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", id);
		map.put("table", "kg");
		
		Member mem = new Member();
		mem.setEmail(id);
		mem = memberDao.selectMemberByEmail(map);
		String d_name = timeTableDao.selectDepartname(mem);
		
//		Map<String,String> map = new HashMap<String, String>();
		
		map.put("d_name",d_name);
		map.put("stime", req.getParameter("timepriority"));
		map.put("etime", Integer.toString(Integer.parseInt(req.getParameter("timepriority"))+3));
		map.put("classNum", req.getParameter("classNum"));
		map.put("id",id);
		
//		List reclist = timeTableDao.recommendTmp(map);
		
		String recCode[] = timeTableDao.recCode(map);
		TempTable temp = new TempTable();
		temp.setUser_email(id);
		timeTableDao.deleteRec(temp);
		
		for(int i=0;i<recCode.length;i++) {
			temp.setLecture_code(Integer.parseInt(recCode[i]));
			timeTableDao.insertRec(temp);
		}
		
		List reclist = timeTableDao.selectRec(id);
		
		return reclist;
	}

	@Override
	public boolean checkLecture(TempTable temp) {
		
		boolean check = true;
		if(timeTableDao.lectureCheck(temp)==0) {
			check=true;
		}
		else {
			check=false;
		}
		
		return check;
	}

	@Override
	public TimeTable getTableByTemp(TempTable temp) {
		return timeTableDao.getTableByTemp(temp);
	}

	@Override
	public void setMytable(String id) {
		timeTableDao.insertMytable(id);
	}


}
