console.log("check manager js loaded");
let url2='/checkLogin'
fetch(url2).then(res=>res.text()).then(data=>{
console.log("data fetched"+data);
if(data!="manager"){
    console.log("Not logined");
    alert("관리자 아이디로 로그인 후 이용해 주세요");
    window.location.href='/login';
}
}).catch(err=>{
console.log("login check api error");
})