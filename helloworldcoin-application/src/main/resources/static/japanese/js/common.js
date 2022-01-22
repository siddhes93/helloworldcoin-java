function baseUrl(){
    return "";
}

function parseUrlParameters(location){
    return new Map(location.search.slice(1).split('&').map(kv => kv.split('=')));
}

function isNull(element){
    return element == null || element == undefined
}

function isEmpty(element){
    return element == null || element == undefined || element == ""
}

async function $ajax(option){
    const obj = {
        type: "post",
        contentType: "application/json",
        dataType: "json",
        data: `{}`,
        async: false,
    }
    Object.assign(obj , option)
    return new Promise ((resolve, rejact) => {
        $.ajax({
            ...obj,
            success(data){
                resolve(data)
            },
            error(err){
                rejact(err)
            }
        })
    })
}

function commonTranslate(element){
    if(element == 'request_param_illegal'){
        return '要求（ようきゅう）パラメータが不正（ふせい）です';
    }
    if(element == 'service_unavailable'){
        return 'サービス利用（りよう）不可（ふか）';
    }
    return null;
}

function transactionHtml(item){
    let {transactionInputs,transactionOutputs} = item;

    let left = '';
    if(!isNull(transactionInputs)){
        transactionInputs.forEach(item1 => {
            left += `<div>支払（しはらい）人（じん）：<span><a title="住所（じゅうしょ）の詳細（しょうさい）" target="_blank" href="./address.html?address=${item1.address}">${item1.address}</a></span>&nbsp;<a title="トランザクション出力（しゅつりょく）の詳細（しょうさい）" target="_blank" href="./transactionoutput.html?transactionHash=${item1.transactionHash}&transactionOutputIndex=${item1.transactionOutputIndex}"><i class="glyphicon-euro"></i></a>&nbsp;<span>${item1.value}</span></div>`
        })
    }

    let right = ''
    if(!isNull(transactionOutputs)){
        transactionOutputs.forEach(item1 => {
            right += `<div style="display:flex">受取（うけとり）人（じん）：<span><a title="住所（じゅうしょ）の詳細（しょうさい）" target="_blank" href="./address.html?address=${item1.address}">${item1.address}</a></span>&nbsp;<a title="トランザクション出力（しゅつりょく）の詳細（しょうさい）" target="_blank" href="./transactionoutput.html?transactionHash=${item1.transactionHash}&transactionOutputIndex=${item1.transactionOutputIndex}"><i class="glyphicon-euro"></i></a>&nbsp;<span>${item1.value}</span></div>`
        });
    }

    let itemTransactionType = item.transactionType == 'GENESIS_TRANSACTION'?"ジェネシストランザクション":"標準（ひょうじゅん）取引（とりひき）";

    let transactionHtml = `
        <div style="font-size: 14px; line-height: 40px; margin-top: 10px;">
            <!-- 开头 -->
            <div style="display: flex; background-color: #f5f5f5; flex-wrap: wrap; border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">
                <div style="min-width: 40%;"><a title="取引（とりひき）の詳細（しょうさい）" target="_blank" href="./transaction.html?transactionHash=${item.transactionHash}">${item.transactionHash}</a></div>
                <div style="min-width: 20%; text-align: center;">${itemTransactionType}</div>
                <div style="min-width: 20%; text-align: center;">${item.transactionFee}</div>
                <div style="min-width: 20%; text-align: center;">${item.blockTime}</div>
            </div>
            <div style="display: flex; flex-wrap:wrap;">
                <div style="width:auto; min-width:50%;">${left}</div>
                <div style="width:auto; min-width:40%;">${right}</div>
            </div>
        </div>
    `;
    return transactionHtml;
}

function unconfirmedTransactionHtml(item){
    let {transactionInputs,transactionOutputs} = item;

    let left = '';
    if(!isNull(transactionInputs)){
        transactionInputs.forEach(item1 => {
            left += `<div>支払（しはらい）人（じん）：<span><a title="住所（じゅうしょ）の詳細（しょうさい）" target="_blank" href="./address.html?address=${item1.address}">${item1.address}</a></span>&nbsp;<a title="トランザクション出力（しゅつりょく）の詳細（しょうさい）" target="_blank" href="./transactionoutput.html?transactionHash=${item1.transactionHash}&transactionOutputIndex=${item1.transactionOutputIndex}"><i class="glyphicon-euro"></i></a>&nbsp;<span>${item1.value}</span></div>`
        })
    }

    let right = ''
    if(!isNull(transactionOutputs)){
        transactionOutputs.forEach(item1 => {
            right += `<div style="display:flex">受取（うけとり）人（じん）：<span><a title="住所（じゅうしょ）の詳細（しょうさい）" target="_blank" href="./address.html?address=${item1.address}">${item1.address}</a></span>&nbsp;<a title="トランザクション出力（しゅつりょく）の詳細（しょうさい）" target="_blank" href="./transactionoutput.html?transactionHash=${item1.transactionHash}&transactionOutputIndex=${item1.transactionOutputIndex}"><i class="glyphicon-euro"></i></a>&nbsp;<span>${item1.value}</span></div>`
        });
    }

    let transactionHtml = `
        <div style="font-size: 14px; line-height: 40px; margin-top: 10px;">
            <!-- 开头 -->
            <div style="display: flex; background-color: #f5f5f5; flex-wrap: wrap; border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">
                <div style="min-width: 40%;"><a title="取引（とりひき）の詳細（しょうさい）" target="_blank" href="./transaction.html?transactionHash=${item.transactionHash}">${item.transactionHash}</a></div>
            </div>
            <div style="display: flex; flex-wrap:wrap;">
                <div style="width:auto; min-width:50%;">${left}</div>
                <div style="width:auto; min-width:40%;">${right}</div>
            </div>
        </div>
    `;
    return transactionHtml;
}
