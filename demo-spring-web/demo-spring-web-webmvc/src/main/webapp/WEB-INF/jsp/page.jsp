<%@page import="org.ruanwei.demo.springframework.web.core.Page"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<script type="text/javascript">
	function gotoPage(pageNum) {
		$('#curPage')[0].value = pageNum;
		$('#listForm')[0].submit();
		return;
	}
</script>

<input type="hidden" name="pageSize" id="pageSize" value="${page.pageSize}"/>
<input type="hidden" name="curPage" id="curPage" value="1"/>
<div class="pre-next">
第${page.curPage}/${page.totalPage}页&nbsp;
共${page.totalRecord}条&nbsp;
<c:choose>
<c:when test="${page.curPage>1}">
<span><a href="javascript:gotoPage(1);">首页</a></span>
<span><a href="javascript:gotoPage(<c:out value="${page.curPage-1}"></c:out>);">上一页</a></span>
</c:when>
<c:otherwise>首页&nbsp;上一页</c:otherwise>
</c:choose>
<c:choose>
<c:when test="${page.curPage<page.totalPage}">
<span><a href="javascript:gotoPage(<c:out value="${page.curPage+1}"></c:out>);">下一页</a></span>
<span><a href="javascript:gotoPage(<c:out value="${page.totalPage}"></c:out>);">末页</a></span>
</c:when>
<c:otherwise>下一页&nbsp;末页</c:otherwise>
</c:choose>&nbsp;
第
<select onchange="gotoPage(this.value);">
<%
Page p = (Page)request.getAttribute("page");
for(int i=1;i<=p.getTotalPage();i++){
	if(i==p.getCurPage()){
%>
<option value="<%=i%>" selected><%=i%></option>
<%		
	}else{
%>
<option value="<%=i%>"><%=i%></option>
<%	
	}
}
%>
</select>
页
</div>