
var idx=document.getElementById("gameIndex").textContent;
const url='/manage/songList?gameIndex='+idx;
console.log("url is "+url);
const table=document.getElementById("tableBody");
fetch(url).then(res=>res.json()).then(data=>{
    data.forEach(function(answers){
        console.log(answers);
        var row=`<tr>
        <td>${answers.answer}</td>
        <td>${answers.singer}</td>
        <td>${answers.initial}</td>
        <td><input type="button" value="정답 수정" id="modifySongBtn" onclick="modifySong(${answers.id})"></td>
        <td><input type="button" value="노래 삭제" id="deleteSongBtn" onclick="deleteSong(${answers.id})"></td>
        </tr>`
        table.innerHTML+=row;
    })
}).catch(err=>{
    console.log("cannot get song list");
})

function modifySong(idx){
    console.log(idx);
}
function deleteSong(idx){
    console.log(idx);
}
function addSong(){
//노래 정답들, 가수, 초성 힌트 같은 정보들 입력받고
// 해당 노래를 mp3로 업로드하게 해주고
// 업로드 된 mp3를 저장해야 함..(보안 이슈 발생 가능)
    console.log("add song btn clicked");
    window.location.href='http://localhost:8080/manage/upload';
}