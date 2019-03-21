
function edit(url,locationhref){
	var pattern = /^1[34578]\d{9}$/;  
	var name = $.trim($("#name").val());
	var  tel= $.trim($("#tel").val());
	var  area = $.trim($("#area").val());
	var schoolName=$.trim($("#schoolName").val());
	var  addr = $.trim($("#addr").val());
	if(name.length == 0){
		$("#nameEor").html("姓名不能为空");
		$("#name").focus();
		return;
	}else{
		$("#nameEor").html("");
	}
	if(pattern.test(tel) == false){
		$("#telEor").html("请重新输入手机号");
		$("#tel").focus();
		return;
	}else{
		$("#telEor").html("");
	}
	if(area.length == 0 || schoolName.length == 0 || addr.length == 0 ){
		$("#addrEor").html("地址不能为空");
		return;
	}else{
		$("#nameEor").html("");
		$("#telEor").html("");
		$("#addrEor").html("");
	$.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: url,//url
            data: $('#form').serialize(),
            success: function (result) {
            	console.log(result);//打印服务端返回的数据(调试用)
            	if(result.status == 200){
            		var reurl=locationhref + String(result.data);
            		location.href= reurl;
            	 }
            	;
            },
            error : function() {
            	$("#msg").html("登录异常，请联系管理员s")
            }
        });
	}
 }


