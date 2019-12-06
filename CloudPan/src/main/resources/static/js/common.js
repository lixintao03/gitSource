/**通用的ajax提交
 * type "GET" or "POST"
 * param 请求参数
 * url 请求url
 * callBackFlag 回调函数标识，指明出发什么操作 1:categorys查询
 * */
function ajaxCommonSubmit(type,param,url,callBackFlag){
	$.ajax({
		type : "GET",
		url : url,
		dataType : "json",
		data : param,
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			if(callBackFlag == '1'){
				computeCategories(data);
			}else if(callBackFlag == '2'){
				computeRecentImages(data);
			}
		},
		error : function() {
			alert("出错了！");
		}
	});
}

function getObjectURL(file) {
	var url = '';
	if(file){
		/*for(let i in file){*/
			if (window.createObjectURL != undefined) {
				url = window.createObjectURL(file);
			} else if (window.URL != undefined) {// mozilla(firefox)
				url = window.URL.createObjectURL(file);
			} else if (window.webkitURL != undefined) {// webkit or chrome  
				url = window.webkitURL.createObjectURL(file);
			}
		/*}*/
	}
	return url;
}