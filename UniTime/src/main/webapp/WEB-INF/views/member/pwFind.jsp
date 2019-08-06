<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript" src="http://code.jquery.com/jquery-2.2.4.min.js"></script>

<script type="text/javascript">
var httpRequest = null;
//httpRequest 객체 생성
function getXMLHttpRequest(){
 var httpRequest = null;
 if(window.ActiveXObject){
     try{
         httpRequest = new ActiveXObject("Msxml2.XMLHTTP");    
     } catch(e) {
         try{
             httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
         } catch (e2) { httpRequest = null; }
     }
 }
 else if(window.XMLHttpRequest){
     httpRequest = new window.XMLHttpRequest();
 }
 return httpRequest;    
}
function pwFind(){
	var email=document.getElementById("email").value;
	
	if(!email){
		alert("이름을 입력하지 않았습니다");
		return false;
	} else{
		var param="username="+username+"&email="+email+"&userid="+userid;
		httpRequest=getXMLHttpRequest();
		httpRequest.onreadystatechange=callback;
		httpRequest.open("POST","/member/pwFind",true);
		httpRequest.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		httpRequest.send(param);
	}
}
function callback(){
	if(httpRequest.readyState==4){
		var resultText=httpRequest.responseText;
		
		document.getElementById("result").innerHTML=resultText;
	}
}
</script>

<style type="text/css">
.pwFind{
	background-color : #eeeff1;
	margin:-7px;
	padding-top:15px;
	padding-left:15px;
}
.pwFind input[type="text"]{
	width:300px;
	height:30px;
	border-radius: 5px;
	border:1.5px solid #ccc;
	font-size:20px;
	padding-left:10px;
}
.pwFind label{
	width:300px;
	height:30px;
	font-size:20px;
	padding-left:10px;
	font-weight:bold;
}
.pwFind input[type="button"]{
	margin-left:100px;
	width:125px;
	background-color:#0080ff;
	border:#0080ff;
	color:white;
	font-size:15px;
	font-weight:bold;
	border-radius: 5px;
	padding: 15px;
	cursor:pointer;
}
#result{
	height:70px;
	font-weight:bold;
	color:#0080ff;
	text-align:center;
	margin-top:20px;
}
</style>

</head>
<body>

<div class="pwFind">

<form action="/member/pwFind" method="post">

	<label for="email">이메일</label><br>
	<input type="text" id="email" name="email" placeholder="이메일을 입력하세요"/><br>
	
	<input type="button" value="비밀번호 찾기" onclick="pwFind()"/>
</form>

<div id="result"></div> 

</div>