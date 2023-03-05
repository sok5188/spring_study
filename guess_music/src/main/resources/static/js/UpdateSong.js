

function modifySong(answerId,trId,seq,type){
    var tr=document.getElementById(trId);
    console.log(tr);
    //tr.hidden="hidden";
    let ans=document.createElement("input");
    ans.type="text";
    ans.placeholder="enter new answer";
    ans.id="newAnswer";
    ans.style="width:200px; font-size: 65%";
    var tbody=document.getElementById("tableBody");
    let sendAns=document.createElement("input");
    sendAns.type="button";
    sendAns.value="저장하기";
    console.log("type is  : "+type);
    if(type=="add"){
        sendAns.setAttribute("onclick","addAns("+seq+")");
    }
    else{
        sendAns.setAttribute("onclick","sendNewAnswer("+answerId+")");
    }

    let backBtn=document.createElement("input");
    backBtn.type="button";
    backBtn.value="취소";
    backBtn.setAttribute("onclick","cacelEdit()");

    var beforeComponent=document.getElementById(trId+1);
    tbody.insertBefore(ans,beforeComponent);
    tbody.insertBefore(sendAns,beforeComponent);
    tbody.insertBefore(backBtn,beforeComponent);
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
            window.location.href='/manage/modifyGame?gameIndex='+gameIndex;
        }
    }).catch(err=>{console.log("send new answer error")});
}

function deleteSong(seq){
    let urls='/manage/modifyGame?gameIndex='+gameIndex+'&seq='+seq;
    fetch(urls,{method:"DELETE"}).then(res=>res.text()).then(data=>{
        if(data=="Success")
            window.location.href='/manage/modifyGame?gameIndex='+gameIndex;
        else
            alert('fail to delete');
    }).catch(err=>{console.log("delete song error")});
}
function addSong(){
//노래 정답들, 가수, 초성 힌트 같은 정보들 입력받고(sql injection등 sanitize하는 방안 필요할 듯..)
// 업로드 된 mp3를 저장해야 함..(보안 이슈 발생 가능)
    console.log("add song btn clicked");
    let urls='/manage/upload?gameIndex='+gameIndex;
    window.location.href=urls;
}
function goback(){
    window.location.href='/manage';
}
function cacelEdit(){
    window.location.href='/manage/modifyGame?gameIndex='+gameIndex;
}
function addAns(seq){
//idx게임의 seq노래의 정답을 추가한다.
    console.log("add Answer called");
    let ans=document.getElementById("newAnswer");
    let newAns=ans.value;
    let urls='/manage/addAnswer?seq='+seq+"&answer="+ newAns+"&gameIndex="+gameIndex;
    fetch(urls,{method:"POST"}).then(res=>res.text()).then(data=>{
        if(data=="Fail")
            alert('invalid change');
        else{
            window.location.href='/manage/modifyGame?gameIndex='+gameIndex;
        }
    }).catch(err=>{console.log("send new answer error")});
}
function deleteAns(id){
//해당 id를 가진 answer를 테이블에서 삭제 시킨다.
    let urls='/manage/updateAnswer?ansId='+id;
        fetch(urls,{method:"DELETE"}).then(res=>res.text()).then(data=>{
            if(data=="Success")
                window.location.href='/manage/modifyGame?gameIndex='+gameIndex;
            else
                alert('fail to delete');
        }).catch(err=>{console.log("delete song error")});
}