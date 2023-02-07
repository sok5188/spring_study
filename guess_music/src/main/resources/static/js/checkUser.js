let url2='/auth/checkLogin'
fetch(url2).then(res=>res.text()).then(data=>{
    console.log("data fetched"+data);
    if(data=="False"){
        console.log("Not logined");
        alert("로그인 후 이용해 주세요");
        window.location.href='/login';
    }
}).catch(err=>{
    console.log("login check api error");
})