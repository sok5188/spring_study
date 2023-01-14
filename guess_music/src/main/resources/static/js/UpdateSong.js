
var idx=document.getElementById("gameIndex").textContent;
const url='/manage/songList?gameIndex='+idx;
const table=document.getElementById("tableBody");
var ansCount=0;
fetch(url).then(res=>res.json()).then(data=>{
    data.forEach(function(answers){
        ansCount++;
        var row=`<tr id="${ansCount}">
        <td>${answers.seq}</td>
        <td>${answers.answer}</td>
        <td>${answers.singer}</td>
        <td>${answers.initial}</td>
        <td><input type="button" value="정답 수정" id="modifySongBtn" onclick="modifySong(${answers.id},${ansCount})"></td>
        <td><input type="button" value="노래 삭제" id="deleteSongBtn" onclick="deleteSong(${answers.seq})"></td>
        </tr>`
        table.innerHTML+=row;
    })
}).catch(err=>{
    console.log("cannot get song list");
})

function modifySong(answerId,trId){
    var tr=document.getElementById(trId);
    console.log(tr);
    tr.hidden="hidden";
    let ans=document.createElement("input");
    ans.type="text";
    ans.placeholder="enter new answer";
    ans.id="newAnswer";
    var tbody=document.getElementById("tableBody");
    let sendAns=document.createElement("input");
    sendAns.type="button";
    sendAns.value="수정하기";
    sendAns.setAttribute("onclick","sendNewAnswer("+answerId+")");
    tbody.insertBefore(ans,document.getElementById(trId+1));
    tbody.insertBefore(sendAns,document.getElementById(trId+1))
}
function sendNewAnswer(id){
    console.log("send new answer called : "+id);

    let ans=document.getElementById("newAnswer");
    let newAns=ans.value;
    let urls='/manage/updateAnswer?id='+id+"&answer="+ newAns;
    fetch(urls,{method:"POST"}).then(res=>res.text()).then(data=>{
        if(data=="Fail")
            alert('invalid change');
        else{
            window.location.href='http://localhost:8080/manage/modifyGame?gameIndex='+idx;
        }
    }).catch(err=>{console.log("send new answer error")});
}

function deleteSong(seq){
    let urls='/manage/modifyGame?gameIndex='+idx+'&seq='+seq;
    fetch(urls,{method:"DELETE"}).then(res=>res.text()).then(data=>{
        if(data=="Success")
            window.location.href='http://localhost:8080/manage/modifyGame?gameIndex='+idx;
        else
            alert('fail to delete');
    }).catch(err=>{console.log("delete song error")});
}
function addSong(){
//노래 정답들, 가수, 초성 힌트 같은 정보들 입력받고(sql injection등 sanitize하는 방안 필요할 듯..)
// 업로드 된 mp3를 저장해야 함..(보안 이슈 발생 가능)
    console.log("add song btn clicked");
    let urls='http://localhost:8080/manage/upload?gameIndex='+idx;
    window.location.href=urls;
}
