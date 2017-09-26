// JavaScript Document

var signImageBase64;
function request(sp ,invoke ,myJSONText)
  {
	// 1 android 2 ios
	try {
		window.signet.transmit(sp,invoke,myJSONText);
	} catch(Excep) {
		 window.location.href = "#/signet?" + invoke + ":?" + myJSONText;
	}

}

function setUserMobile(mobile){
	var userMobile=mobile;
	console.log(userMobile);
	document.getElementById("sdkActivePhoneNumber").innerHTML=mobile.substring(0,3)+"****"+mobile.substring(7,11);
		
}

function revertScreenCallBack(signImg){
	
	console.log(signImg);
	signImageBase64=signImg;
}

function getSignImageFromPage(){
	
	return signImageBase64;
}

function signSettingBack(){
	
	request('signet','signSettingBack','');


}

function captureIdCardCallBack(idCardStr){
	
	 var idCardInfo=JSON.parse(idCardStr);
	  console.log("idCardInfo : "+idCardStr);
	  console.log("idCardInfo.name : "+idCardInfo.name);
	  document.getElementById("idcard_name").innerHTML=idCardInfo.name;
	  document.getElementById("idcard_numb").innerHTML=idCardInfo.cardNum;
	  document.getElementById("idcard_val").innerHTML=idCardInfo.period;
	  console.log("finish");
}

function onSubmitIDInfo(){
	request('signet','onSubmitIDInfo','');
}

function activeUser(){
	
	 var otpCode=document.getElementById("otp").value;
	 if(otpCode.length!=6){
		 request('signet','alertError','请输入六位短信验证码');
		 return;
	 }
	 var pin1="";
	 var pin2="";
	 if(document.getElementById("userpin1")&&document.getElementById("userpin2")){
		 pin1=document.getElementById("userpin1").value;
		 pin2=document.getElementById("userpin2").value;
		 
		 if(pin1.length!=6||pin2.length!=6){
			 request('signet','alertError','请输入6位数字口令');
			 return;
		 }
		 if(isNaN(pin1)||isNaN(pin2)){
			 request('signet','alertError','请输入6位数字口令');
			 return;
		 }
		 if(pin1!=pin2){
			 request('signet','alertError','口令输入不一致');
		 }
	 }
	 
	 
	 
	 var paramObj=new Object();
	 paramObj.otp=otpCode;
	 paramObj.pinOne=pin1;
	 paramObj.pinTwo=pin2;
	 
	 request('signet','activeUser',JSON.stringify(paramObj));	
	
}

function oldUserCheck(){
	
	var paramObj=new Object();
	paramObj.name=document.getElementById("name").value;
	paramObj.idCardNumber=document.getElementById("paperid").value;
	request('signet','userActiveDevice',JSON.stringify(paramObj));
	
}

//企业用户出发:检测企业身份有效性
function enterpriseCheck(){	

	var paramObj=new Object();
	paramObj.ORG=document.getElementById("enterpriseorg").value;
	paramObj.name=document.getElementById("name").value;
	paramObj.idCard=document.getElementById("paperid").value;
	
	request('signet','enterpriseActiveDevice',JSON.stringify(paramObj));
	
}

function signSettingBack(){
	request('signet','signSettingBack','');
}

function inputNumber(){
	  request('signet','inputNumber',document.getElementById("phonenumber01").value);
}



function active_sendmsg(){
	request('signet','reactive','');
	sdkTimeFun();
}