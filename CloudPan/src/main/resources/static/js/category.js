/*$(function(){
	var para = getParam();
	var category = para[category];
	loadImage("others");
});*/
//获取url参数
function getParam(){
	var res = {};
	var url = document.URL;
	var urlParam = url.split("?");
	if(urlParam && urlParam.length > 1){//有参数
		urlParam = urlParam[1];
		var attribute = urlParam.split("&");//各个参数
		for(var i=0;i<attribute.length;i++){
			var subAttr = attribute[i].split("=");//每个参数的key-value
			if(subAttr){
				var value = subAttr[1];
				var lastChar = value.substr(value.length-1,1);
				if(lastChar == '#'){
					value = value.substr(0,value.length-1);
				}
				res[subAttr[0]] = value;
			}
		}
	}
	return res;
}

$(function(){
    $("#avatsel1").click(function(){
        $("input[type='file']").trigger('click');
    });
    //input框点击选择文件
    $("#avatval").click(function(){
       // $("input[type='file']").trigger('click');
    });
    $("input[type='file']").change(function(){
    	$(".photo-listContainer").empty();
    	var pathVal = $("#avatval").val();
    	for(var i=0;i<this.files.length;i++){
    		var path = getObjectURL(this.files[i]);
    		if(pathVal){
    			pathVal = pathVal+"；"+this.files[i].name;
    		}else{
    			pathVal = this.files[i].name;
    		}
    		buildPhotoList(path);
    	}
    	$("#avatval").val(pathVal);
    });
});
//选择图片时临时显示要上传的图片
function buildPhotoList(path){
	var $imgListContainer = $(".photo-listContainer");
	var content = '<div class="media photo-list">'
				+'<img src="'+path+'"/>'
				+'</div>';
	$imgListContainer.append(content);
}
//点击弹出编辑信息框
function editInfomation(){
	 layer.open({
	        type: 1,
	        title: false,
	        closeBtn: 0,
	        shadeClose: true,
	        skin: 'yourclass',
	        content: $("#info_layer"),
	        success:function(){
	        	
	        }
	      });
}