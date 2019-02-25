// 根据选择的目录生成菜单
function changeType() {
	var type = $("#type").val();
	if (type == 1) {
		$("#parentmenu").css("display", "none");
		$("#parentId").attr("disabled", "disabled");
	} else {
		type = type == '2' ? '1' : '2';
		// 需要通过ajax加载对应的菜单列表
		$.ajax({
			type : 'POST',
			url : "getwebmenu",
			data : "type=" + type,	
			dataType : "json",
			success : function(data) {
				$("#parentmenu").show();
				$("#parentId").attr("disabled", false);
				var sale = "<option value=''>----选择菜单----</option>";
				if(data.data!=null){
					$.each(data.data, function(index, item) {
						sale += "<option value=" + item.id + ">" + item.name
								+ "</option>";
					});
				}
				$("#parentId").html(sale)
			}
		});

	}

}

$(function() {
	$.validator.setDefaults({
		highlight : function(a) {
			$(a).closest(".form-group").removeClass("has-success").addClass(
					"has-error")
		},
		success : function(a) {
			a.closest(".form-group").removeClass("has-error").addClass(
					"has-success")
		},
		errorElement : "span",
		errorPlacement : function(a, b) {
			if (b.is(":radio") || b.is(":checkbox")) {
				a.appendTo(b.parent().parent().parent())
			} else {
				a.appendTo(b.parent())
			}
		},
		errorClass : "help-block m-b-none",
		validClass : "help-block m-b-none"
	});
	$().ready(function() {
		$("#commentForm").validate();
		var a = "<i class='fa fa-times-circle'></i> ";
		$("#webMenuForm").validate({
			rules : {
				name : {
					required : true,
					minlength : 2
				},
				resUrl : {
					required : true,
				},
				parentId : {
					required : true,
				},

			},
			messages : {
				name : {
					required : a + "请输入菜单名称",
					minlength : a + "菜单必须两个字符以上"
				},
				resUrl : {
					required : a + "请输入访问路径"
				},
				parentId : {
					required : a + "请输入上级菜单"
				}
			}
		});
	});
})
