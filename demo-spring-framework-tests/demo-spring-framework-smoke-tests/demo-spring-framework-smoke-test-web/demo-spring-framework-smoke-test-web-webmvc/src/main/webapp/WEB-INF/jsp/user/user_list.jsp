<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	function batchDelete(){
		var checks = $("input[name='ids']").filter(':checked').length;
		if(checks==0){
			alert('请至少选择一条记录');
			return false;
		}
		$('#listForm')[0].action = '<%=ctx%>/user/batchDelete.html';
		$('#listForm')[0].submit();
	}
	
	function toggleCheck(check){
		$("input[name='ids']").attr('checked', check);
	}
	
	function testAsyncJson1(){
		$.getJSON('../rest/user/async1/1',function(data){
			alert('username='+data.name+'\npassword='+data.age);
		});
	}
	
	function testAsyncJson2(){
		$.getJSON('../rest/user/async2/1',function(data){
			alert('username='+data.name+'\npassword='+data.age);
		});
	}
	
	function testAsyncJson3(){
		$.getJSON('../rest/user/async3/1',function(data){
			alert('username='+data.name+'\npassword='+data.age);
		});
	}
	
	function testAsyncJson4(){
		$.getJSON('../rest/user/async4/1',function(data){
			alert('username='+data.name+'\npassword='+data.age);
		});
	}
	
	var localHandler = function(data){
		alert('获取远程数据：' + data.name);
	};
	
	function testJsonp(){
		var url = "../user/1?callback=localHandler";
	    var script = document.createElement('script');
	    script.setAttribute('src', url);
	    document.getElementsByTagName('head')[0].appendChild(script); 
	}
	
</script>
<title>List</title>
</head>
<body class="main-body query">
<form id="listForm" name="listForm" method="get" action="<%=ctx%>/user/list.html">
	<div class="path">
		<p><span>当前位置：首页</span></p>
	</div>
	<div class="main-cont">
		<h3 class="title">用户查询</h3>
			<div class="box">
				<div class="form">
						<div class="row">
							<label>姓名:</label>
							<div class="el">
								<input type="text" name="name" id="name" value="${userForm.name}" />
							</div>
						</div>
						<div class="row">
							<label>性别:</label>
							<div class="el">
								<input type="radio" name="gender" id="gender1" value="1" ${userForm.gender==1?'checked="checked"':''}/>男
								<input type="radio" name="gender" id="gender2" value="2" ${userForm.gender==2?'checked="checked"':''}/>女
							</div>
						</div>
						<div class="row">
							<label>学历:</label>
							<div class="el">
								<select name="degree" id="degree">
									<option value="0" ${userForm.degree==0?'selected="selected"':''}>不限</option>
									<option value="1" ${userForm.degree==1?'selected="selected"':''}>大专</option>
									<option value="2" ${userForm.degree==2?'selected="selected"':''}>本科</option>
									<option value="3" ${userForm.degree==3?'selected="selected"':''}>硕士</option>
									<option value="4" ${userForm.degree==4?'selected="selected"':''}>博士</option>
								</select>
							</div>
						</div>
						<div class="row">
							<label>年龄:</label>
							<div class="el">
								<c:if test="${userForm.age==0}"><input type="text" name="age" id="age" value="" /></c:if>
								<c:if test="${userForm.age!=0}"><input type="text" name="age" id="age" value="${userForm.age}" /></c:if>
							</div>
						</div>
						<div class="row">
							<label>生日:</label>
							<div class="el">
								<input type="text" name="birthday" id="birthday" value="<fmt:formatDate value="${userForm.birthday}" pattern="yyyy-MM-dd" />" onfocus="WdatePicker({el:'birthday',readOnly:true,dateFmt:'yyyy-MM-dd'})" class="Wdate"/>
							</div>
						</div>
						<div class="actions">
							<button type="submit">查询</button>
							<button type="button" onclick="javascript:window.location='<%=ctx%>/user/add.html';">新增</button>
							<button type="button" onclick="testAsyncJson1();">asyncJson1</button>
							<button type="button" onclick="testAsyncJson2();">asyncJson2</button>
							<button type="button" onclick="testAsyncJson3();">asyncJson3</button>
							<button type="button" onclick="testAsyncJson4();">asyncJson4</button>
							<button type="button" onclick="testJsonp();">JSONP</button>
							<span style="color:red">${errorMessage}</span>
						</div>
				</div>
				<!-- end of form -->
				<div class="set-area">
					<table cellpadding="0" class="table">
						<colgroup>
	                        <col style="width:20px;" />
	                        <col style="width:40px;" />
	                        <col style="width:20px;" />
	                        <col style="width:20px;" />
	                        <col style="width:40px;" />
	                        <col style="width:20px;" />
	                        <col style="width:40px;" />
	                        <col style="width:50px;" />
	                        <col style="width:50px;" />
	                        <col style="width:100px;" />
	                    </colgroup>
						<thead>
							<tr>
								<th><input type="checkbox" name="allCheck" id="allCheck" value="" onclick="toggleCheck(this.checked);"/></th>
								<th>姓名</th>
								<th>性别</th>
								<th>年龄</th>
								<th>生日</th>
								<th>学历</th>
								<th>手机</th>
								<th>邮箱</th>
								<th>简介</th>
								<th><button type="button" onclick="batchDelete();">批量删除</button></th>
							</tr>
						</thead>
						<tfoot>
	                        <tr>
	                            <td colspan="10">
	                                <%@ include file="../page.jsp"%>
	                            </td>
	                        </tr>
	                    </tfoot>
						<tbody>
						<c:forEach items="${list}" var="u" varStatus="status">
							<tr>
								<td width="3%"><input type="checkbox" name="ids" id="id_${status.index}" value="${u.id}"/></td>
								<td><c:out value="${u.name}"></c:out></td>
								<td><c:out value="${u.gender==1?'男':'女'}"></c:out></td>
								<td><c:out value="${u.age}"></c:out></td>
								<td><fmt:formatDate value="${u.birthday}" pattern="yyyy-MM-dd" /></td>
								<td><c:out value="${u.degree==1?'大专':(u.degree==2?'本科':(u.degree==3?'硕士':(u.degree==4?'博士':'未知')))}"></c:out></td>
								<td><c:out value="${u.cellphone}"></c:out></td>
								<td><c:out value="${u.email}"></c:out></td>
								<td><c:out value="${u.intro}"></c:out></td>
								<td><a href="<%=ctx%>/user/edit/${u.id}.html">编辑</a>&nbsp;<a href="<%=ctx%>/user/delete/${u.id}.html">删除</a>&nbsp;|&nbsp;<a href="<%=ctx%>/user/${u.id}.json">JSON</a>
									&nbsp;<a	href="<%=ctx%>/user/${u.id}.xml">XML</a>&nbsp;<a href="<%=ctx%>/user/${u.id}.xlsx">XLSX</a>&nbsp;<a href="<%=ctx%>/user/${u.id}.pdf">PDF</a>&nbsp;<a href="<%=ctx%>/rest/user/sse/${u.id}">SSE</a></td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
				<!-- end of table -->
			</div>
		</div>
</form>
</body>
</html>