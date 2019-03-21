//
function assign(url,toIndex){
    $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: url,//url
            data: $('#form').serialize(),
            beforeSend: function(){
            	$.showLoading("正在提交");
            },
           complete: function(){
        	    $.hideLoading();
           },
            success: function (result) {
                if (result.status == 200) {
                	alert("提交成功")
                	setTimeout(function(){
            		location.href=toIndex;
            	   },1000)
                }else if(result.status == 203){
                	alert(result.msg)
                };
            },
            error : function() {
            	alert("重试!1!");
            }
        });
    }

//接受
function receipt(url,toIndex){
    $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: url,//url
            data: $('#form').serialize(),
            beforeSend: function(){
            	$.showLoading("正在提交");
            },
           complete: function(){
        	    $.hideLoading();
           },
            success: function (result) {
                if (result.status == 200) {
                	alert("提交成功")
                	setTimeout(function(){
            		location.href=toIndex;
            	   },1000)
                }else if(result.status == 203){
                	alert(result.msg)
                };
            },
            error : function() {
            	alert("重试!1!");
            }
        });
    }

//完成
function finish(url,toIndex){
	var form2=new FormData(document.querySelector("#form"));
    $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: url,//url
            data:form2,
            processData:false,
            contentType:false,
            beforeSend: function(){
            	$.showLoading("正在提交");
            },
           complete: function(){
        	    $.hideLoading();
           },
            success: function (result) {
                if (result.status == 200) {
                	alert("提交成功")
                	setTimeout(function(){
            		   location.href=toIndex;
            	   },1000)
                }else if(result.status == 201){
                	alert(result.msg)
                };
            },
            error : function() {
            	alert("重试!1!");
            }
        });
    }


//维修提交
function repair(url,indexpath){
	$.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: url,//url
            data: $('#repairForm').serialize(),
            beforeSend: function(){
            	$.showLoading("正在提交");
            },
           complete: function(){
        	    $.hideLoading();
           },
            success: function (result) {
            	//200成功，201错误信息
            	if(result.status == 200){
            	   alert("提交成功");
            	   setTimeout(function(){
            		   location.href=indexpath;
            	   },1000)
            	}
            	if(result.status == 201){
            		 alert(result.msg); 
            	};
            },
            error : function() {
        		alert("提交异常"); 
            }
        });
 }


//登录
function login(url,toIndex){
    var username = $.trim($("#username").val());
    var password = $.trim($("#password").val());
    if(username == ""){
    	$("#msg").html("请输入用户名")
    	$("#username").focus;
    }else if(password == ""){
    	$("#msg").html("请输入密码")
    	$("#password").focus;
    }else{
    	$("#msg").html("")
    $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: url,//url
            data: $('#form').serialize(),
            success: function (result) {
                alert(result.status);//打印服务端返回的数据(调试用)
                if (result.status == 200) {
                	var reurl=toIndex + result.data.id;
                	location.href=reurl;
                }else if(result.status == 202){
                	$("#msg").html("帐号或密码错误")
                }else if(result.status == 203){
                	$("#msg").html("帐号密码不能为空")
                }
                ;
            },
            error : function() {
            	$("#msg").html("登录异常，请联系管理员s")
            }
        });
    }
}
