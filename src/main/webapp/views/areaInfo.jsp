<%@page pageEncoding="UTF-8" language="java"
	contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>

	<script type="text/javascript"
		src="http://code.jquery.com/jquery-1.8.0.min.js"></script>

	<script src="http://echarts.baidu.com/build/dist/echarts.js"></script> 
<body> 
 <table border="1" cellpadding="10" cellspacing="0">  
            <tr>  
                <th>名称</th>  
                <th>区域</th> 
                <th>地址</th>  
                <th>状态</th>  
                <th>类型</th>  
                <th>价格</th>  
                <th>两天价格波动</th>
                <th>7天价格波动</th>
                <th>操作</th>
            </tr>  
              
            <c:forEach items="${list}" var="house">  
                <tr>  
                    <td>${house.name }</td>  
                    <td>${house.areaName }</td>
                    <td>${house.address }</td>  
                    <td>${house.state }</td>  
                    <td>${house.describe }</td>  
                    <td>${house.price }</td>  
                    <c:if test="${house.isChangePrice == 0}">
                    	<td>无变化</td>
                    </c:if>
                    <c:if test="${house.isChangePrice == 1}">
                    	<td>上涨</td>
                    </c:if>
                    <c:if test="${house.isChangePrice == 2}">
                    	<td>下跌</td>
                    </c:if>
                    <c:if test="${house.isChange7DayPrice == 0}">
                    	<td>无变化</td>
                    </c:if>
                    <c:if test="${house.isChange7DayPrice == 1}">
                    	<td>上涨</td>
                    </c:if>
                    <c:if test="${house.isChange7DayPrice == 2}">
                    	<td>下跌</td>
                    </c:if>
                    <c:if test="${house.isPrice == 1}">
                    	<td><a onclick="view('${house.name}');">查看历史价格</a></td>
                    </c:if>
                </tr>  
            </c:forEach>  
        </table>   
</body>
	<script type="text/javascript">  

		 
		
		function view(name) {
			window.open("/test/info?name="+name);
		}
	</script>
</html>