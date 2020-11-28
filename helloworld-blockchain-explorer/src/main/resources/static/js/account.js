//生成账户
function generateAccount(){
	var account = {};
	$.ajax({
	    type: "post",
	    url: "/Api/Blockchain/GenerateAccount",
	    contentType: "application/json",
	    data: `{}`,
	    dataType: "json",
	    async: false,
	    success: function (data) {
	        account = data.result.account;
			// console.log(account);
	    },
	    error: function (e) {
	    }
	});
	var getContent = '<h2 class="red">提示: 请在您信任的站点生成账户！请您保存好自己的账户!网站不会保存您的账户信息，且不对您的账户丢失负任何责任。</h2>' +
					 '<div><span>账户(区块链领域学名:地址)</span>&nbsp;&nbsp;'+account.address+'</div>' +
					 '<div><span>密码(区块链领域学名:私钥)</span>&nbsp;&nbsp;'+account.privateKey+'</div>' ;

	function blank(){
		// console.log("a");
	}
	var nextStaff = function(){
			blank();
		}
	popBox.createBox(getContent,1,nextStaff);
}


