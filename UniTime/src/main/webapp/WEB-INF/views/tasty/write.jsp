<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!-- include summernote css/js-->
<link href="http://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.8/summernote.css" rel="stylesheet">
<script src="http://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.8/summernote.js"></script>

<!-- select태그 부트스트랩 적용 -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.10/dist/css/bootstrap-select.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.10/dist/js/bootstrap-select.min.js"></script>

<script type="text/javascript">

$(document).ready(function() {
		
	$('#summernote').summernote({
		height: 800,
		minHeight: 300,
		maxHeight: null,
		focus: true,
		lang: 'ko-KR',
		placeholder: '음식사진은 최소 한 장 이상 첨부해주세요. 불건전한 언어 사용, 타인 비방 및 게시판 운영을 방해하는 행위가 확인되면 서비스 이용이 제한될 수 있습니다.',
		toolbar: [
			['style', ['style']],
			['font', ['bold', 'italic', 'underline', 'clear']],
	        ['fontname', ['fontname']],
	        ['color', ['color']],
	        ['para', ['ul', 'ol', 'paragraph']],
	        ['height', ['height']],
	        ['table', ['table']],
	        ['insert', ['link', 'hr', 'picture']],
	        ['view', ['fullscreen', 'codeview']],
	        ['help', ['help']]
		],
		callbacks: {
			onImageUpload: function(files, editor){
				sendFile(files[0], this);
// 				for(var i=files.length-1; i>=0; i--) {
// 					sendFile(files[i], this);
// 				}
			},
			onMediaDelete : function(files) {
	                alert("delete") 
	                deleteFile(files[0]);
	        }
		}
		
	});
	
	//취소 버튼 동작
	$("#btnCancel").click(function() {
		history.go(-1);
	});
	
	//select태그 동작
	$('select').selectpicker();
	
	$("#fo").submit(function() {
		
		
		if(writeForm.storeName.value==""){
			alert("음식점명을 입력해주세요");
			writeForm.storeName.focus();
			return false;
		}
		
		if(writeForm.loc.value==""){
			alert("위치를 입력해주세요");
			writeForm.loc.focus();
			return false;
		}
		
		if(writeForm.score.value==""){
			alert("별점을 선택해주세요");
			writeForm.score.focus();
			return false;
		}
		
		$(this).append($("<input>").attr("type", "hidden").attr("name", "boardno").val(${board.boardno }));
		var code = $('#summernote').summernote('code');
	    $('textarea[name="content"]').val(code);
		$(this).submit();
		
	});
	
	
});

var newfileno;

function sendFile(file, el){
	//파일 전송을 위한 폼 데이터 생성
	var data = new FormData();
	data.append('file', file);
	data.append("boardno",$('#boardno').val());
	
	//ajax를 통해 파일 업로드 처리
	$.ajax({	
		data: data
		, dataType: "json"
		, type: "POST"
		, url: "/tasty/imageUpload"
		, cache: false
		, contentType: false
		, enctype: "multipart/form-data"
		, processData: false
		, success: function(data) {
			newfileno = data.fileno

			$(el).summernote('editor.insertImage', "/tastyImage?fileno="+data.fileno);
			$('#boardno').val(data.boardno);
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus+"  "+errorThrown)
		} 
	});
}

function deleteFile(file){
	
	console.log(newfileno);
	console.log($(this))
}

// -------------- 별점 관련 ----------------------------------
var locked = 0;

function show(star){ //마우스 오버시 채워진 별로 나타나도록 함
	if(locked)
		return;
	var i;
	var image;
	var el;
	
	for(i=1; i<=star; i++){
		image = 'image'+i;
// 		console.log(image);
		el = document.getElementById(image);
// 		console.log(el)
		el.src = "/image/star.PNG";
	}
}

function noshow(star){ //mouse enter
	if(locked)
		return;
	var i;
	var image;
	var el;
	
	for(i=1; i<=star; i++){
		image='image'+i;
		el = document.getElementById(image);
		el.src = "/image/star0.PNG";
	}
}

function mark(star){
	lock(star);
// 	alert("선택:"+star);
	document.getElementById("score").value=star
}

function lock(star){
	show(star);
	locked=1;
}

</script>

<style type="text/css">
.ed>h3 {
	font-weight: bold;
	font-size: 30px;
}
.ed>span {
	font-size: 20px;
}

#starRank img{
	width: 30px;
}
.selectpicker {
	float: left;
}
.container {
	padding: 10px 0;
	margin-bottom: 30px;
	width: 100%;
}
.bootstrap-select:not([class*=col-]):not([class*=form-control]):not(.input-group-btn) {
	width: 60px;
	float: left;
	text-align: center; 
}
 
#btnWrite {
	background-color: #47b8e0;
  -webkit-border-radius: 5;
  -moz-border-radius: 5;
  border-radius: 5px;
  color: #fff;
  font-size: 13	px;
  padding: 5px 10px;
  border: none;
  text-decoration: none;
  
  margin-left: 7px;
  margin-bottom: 2px;
}
#btnCancel {
background-color: #ff7473;
  -webkit-border-radius: 5;
  -moz-border-radius: 5;
  border-radius: 5px;
  color: #fff ;
  font-size: 13	px;
  padding: 5px 10px;
  border: none;
  text-decoration: none;
}
.tasty_tb {
	font-size: 14px;
	line-height: 1.5;
	border: none;
	margin-bottom: 15px;
	font-family: 'NanumSquare', sans-serif;
}
.tasty_tb tr th {
	min-width: 100px;
	font-weight: 700;
	white-space: nowrap;
}
.tasty_tb tr th, .tasty_tb tr td {
	color: #34314c;
	text-align: left;
	padding: 10px 10px 0 0;
	border: none;
}
</style>

<input type="hidden" name="boardno" value="0" />


<div class="ed board-header padding-horizontal-small@s margin-bottom-small" style="text-align: left;">
	<h3>테이스티로드</h3>
	<span>숨겨진 맛집을 찾아서!</span>
	<p>여러분만의 숨겨진 맛집을 학우에게 공유해주세요</p>
</div>

<div>
<!-- <form action="/tasty/write" method="post" onsubmit="return postForm()" name="writeForm"> -->
<form action="/tasty/write" method="post" name="writeForm" id="fo">

	<div class="container">
		<table class="tasty_tb">
			<tr>
				<th>종류</th>
				<td>
					<select id="tag" name="tag" class="selectpicker" style="height:30px">
						<option value="술">술</option>
						<option value="밥">밥</option>
						<option value="카페">카페</option>
					</select>
				</td>
			</tr>
			
			<tr>
				<th>평점</th>
				<td>
				<span id="starRank">
					<img id="image1" onmouseover="show(1)" onclick="mark(1)" onmouseout="noshow(1)" src="/image/star0.PNG" >
					<img id="image2" onmouseover="show(2)" onclick="mark(2)" onmouseout="noshow(2)" src="/image/star0.PNG" >
					<img id="image3" onmouseover="show(3)" onclick="mark(3)" onmouseout="noshow(3)" src="/image/star0.PNG" >
					<img id="image4" onmouseover="show(4)" onclick="mark(4)" onmouseout="noshow(4)" src="/image/star0.PNG" >
					<img id="image5" onmouseover="show(5)" onclick="mark(5)" onmouseout="noshow(5)" src="/image/star0.PNG" >
				</span>
				<input type="hidden" name="score" id="score"/>
				</td>
			</tr>
			
			<tr>
				<th>상호명</th>
				<td>
					<input type="text" class="form-control" name="storeName" style="width:100%"/>
				</td>
			</tr>
			
			<tr>
				<th>위치</th>
				<td>
					<input type="text"  class="form-control" name="loc" style="width:100%"/>
				</td>
			</tr>
			
		</table>
		
	</div>
	
	<input type="hidden" name="writer" value="${nick }" />
	<textarea name="content" id="content" style="display: none;"></textarea>
	<div id="summernote"></div>

<div class="text-center">	
	<button type="submit" id="btnWrite">작성</button>
	<button type="button" id="btnCancel">취소</button>
</div>

<!-- <input type="file" name="file" /> -->
<input type="hidden" name="boardno" id="boardno" value="0" />

</form>
</div>
    