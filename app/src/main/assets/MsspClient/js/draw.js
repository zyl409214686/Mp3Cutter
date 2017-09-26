
var CANVAS_WIDTH = 0;
var CANVAS_HEIGHT = 0;
var MAX_LINE_WIDTH = 3;
var MIN_LINE_WIDTH = 3;
var MAX_VELOCITY = 10;
var context = null;
var HAS_IMAGE=false;

var signImageBase64;


var scale=1;
var drawInterval=20;//绘图更新时间

setTimeout(function(){$(init)},300);
//$(init);

var paths = [];



function init() {
		
	
	// 初始化笔锋
	
		$("#middle").attr("checked","checked");
		$("#slim").removeAttr("checked");
	  $("#fat").removeAttr("checked");
	
		
	//初始化颜色
	
	
	CANVAS_WIDTH = $(window).width();
	CANVAS_HEIGHT = $(window).height()*1.2;
	
	var canvas = document.getElementById('myCanvas');
	
       canvas.height=CANVAS_HEIGHT;
       canvas.width =CANVAS_WIDTH;
        context = canvas.getContext('2d');
	
  	var image = new Image();
	image.src = parent.getSignImageFromPage();
	if(image.src!="data:image/png;base64,null"){ 
	   preImage(image.src,function(){  
	   //context.drawImage(this,0,0,this.width*scale,this.height*scale);  
	   context.drawImage(this,0,0,canvas.width,canvas.height);  
	   HAS_IMAGE=true;
    });
	}
	
	context.lineWidth = MAX_LINE_WIDTH;
	context.fillStyle = '#DC143C';
	context.strokeStyle = '#DC143C';
	context.lineCap = 'round';
	context.lineJoin = 'miter';

	canvas.addEventListener('touchmove', touchMove, false);
	canvas.addEventListener('touchstart', touchStart, false);
	canvas.addEventListener('touchend', touchEnd, false);

$('#refresh').click(function() {
		//以下两行为测试代码
	
		$(canvas).animate({opacity: 0}, 500, function() {
			context.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		
			paths = [];
			
			$(canvas).css('opacity', 1);
		});
		
		$('#saved').animate({opacity: 0}, 500, function() {
			$(this).remove();
		});
		HAS_IMAGE=false;
		return false;
	});

$('#save').click(function(){

	var canvas = document.getElementById('myCanvas');
	var image = new Image();
	image.src = canvas.toDataURL("image/png");
	
    var newcanvas = document.createElement('canvas');
	newcanvas.height=CANVAS_HEIGHT/scale;
    newcanvas.width =CANVAS_WIDTH/scale;
    var ctx=newcanvas.getContext('2d');
	
	
	     preImage(image.src,function(){ 
         newcanvas.getContext('2d').drawImage(this, 0, 0,this.width/scale,this.height/scale);
         var imageScale = new Image();
         imageScale.src = newcanvas.toDataURL("image/png");
		 
		if(HAS_IMAGE){
		//parent.saveSignImageInPage(imageScale.src);	
		parent.request('signet','updatepersonalseal',imageScale.src);
		//parent.storeSignImage(imageScale.src);
		//parent.back();
	   } else {
		//parent.showNullImgErr();
		parent.request('signet','alertError','签名图片不能为空');
	    }
      }); 	
});





$('#setBlack').click(function(){
		
   context.fillStyle = '#000000';
	 context.strokeStyle = '#000000';

});

$('#setRed').click(function(){
	
   context.fillStyle = '#DC143C';
	 context.strokeStyle = '#DC143C';

});

$('#setBlue').click(function(){
	
	
   context.fillStyle = '#295dab';
	 context.strokeStyle = '#295dab';

});

$('#slim').click(function(){
	
	
	$("#slim").attr("checked","checked");
	$("#middle").removeAttr("checked");
	$("#fat").removeAttr("checked");

	 MAX_LINE_WIDTH = 3;
   MIN_LINE_WIDTH = 3;
   MAX_VELOCITY = 10;
});

$('#middle').click(function(){
	
	$("#middle").attr("checked","checked");
	$("#slim").removeAttr("checked");
	$("#fat").removeAttr("checked");
	 MAX_LINE_WIDTH = 3;
   MIN_LINE_WIDTH = 3;
   MAX_VELOCITY = 10;
	
	
});

 $('#fat').click(function(){
	
	$("#fat").attr("checked","checked");
	$("#middle").removeAttr("checked");
	$("#slim").removeAttr("checked");
	
	MAX_LINE_WIDTH = 12;
  MIN_LINE_WIDTH = 2;
  MAX_VELOCITY = 10;
	
}); 

function preImage(url,callback){  
     var img = new Image(); //创建一个Image对象，实现图片的预下载  
     img.src = url;  
      
	 if ( (url.indexOf("null")<=0) && (url.length>50) ) //判断图片非空
	 {
	    if (img.complete) 
	 { 
	 	//如果图片已经存在于浏览器缓存，直接调用回调函数  
     callback.call(img);  
     return; // 直接返回，不用再处理onload事件  
   }  
  
     img.onload = function () 
      { //图片下载完毕时异步调用callback函数。  
         callback.call(img);//将回调函数的this替换为Image对象  
      };  
   }
}



function writeObj(obj){ 
    var description = ""; 
    for(var i in obj){   
        var property=obj[i];   
        description+=i+" = "+property+"\n";  
    }   
    alert(description); 
} 


setInterval(draw, drawInterval);
	
	
}
function clean() 
{
		document.getElementById("refresh").click();
				
}

function save() 
{
		document.getElementById("save").click();
}

function touchStart(event) {
	
	event.preventDefault();
	HAS_IMAGE=true;
	for (var i = 0; i < event.touches.length; i++) {
		paths.push({
			id: event.touches[i].identifier,
			points: [{
				x: event.touches[i].pageX,
				y: event.touches[i].pageY,
				timestamp: new Date().getTime(),
				drawn: false
			}],
			complete: false
		});
	}
}

function touchEnd(event) {
	event.preventDefault();

	for (var i = 0; i < event.changedTouches.length; i++) {
		for (var j = 0; j < paths.length; j++) {
			if (paths[j].id == event.changedTouches[i].identifier) {
				paths[j].id = null;
				paths[j].complete = true;
			}
		}
	}
}

function touchMove(event) {
	event.preventDefault();

	for (var i = 0; i < event.touches.length; i++) {
		for (var j = 0; j < paths.length; j++) {
			if (paths[j].id == event.touches[i].identifier) {
				paths[j].points.push({
					x: event.touches[i].pageX,
					y: event.touches[i].pageY,
					drawn: false,
					timestamp: new Date().getTime()
				});
			}
		}
	}
}

function draw() {
	//alert('image')
	// Limit the amount of time allowed for a draw to take place
	var DRAW_TIME_THRESHOLD = 10;
	var start = new Date();
	
	
	for (var i = 0; i < paths.length && new Date() - start < DRAW_TIME_THRESHOLD; i++) {
		var firstPoint = true;
		var points = paths[i].points;
		
		if (points.length > 1 && points[points.length - 1].drawn == false) {
			var prevLineWidth = MAX_LINE_WIDTH;
			
			context.beginPath();
	
			for (var j = 1; j < points.length; j++) {
				var pointDistance = Math.sqrt(Math.pow(points[j].x - points[j - 1].x, 2) + Math.pow(points[j].y - points[j - 1].y, 2));
				var lineWidth = Math.max(MIN_LINE_WIDTH, (MAX_VELOCITY - pointDistance) / MAX_VELOCITY * MAX_LINE_WIDTH);
				
				if (Math.abs(lineWidth - prevLineWidth) > 1) {
					lineWidth = prevLineWidth + (lineWidth - prevLineWidth) / Math.abs(lineWidth - prevLineWidth);
				}
				
				lineWidth = Math.round(lineWidth);
				
				if (firstPoint && points[j].drawn == false) {
					firstPoint = false;
					
					context.moveTo(points[j - 1].x, points[j - 1].y);
					points[j - 1].drawn = true;

					context.lineWidth = lineWidth;
					context.lineTo(points[j].x, points[j].y);
					
					
				}
				else if (points[j].drawn == false) {
					var pointDistance = Math.sqrt(Math.pow(points[j].x - points[j - 1].x, 2) + Math.pow(points[j].y - points[j - 1].y, 2));
					context.lineWidth = lineWidth;
					context.lineTo(points[j].x, points[j].y);
				
				}
				
				prevLineWidth = lineWidth;
				
				points[j].drawn = true;
			}
	
			context.stroke();
			context.closePath();
		}
		else if (paths[i].complete && points[0].drawn == false) {
			context.arc(points[0].x, points[0].y, MAX_LINE_WIDTH / 2, 0, Math.PI * 2, false);
			context.closePath();
			context.fill();
			
			points[0].drawn = true;
		}
	}
}

function debug(text, append) {
	var debug = document.getElementById('debug');
	
	if (typeof append != 'undefined' && append) {
		debug.innerHTML += text;
	}
	else {
		debug.innerHTML = text;
	}
}
