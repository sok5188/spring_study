const url='/Game/gameList'
const table=document.getElementById("tableBody");
var ansCount=0;
fetch(url).then(res=>res.json()).then(data=>{
    data.forEach(function(game){
        ansCount++;
        console.log(game);
        var row=`<tr id="${ansCount}">
        <td>${game.gameIndex}</td>
        <td>${game.title}</td>
        <td>${game.songNum}</td>
        <td><input type="button" value="제목 변경" id="modifyTitleBtn" onclick="addNewTitle(${game.gameIndex},${ansCount})"></td>
        <td><input type="button" value="게임 수정" id="modifyGameBtn" onclick="modifyGame(${game.gameIndex})"></td>
        <td><input type="button" value="게임 삭제" id="deleteGameBtn" onclick="deleteGame(${game.gameIndex})"></td>
        </tr>`
        table.innerHTML+=row;
    })
})
function addNewTitle(gameIndex,trId){
    var tr=document.getElementById(trId);

    let ans=document.createElement("input");
    ans.type="text";
    ans.placeholder="enter new Title";
    ans.id="newTitle"+gameIndex;
    ans.style="width:200px; font-size: 65%";

    let sendAns=document.createElement("input");
    sendAns.type="button";
    sendAns.value="저장하기";
    sendAns.id="sendTitle"+gameIndex;
    sendAns.setAttribute("onclick","modifyTitle("+gameIndex+")");

    let backBtn=document.createElement("input");
    backBtn.type="button";
    backBtn.value="취소";
    backBtn.id="undo"+gameIndex;
    backBtn.setAttribute("onclick","cacelEdit("+gameIndex+")");

    var beforeComponent=document.getElementById(trId+1);
    table.insertBefore(ans,beforeComponent);
    table.insertBefore(sendAns,beforeComponent);
    table.insertBefore(backBtn,beforeComponent);
}
function modifyGame(idx){
    let urls='/manage/modifyGame?gameIndex='+idx;
    window.location.href=urls;
}

function deleteGame(idx){
    let urls='/manage?gameIndex='+idx;
    fetch(urls,{method:"DELETE"}).then(res=>res.text()).then(data=>{
        if(data=="Success")
            window.location.href='/manage';
        else
            alert('fail to delete');
    }).catch(err=>{console.log("delete game error")});
}
function makeGame(){
    window.location.href='/manage/createGame';
}
function goback(){
    window.location.href='/';
}

function modifyTitle(idx){
    let title=document.getElementById("newTitle"+idx);
    let urls='/manage/modifyGame?gameIndex='+idx+'&newTitle='+title.value;
    fetch(urls,{method:"POST"}).then(res=>res.text()).then(data=>{
        if(data=="Success")
            window.location.href='/manage';
        else
            alert('fail to modify Title');
    }).catch(err=>{console.log("modify title error")})
}

function cacelEdit(gameIndex){
    let title=document.getElementById("newTitle"+gameIndex);
    let send=document.getElementById("sendTitle"+gameIndex);
    let undo=document.getElementById("undo"+gameIndex);

    title.remove();
    send.remove();
    undo.remove();
}