					var
					$gallery = $("#gallery"),
					$galleryImg = $("#galleryImg"),
					$uploaderFiles = $("#uploaderFiles");
					var index; //第几张图片  
					$uploaderFiles.on("click", "li", function() {
						index = $(this).index();
						$galleryImg.attr("style", this.getAttribute("style"));
						$galleryImg.css("background-size","contain");
						$gallery.fadeIn(100);
					});
					
					//点击消失
					$gallery.on("click", function() {
						$gallery.fadeOut(100);
					});