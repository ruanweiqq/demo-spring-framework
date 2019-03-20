<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>糟糕，系统开了个小差！</title>
</head>
<body>
<h2 style="color: red">
    糟糕，系统开了个小差！
</h2>
<%--状态码： <%=request.getAttribute("javax.servlet.error.status_code")%> <br>
异常信息： <%=request.getAttribute("javax.servlet.error.message")%> <br>
异常： <%=request.getAttribute("javax.servlet.error.exception_type")%> <br>--%>
成功： <%=request.getAttribute("success")%> <br>
返回码： <%=request.getAttribute("code")%> <br>
返回信息： <%=request.getAttribute("message")%> <br>
</body>
</html>