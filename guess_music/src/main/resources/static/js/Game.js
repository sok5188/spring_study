
function checkAnswer(){
    var textBox=document.getElementById('answer');
    if(textBox!=null){
        var target=textBox.value;
        var url='/testGame?target='+target;
        console.log(url);
        fetch(url,{method:'post'}).then(res=>res.json()).then(data=>{
            console.log(data.result);
            //정답시.. 정답화면 표
            if(data.result=="Game End"){
                console.log("game end gogo!")
                this.showAnswer("end",data.answer);
                //window.location.href='http://localhost:8080';
            }else if(data.result=="Next Song"){
                console.log("next song gogo!")
                this.showAnswer("next",data.answer);
                console.log("after called showAnswer");
                //window.location.href='http://localhost:8080/testGame';
            }
        })
        .catch(err=>{
            console.log("error !");
        });
        textBox.value="";
    }
}
function enterKey(e){
    if(e.keyCode==13){
        document.getElementById('sub_btn').click();
    }
}

function showAnswer(status,answer){

    let submitDiv=document.getElementById('enterAnswer');
    let gotAnswerDiv=document.getElementById('gotAnswer');
    let answerText=document.getElementById('answerText');

    submitDiv.hidden=true;
    gotAnswer.hidden=false;

    answerText.textContent=answer;

    if(status=="end"){
        document.getElementById('endText').hidden=false;
    }

}
function skipSong(){
    let hidden=document.getElementById('gotAnswer').getAttribute("hidden");
    let endFlag=document.getElementById('endText').getAttribute("hidden");
    if(!endFlag){
        //game end!
        //일단 홈으로 이동시킴
        window.location.href='http://localhost:8080';
    }
    else{
        //다음곡으로 이동
        if(hidden){
            //정답을 맞추지 못한 상태로 skip : seq값 증가시켜 줘야 함
        }
        window.location.href='http://localhost:8080/testGame';
    }
}
