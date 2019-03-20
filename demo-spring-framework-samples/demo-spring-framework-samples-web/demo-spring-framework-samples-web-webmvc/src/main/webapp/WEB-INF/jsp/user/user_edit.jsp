<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.ruanwei.demo.springframework.web.user.web.form.UserForm" %>
<%@ include file="../head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Edit</title>
</head>
<body class="main-body">
	<div class="path">
		<p><span>当前位置：</span>首页&gt;<span>编辑数据</span></p>
	</div> 
	<div class="main-cont">
		<h3 class="title">编辑数据</h3>
		<div class="box">
			<div class="form">
				<form method="post" action="<%=ctx%>/user/doEdit.html">
					<div class="row">
						<label>姓名:</label>
						<div class="el">
							<input type="text" name="name" id="name" value="${user.name}" />
						</div>
					</div>
					<div class="row">
						<label>性别:</label>
						<div class="el">
							<input type="radio" name="gender" id="gender1" value="1" ${user.gender==1?'checked="checked"':''}/>男
							<input type="radio" name="gender" id="gender2" value="2" ${user.gender==2?'checked="checked"':''}/>女
						</div>
					</div>
					<div class="row">
						<label>年龄:</label>
						<div class="el">
							<input type="text" name="age" id="age" value="${user.age}"/>
						</div>
					</div>
					<div class="row">
						<label>生日:</label>
						<div class="el">
							<input type="text" name="birthday" id="birthday" value="<fmt:formatDate value="${user.birthday}" pattern="yyyy-MM-dd" />" onfocus="WdatePicker({el:'birthday',readOnly:true,dateFmt:'yyyy-MM-dd'})" class="Wdate" style="width:200px"/>
						</div>
					</div>
					<div class="row">
						<label>学历:</label>
						<div class="el">
							<select name="degree" id="degree">
								<option value="0" ${user.degree==0?'selected="selected"':''}>不限</option>
								<option value="1" ${user.degree==1?'selected="selected"':''}>大专</option>
								<option value="2" ${user.degree==2?'selected="selected"':''}>本科</option>
								<option value="3" ${user.degree==3?'selected="selected"':''}>硕士</option>
								<option value="4" ${user.degree==4?'selected="selected"':''}>博士</option>
							</select>
						</div>
					</div>
					<div class="row">
						<label>手机:</label>
						<div class="el">
							<input type="text" name="cellphone" id="cellphone" value="${user.cellphone}"/>
						</div>
					</div>
					<div class="row">
						<label>邮箱:</label>
						<div class="el">
							<input type="text" name="email" id="email" value="${user.email}"/>
						</div>
					</div>
					<div class="row">
						<label>爱好:</label>
						<div class="el">
							<input type="checkbox" name="hobbyArray" id="hobbyArray1" value="1" <%=((((UserForm)request.getAttribute("user")).getHobby())&1)!=0?"checked=\"checked\"":"" %>/>政治
							<input type="checkbox" name="hobbyArray" id="hobbyArray2" value="2" <%=((((UserForm)request.getAttribute("user")).getHobby())&2)!=0?"checked=\"checked\"":"" %>/>经济
							<input type="checkbox" name="hobbyArray" id="hobbyArray4" value="4" <%=((((UserForm)request.getAttribute("user")).getHobby())&4)!=0?"checked=\"checked\"":"" %>/>文化
							<input type="checkbox" name="hobbyArray" id="hobbyArray8" value="8" <%=((((UserForm)request.getAttribute("user")).getHobby())&8)!=0?"checked=\"checked\"":"" %>/>军事
						</div>
					</div>
					<div class="row">
						<label>个人简介:</label>
						<div class="el">
							<textarea name="intro" id="intro" rows="5" cols="50">${user.intro}</textarea>
						</div>
					</div>
					<div class="actions">
						<input type="hidden" name="id" id="id" value="${user.id}"/>
						<button type="submit">提交</button>&nbsp;
						<button type="button" onclick="history.back();">取消</button>
						<span style="color:red">${errorMessage}</span>
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>