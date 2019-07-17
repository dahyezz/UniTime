<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
    
<div class="ed board-header padding-horizontal-small@margin-bottom-small">
	<h3>중고장터</h3>
</div>

<table border=1 
    style="text-align: center; margin: auto;">
	<tr>
		<th style="text-align:center; width: 7%">글번호</th>
		<th style="text-align:center; width: 8%">태그</th>
		<th style="text-align:center; width: 15%">작성자</th>
		<th style="text-align:center; width: 35%">제목</th>
		<th style="text-align:center; width: 12%">가격</th>
		<th style="text-align:center; width: 8%">조회수</th>
		<th style="text-align:center; width: 15%">작성일</th>
	</tr>
	<c:forEach items="#{list }" var="i">
	<tr>
		<td>${i.boardno }</td>
		<td>${i.tag }</td>
		<td>${i.writer }</td>
		<td><a href="/used/view?boardno=${i.boardno }">${i.title }</a></td>
		<td>${i.price }</td>
		<td>${i.hit }</td>
		<td><fmt:formatDate value="${i.writtendate }" pattern="yyyy-MM-dd" /></th>
	</tr>	
	</c:forEach>
</table>