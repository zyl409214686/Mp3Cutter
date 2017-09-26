

//验证输入框
var panduanArray=new Array(false,false,false);
function sdkPanduan(n,lengths){
	if($(".sdk_input"+n).val().length==lengths){
		$(".sdk_ts"+n).hide();
		panduanArray[n]=true;		
	}else if($(".sdk_input"+n).val().length==0){
		$(".sdk_ts"+n).hide();
		panduanArray[n]=false;
	}else{
		$(".sdk_ts"+n).show();
		panduanArray[n]=false;
	}
	
	if(n==3){
		if(panduanArray[n]){
			$(".btn-primary").attr("disabled",false);
		}else{
			$(".btn-primary").attr("disabled",true);
		}
	}else if(n==4){
		if(panduanArray[n]&&(String($(".sdk_input"+n).val()).charAt(0)==1)){
			$(".btn-primary").attr("disabled",false);
		}else{
			$(".btn-primary").attr("disabled",true);
		}
	}else{		
		if(panduanArray[0]&&panduanArray[1]&&panduanArray[2]){
			$(".btn-primary").attr("disabled",false);
		}else{
			$(".btn-primary").attr("disabled",true);
		}
	}
}


//获取验证码
var sdk_time_num=59;
var sdk_time_interval;
function sdkTimeFun(){
	$("#sdk_time").unbind();
	$("#sdk_time").html(sdk_time_num-- +" 秒");		
	sdk_time_interval=setInterval(function(){
		if(sdk_time_num>0){
			$("#sdk_time").html(sdk_time_num-- +" 秒");
		}else{
			clearInterval(sdk_time_interval);
			$("#sdk_time").html("获取验证码");
			sdk_time_num=59;
			$("#sdk_time").bind("click",function(){active_sendmsg()});
		}
	},1000);
}


//olduser姓名和身份证验证
var olderUserNameToolean=new Array();
var olderIdCardToolean=false;
function sdkOlduserPuanduan(n){
	if($(".sdk_input"+n).val().length>=2){
		$(".sdk_ts"+n).hide();
		olderUserNameToolean[n]=true;		
	}else if($(".sdk_input"+n).val().length==0){
		$(".sdk_ts"+n).hide();
		olderUserNameToolean[n]=false;
	}else{
		$(".sdk_ts"+n).show();
		olderUserNameToolean[n]=false;
	}
	if($(".sdk_input6").length==0){
		olderUserNameToolean[6]=true;
	}
	if(olderUserNameToolean[5]&&olderUserNameToolean[6]&&olderIdCardToolean){
		$(".btn-primary").attr("disabled",false);
	}else{
		$(".btn-primary").attr("disabled",true);
	}
}

function isIdCardNo(num)
{
    if( typeof(num) == undefined )
        return false;
    if( num == null )
        return false;
	var factorArr = new Array(7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2,1);
	var error;
	var varArray = new Array();
	var intValue;
	var lngProduct = 0;
	var intCheckDigit;
	var intStrLen = num.length;
	var idNumber = num;
	// initialize
	if (intStrLen != 18) {

		return false;
	}
	// check and set value
	for(i=0;i<intStrLen;i++) {
		varArray[i] = idNumber.charAt(i);
		if ((varArray[i] < '0' || varArray[i] > '9') && (i != 17)) {

			return false;
		} else if (i < 17) {
			varArray[i] = varArray[i]*factorArr[i];
		}
	}
	if (intStrLen == 18) {
		//check date
		var date8 = idNumber.substring(6,14);
		if (checkDate(date8) == false) {

			return false;
		}
		// calculate the sum of the products
		for(i=0;i<17;i++) {
			lngProduct = lngProduct + varArray[i];
		}
		// calculate the check digit
		intCheckDigit = 12 - lngProduct % 11;
		switch (intCheckDigit) {
			case 10:
				intCheckDigit = 'X';
				break;
			case 11:
				intCheckDigit = 0;
				break;
			case 12:
				intCheckDigit = 1;
				break;
		}
		// check last digit
		if (varArray[17].toUpperCase() != intCheckDigit) {

			return false;
		}
	}
	else{        //length is 15
		//check date
		var date6 = idNumber.substring(6,12);
		if (checkDate(date6) == false) {

			return false;
		}
	}
	return true;
}

function checkDate(date)
{
	return true;
}
function checkIdCard(){
	var cardnumber=$(".idcardinput").val();
	var idcardboolen=isIdCardNo(cardnumber);
	if(idcardboolen){
		$(".sdk_ts_idcard").hide();
		olderIdCardToolean=true;
	}else{
		$(".sdk_ts_idcard").show();
		olderIdCardToolean=false;
	}
	if($(".sdk_input6").length==0){
		olderUserNameToolean[6]=true;
	}
	if(olderUserNameToolean[5]&&olderUserNameToolean[6]&&olderIdCardToolean){
		$(".btn-primary").attr("disabled",false);
	}else{
		$(".btn-primary").attr("disabled",true);
	}
}




$(function(){
	sdkTimeFun();
});

