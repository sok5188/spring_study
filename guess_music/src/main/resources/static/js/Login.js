let url='/auth/checkLogin'
let notLoginDiv=document.getElementById('notLoginHome');
let loginDiv=document.getElementById('loginHome');
fetch(url).then(res=>res.text()).then(data=>{
    if(data!="False"){
        notLoginDiv.hidden=true;
        loginDiv.hidden=false;
    }else{
        notLoginDiv.hidden=false;
        loginDiv.hidden=true;
    }
}).catch(err=>{
    console.log("login check api error");
})

function logoutHandler(){
    location.href='/logout';
}

function goLogIn(){
    location.href='/auth/loginForm';
}

function goSignIn(){
    location.href='/auth/joinForm';
}
function playGame(){
    location.href='/Game/roomList';
}