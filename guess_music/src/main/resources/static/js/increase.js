function increase(type){
    console.log("type:"+type);
    fetch("/manage/increase?type="+type, {method: 'POST'}).then(res=>res.text());
}