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

function transactionHtml(item){
    let {transactionInputs,transactionOutputs} = item;

    let left = '';
    if(!isNull(transactionInputs)){
        transactionInputs.forEach(item1 => {
            left += `<div>الدافع：<span><a title="تفاصيل العنوان" target="_blank" href="./address.html?address=${item1.address}">${item1.address}</a></span>&nbsp;<a title="تفاصيل إخراج المعاملة" target="_blank" href="./transactionoutput.html?transactionHash=${item1.transactionHash}&transactionOutputIndex=${item1.transactionOutputIndex}"><i class="glyphicon-euro"></i></a>&nbsp;<span>${item1.value}</span></div>`
        })
    }

    let right = ''
    if(!isNull(transactionOutputs)){
        transactionOutputs.forEach(item1 => {
            right += `<div style="display:flex">المدفوع لأمره：<span><a title="تفاصيل العنوان" target="_blank" href="./address.html?address=${item1.address}">${item1.address}</a></span>&nbsp;<a title="تفاصيل إخراج المعاملة" target="_blank" href="./transactionoutput.html?transactionHash=${item1.transactionHash}&transactionOutputIndex=${item1.transactionOutputIndex}"><i class="glyphicon-euro"></i></a>&nbsp;<span>${item1.value}</span></div>`
        });
    }

    let itemTransactionType = item.transactionType == 'GENESIS_TRANSACTION'?"معاملة التكوين":"معاملة قياسية";

    let transactionHtml = `
        <div style="font-size: 14px; line-height: 40px; margin-top: 10px;">
            <!-- 开头 -->
            <div style="display: flex; background-color: #f5f5f5; flex-wrap: wrap; border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">
                <div style="min-width: 40%;"><a title="تفاصيل الصفقة" target="_blank" href="./transaction.html?transactionHash=${item.transactionHash}">${item.transactionHash}</a></div>
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
            left += `<div>الدافع：<span><a title="تفاصيل العنوان" target="_blank" href="./address.html?address=${item1.address}">${item1.address}</a></span>&nbsp;<a title="تفاصيل إخراج المعاملة" target="_blank" href="./transactionoutput.html?transactionHash=${item1.transactionHash}&transactionOutputIndex=${item1.transactionOutputIndex}"><i class="glyphicon-euro"></i></a>&nbsp;<span>${item1.value}</span></div>`
        })
    }

    let right = ''
    if(!isNull(transactionOutputs)){
        transactionOutputs.forEach(item1 => {
            right += `<div style="display:flex">المدفوع لأمره：<span><a title="تفاصيل العنوان" target="_blank" href="./address.html?address=${item1.address}">${item1.address}</a></span>&nbsp;<a title="تفاصيل إخراج المعاملة" target="_blank" href="./transactionoutput.html?transactionHash=${item1.transactionHash}&transactionOutputIndex=${item1.transactionOutputIndex}"><i class="glyphicon-euro"></i></a>&nbsp;<span>${item1.value}</span></div>`
        });
    }

    let transactionHtml = `
        <div style="font-size: 14px; line-height: 40px; margin-top: 10px;">
            <!-- 开头 -->
            <div style="display: flex; background-color: #f5f5f5; flex-wrap: wrap; border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">
                <div style="min-width: 40%;"><a title="تفاصيل الصفقة" target="_blank" href="./transaction.html?transactionHash=${item.transactionHash}">${item.transactionHash}</a></div>
            </div>
            <div style="display: flex; flex-wrap:wrap;">
                <div style="width:auto; min-width:50%;">${left}</div>
                <div style="width:auto; min-width:40%;">${right}</div>
            </div>
        </div>
    `;
    return transactionHtml;
}
