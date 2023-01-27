const url='/gameList'
const parentNode=document.getElementById("gameList");
const listDiv=document.getElementById("listDiv");
fetch(url).then(res=>res.json()).then(data=>{
    data.forEach(function(game){
        console.log("got game :"+game.gameIndex);
        let radioBtn=`<input type="radio" name="gameBtn" id="${game.gameIndex}" value="${game.gameIndex}">
                          <label for="${game.gameIndex}"> ${game.title}</label>`
        listDiv.innerHTML+=radioBtn
    })
})

function create(){

    var obj_length = document.getElementsByName("gameBtn").length;
    let gameIndex;
    for (var i=0; i<obj_length; i++) {
        if (document.getElementsByName("gameBtn")[i].checked == true) {
            gameIndex=document.getElementsByName("gameBtn")[i].value;
            console.log("it is clicked"+i);
        }
    }
    //방 생성 후 대기방으로 이동
    let urls="/createRoom?gameIndex="+gameIndex;
    fetch(urls,{method:"POST"}).then(res=>res.text()).then(data=>{
        if(data=="Success"){
            //대기방으로 이동
            window.location.href='/Game/waitingRoom';
        }
        else{
            alert("방 생성에 실패하였습니다");
        }
    })
}