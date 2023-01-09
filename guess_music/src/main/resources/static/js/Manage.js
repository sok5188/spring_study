const url='/gameList'
const table=document.getElementById("tableBody");
fetch(url).then(res=>res.json()).then(data=>{
    data.forEach(function(game){
        console.log(game);
        var row=`<tr>
        <td>${game.gameIndex}</td>
        <td>${game.title}</td>
        <td>${game.songNum}</td>
        <td><input type="button" value="게임 수정" id="modifyGameBtn" onclick="modifyGame(${game.gameIndex})"></td>
        <td><input type="button" value="게임 삭제" id="deleteGameBtn" onclick="deleteGame(${game.gameIndex})"></td>
        </tr>`
        table.innerHTML+=row;
    })
})

function modifyGame(idx){
    let urls='http://localhost:8080/manage/modifyGame?gameIndex='+idx;
    window.location.href=urls;
}

function deleteGame(idx){
    let urls='/manage?gameIndex='+idx;
    fetch(urls,{method:"DELETE"}).then(res=>res.text()).then(data=>{
        if(data=="Success")
            window.location.href='http://localhost:8080/manage';
        else
            alert('fail to delete');
    }).catch(err=>{console.log("delete game error")});
}
function makeGame(){
    window.location.href='http://localhost:8080/manage/createGame';
}