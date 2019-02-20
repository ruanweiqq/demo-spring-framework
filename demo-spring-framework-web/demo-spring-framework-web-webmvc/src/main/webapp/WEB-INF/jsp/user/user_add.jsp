<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Add</title>
</head>
<body class="main-body">
	<div class="path">
		<p><span>当前位置：</span>首页&gt;<span>添加数据</span></p>
	</div>
	<div class="main-cont">
		<h3 class="title">添加数据</h3>
		<div class="box">
			<div class="form">
				<form method="post" action="<%=ctx%>/user/doAdd.html">
					<div class="row">
						<label>姓名：</label>
						<div class="el">
							<input type="text" name="name" id="name" />
						</div>
					</div>
					<div class="row">
						<label>性别:</label>
						<div class="el">
							<input type="radio" name="gender" id="gender1" value="1" checked="checked"/>男
							<input type="radio" name="gender" id="gender2" value="2"/>女
						</div>
					</div>
					<div class="row">
						<label>年龄:</label>
						<div class="el">
							<input type="text" name="age" id="age"/>
						</div>
					</div>
					<div class="row">
						<label>生日:</label>
						<div class="el">
							<input type="text" name="birthday" id="birthday" onfocus="WdatePicker({el:'birthday',readOnly:true,dateFmt:'yyyy-MM-dd'})" class="Wdate" style="width:200px"/>
						</div>
					</div>
					<div class="row">
						<label>学历:</label>
						<div class="el">
							<select name="degree" id="degree">
								<option value="1" selected="selected">大专</option>
								<option value="2">本科</option>
								<option value="3">硕士</option>
								<option value="4">博士</option>
							</select>
						</div>
					</div>
					<div class="row">
						<label>手机:</label>
						<div class="el">
							<input type="text" name="cellphone" id="cellphone"/>
						</div>
					</div>
					<div class="row">
						<label>邮箱:</label>
						<div class="el">
							<input type="text" name="email" id="email">
						</div>
					</div>
					<div class="row">
						<label>爱好:</label>
						<div class="el">
							<input type="checkbox" name="hobbyArray" id="hobbyArray1" value="1"/>政治
							<input type="checkbox" name="hobbyArray" id="hobbyArray2" value="2"/>经济
							<input type="checkbox" name="hobbyArray" id="hobbyArray4" value="4"/>文化
							<input type="checkbox" name="hobbyArray" id="hobbyArray8" value="8"/>军事
						</div>
					</div>
					<div class="row">
						<label>个人简介:</label>
						<div class="el">
							<textarea name="intro" id="intro" rows="5" cols="50"></textarea>
						</div>
					</div>
					<div class="actions">
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