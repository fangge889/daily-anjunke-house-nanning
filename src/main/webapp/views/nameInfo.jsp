<%@page pageEncoding="UTF-8" language="java"
	contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>

	<script type="text/javascript"
		src="http://code.jquery.com/jquery-1.8.0.min.js"></script>

	<script src="http://echarts.baidu.com/build/dist/echarts.js"></script>
<body>
	<!--   折线统计图 -->
	<div class="row">
		<div id="main" style="width: 1800px; height: 350px; margin-top: 80px;"></div>
	</div>

	<script type="text/javascript">
		require.config({
			paths : {
				echarts : 'http://echarts.baidu.com/build/dist'
			}

		});

		// 使用 
		// 使用柱状图就加载bar模块，按需加载 
		require([ 'echarts', 'echarts/chart/line' ],

		function(ec) {
			// 基于准备好的dom，初始化echarts图表 
			var myChart = ec.init(document.getElementById('main'));
			option = ${option};

			// 为echarts对象加载数据
			myChart.setOption(option);
		}

		);
	</script>
</body>
</html>