$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");  // 隐藏发布消息的框

	// // 在发送AJAX请求之前，将CSRF令牌设置到请求的消息头中
	// var token=$("meta[name='_csrf']").attr("content");
	// var header=$("meta[name='_csrf_header']").attr("content");
	// $(document).ajaxSend(function (e,xhr,options){
	// 	xhr.setRequestHeader(headerm,token);   // 设置请求头
	// });

	// 获取标题和内容
	var title=$("#recipient-name").val();
	var content=$("#message-text").val();

	// 发送异步请求
	$.post(
		CONTEXT_PATH+"/discuss/add",
		// 发送的数据
		{"title":title, "content":content},
		function (data){
			data=$.parseJSON(data);  // 转化为json对象
			// 在提示框中显示返回消息
			$("#hintBody").text(data.msg);

			$("#hintModal").modal("show");  // 发布后的提示框显示
			// 2秒后自动关闭
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 刷新页面
				if(data.code==0){
					window.location.reload();
				}
			}, 2000);
		}
	);
}