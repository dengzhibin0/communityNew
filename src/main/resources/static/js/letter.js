$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	// 获取标题和内容
	var toName=$("#recipient-name").val();
	var content=$("#message-text").val();

	// 发送异步请求
	$.post(
		CONTEXT_PATH+"/letter/send",
		// 发送的数据
		{"toName":toName, "content":content},
		function (data){
			data=$.parseJSON(data);  // 转化为json对象

			if(data.code===0){
				$("#hintBody").text("发送成功！");
			}else{
				$("#hintBody").text(data.msg);
			}
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();
			}, 2000);
		}
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}