/**
 * 根据weui-media改变Tab的样式
 */
			$(function(){
				var $elements=$('.weui-media-box');
				if($elements.length>=6){
					var $weuiTabber=$('.weui-tabbar');
					$weuiTabber.css('position','relative');
					console.log($weuiTabber.css('position'));
				}
			})
