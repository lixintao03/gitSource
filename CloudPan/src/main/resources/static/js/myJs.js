	var baseUrl="http://localhost:8888/";
	var loginAccount;
	function selectImg(){
		$(".closeLayer").css("display","block");
		$(".closeLayer").css({"-webkit-animation": "tada 1s .2s ease 100","-moz-animation": "tada 1s .2s ease 100"})
	} 
	function cancelSelect(){
		$(".closeLayer").css("display","none");
		$(".closeLayer").css({"-webkit-animation": "","-moz-animation": ""});
	} 
	$(function(){
		var category = getParam();
		if(category && category.category){//指定主题画册内容
			var val = category['category'];
			loadImage(val);
		}else{
			loadTopImage(3);//main页面初始化
			loadCategory();
			getRecentTopNImage(8);
		}
		var token = $.cookie("token");//获取token
		if(token){
			var userInfo = $.cookie(token);
			if(userInfo){
				userInfo = decodeURI(userInfo);
				console.log(userInfo)
				loginAccount = JSON.parse(userInfo);
				$("#userName").html(loginAccount.name);
			}
		}
	});
	$("#form0").change(function() {
	/*	var objUrls = getObjectURL(this[1].files[0]);//获取文件信息  
		//var objUrls = $("#avatval").val()
		console.log("objUrl = " + objUrl);
		if (objUrls) {
			for(let i in objUrls){
				$("#img0").attr("src", objUrl);
			}
		}*/
	});
	
	function loadTopImage(count){
		//获取当前都有哪些内容，查询fileInfo获取文件结果
		$.ajax({
			type : "GET",
			url : baseUrl+"getTopImages?count="+count+"&userId=1001",
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(data) {
				var fileNames = data.fileNames;
				/*for(var i=0; i<fileNames.length; i++){
					computeTopImage(fileNames[i].description,fileNames[i].fileId,fileNames[i].fileName);
				}*/
				
			},
			error : function() {
				alert("出错了！");
			}
		});
	}
	//拼接main页面中的top滚动框内容
	function computeTopImage(description,fileId,fileName){
		var elem = '<div class="item">'
					+'<img id="'+ fileId +'" src="img/body1.jpg" alt="'+fileName+'">'
					+'<div class="carousel-caption">'
					+ description
					+'</div>'
					+'</div>';
		var $carousel = $(".carousel-inner");
		$carousel.append(elem);
		$("#"+fileId).attr("src", baseUrl+"queryOneFile?fileId="+fileId);
	}
	
	//拼接个人category页面中显示内容
	function choose(fileName,fileId){
		var $container = $("#dowebok");
		var $container_a =  $container.children();
		var eleCount = $container_a.length;
		var content ='<li><img class="liImg" id="'+fileId+'" data-original="" src="" alt="'+fileName+'"/>'
						+'<img class="closeLayer" src="../img/del0.jpg" onClick="remove(\''+fileId+'\')">'
						+'</li>';
		
		$container.append(content);
		$("#"+fileId).attr("src", baseUrl+"queryOneFile?fileId="+fileId);
		$("#"+fileId).attr("data-original", baseUrl+"queryOneFile?fileId="+fileId);
		
	}
	function loadCategory(){
		var param = {};
		var type = "GET";
		var url = baseUrl+'getCategorys';
		var callBackFlag = '1';
		ajaxCommonSubmit(param,type,url,callBackFlag);
	}
	//拼装categories显示内容
	function computeCategories(data){
		var $categoriesDiv = $("#categoriesDiv");
		for(var i=0;i<data.length;i++){
			var content = '<div class="category-block" id="'+data[i].categoryName+'">'
			+'<a href="http://localhost:8888/category?category='+data[i].categoryName+'">'
			+'<span>'+data[i].categoryName+'</span>'
			+'</a> </div>';
			$categoriesDiv.append(content);
			$(".category-block").css('width','24%');
			$(".category-block").css('background-image','');
			$("#"+data[i].categoryName).css("background-image","url(http://localhost:8888/getTopNCategoryImage?topN=1&category="+data[i].categoryName+")");
		}
	}
	//获取最近的Images
	function getRecentTopNImage(topN){
		var param = {};
		var type = "GET";
		param.count = topN;
		var url = baseUrl+'getTopImages';
		var callBackFlag = '2';
		ajaxCommonSubmit(type,param,url,callBackFlag);
	}
	function computeRecentImages(data){
		data = data.fileNames;
		var $parentDiv = $("#recent-photos");
		for(var i=0;i<data.length;i++){
			var content = '<div class="item" id="'+data[i].fileId+'">'
				+'</div>';
			$parentDiv.append(content);
		}
		initOwlCarousel();
		//不知道为什么先设置背景图片再初始化滚动框就是显示不出来背景图
		for(var i=0;i<data.length;i++){//初始化滚动框之后为每个滚动框设置背景
			$(".owl-item #"+data[i].fileId).css("background-image","url(http://localhost:8888/getTopNImage?topN="+(i+1)+")");
		}
	}
	function alertMsg(){
		alert("点击了！");
	}
	function upload(){
		var file = $("#file")[0].value;
		if(!file){
			alert("请选择文件进行上传");
			return;
		}
		var para = getParam();
		var category = para['category'];
		$("#category").val(category);
		var option = {
				url : baseUrl+"uploadFile",
				timeout : 40000,
				type : 'POST',
				dataType : 'json',
				headers : {
					"ClientCallMode" : "ajax"
				}, // 添加请求头部
				success : function(data) {
					if(data.ok){
						var fileNames = data.fileNames;
						var fileIds = data.fileIds;
						for(var i=0;i<fileIds.length;i++){
							choose(fileNames[i],fileIds[i]);
						}
						initViewer();
						$(".photo-listContainer").empty();
					}
					alert(data.message);
				},
				error : function(data) {
					alert(JSON.stringify(data) + "--上传失败,请刷新后重试");
				}
			};
			$("#form0").ajaxSubmit(option);
	}
	function initViewer(){
		$('img:not(.closeLayer)').viewer({
		    url: 'data-original',
		});
	}
	//加载
	function loadImage(category){
		//获取当前都有哪些内容，查询fileInfo获取文件结果
		$.ajax({
			type : "GET",
			url : baseUrl+"getImages?category="+category,
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(data) {
				var fileNames = data.fileNames;
				for(var i=0; i<fileNames.length; i++){
					choose(fileNames[i].fileName,fileNames[i].fileId);
				}
				rightClickEvent();
				initViewer();
			},
			error : function() {
				alert("出错了！");
			}
		});
	}
	
	function rightClickEvent(id){
		$('img').contextMenu('context-menu-1', {
            'Context Menu Item 1': {
                click: function(element) {  // element is the jquery obj clicked on when context menu launched
                   	this;
                	alert('Menu item 1 clicked');
                },
                klass: "menu-item-1" // a custom css class for this menu item (usable for styling)
            },
            'Context Menu Item 2': {
                click: function(element){ alert('second clicked'); },
                klass: "second-menu-item"
            }
        });
	}
	//选择删除内容
	function remove(fileId){
		//获取当前都有哪些内容，查询fileInfo获取文件结果
		$.ajax({
			type : "GET",
			url : baseUrl+"removeImages?fileId="+fileId,
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(data) {
				if(data.ok){
					$("#"+fileId).parent()[0].remove();
					initViewer();
				}
				alert(data.message);
			},
			error : function() {
				alert("出错了！");
			}
		});
		
	}
	function initOwlCarousel(){
		$("#recent-photos").owlCarousel({
			autoPlay: 3000,
			items : 4,
			itemsDesktop : [1199, 3],
			itemsDesktopSmall : [990, 2],
			itemsTablet : [768, 1]
		});
	}
	/*$(document).ready(function() {
		$("#recent-photos").owlCarousel({
			autoPlay: 3000,
			items : 4,
			itemsDesktop : [1199, 3],
			itemsDesktopSmall : [990, 2],
			itemsTablet : [768, 1]
		});
	});*/