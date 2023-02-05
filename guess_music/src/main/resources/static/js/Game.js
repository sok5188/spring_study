
//for timer
var time=60;
var sec="";
var x;
const timerHandler=()=>{
    sec=time%60
    timer=document.getElementById("timer").value=sec+"초";
    time--;
    if(time==30){
        showSingerHint();
    }
    if(time==15){
        showInitialHint();
    }
    if(time<0){
        clearInterval(x);
        skipSong();
    }
};
x=setInterval(timerHandler,1000);

var audio=document.getElementById('musicPlayer');
audio.volume=0.5;

function checkAnswer(){
    var textBox=document.getElementById('answer');
    if(textBox!=null){
        var target=textBox.value;
        var url='/Game/checkAnswer?target='+target;
        fetch(url).then(res=>res.json()).then(data=>{
            console.log(data.result);
            //정답시.. 정답화면 표
            if(data.result=="Game End"){
                console.log("game end gogo!")
                this.showAnswer("end",data.answer,data.singer);
                //window.location.href='http://localhost:8080';
            }else if(data.result=="Next Song"){
                console.log("next song gogo!")
                this.showAnswer("next",data.answer,data.singer);
                console.log("after called showAnswer");
                //window.location.href='http://localhost:8080/Game';
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

function showAnswer(status,answer,singer){

    let submitDiv=document.getElementById('enterAnswer');
    let gotAnswerDiv=document.getElementById('gotAnswer');
    let answerText=document.getElementById('answerText');

    submitDiv.hidden=true;
    gotAnswer.hidden=false;

    answerText.textContent=singer+"  -  "+answer;

    if(status=="end"){
        document.getElementById('endText').hidden=false;
    }

}
function skipSong(){
    let hidden=document.getElementById('gotAnswer').getAttribute("hidden");
    let endFlag=document.getElementById('endText').getAttribute("hidden");
    console.log("skip handler called");
    audio.pause();
    clearInterval(x);
    if(!endFlag){
        //game end!
        //일단 홈으로 이동시킴
        window.location.href='/';
    }
    else{
        //다음곡으로 이동
        if(hidden){
            //정답을 맞추지 못한 상태로 skip : 정답 창 보여주기
            let url='Game/checkAnswer?target=skip';
            fetch(url).then(res=>res.json()).then(data=>{
                console.log("got response"+data.result);
                if(data.result=="Game End"){
                    this.showAnswer("end",data.answer,data.singer);
                }else if(data.result=="Next Song"){
                    this.showAnswer("next",data.answer,data.singer);
                }
            })
            .catch(err=>{
                console.log("error !");
            });
        }
        else
            window.location.href='/Game';
    }
}
var hintDiv=document.getElementById('hintDiv');
function showSingerHint(){
    //document.getElementById('hintDiv').textContent
    let url='Game/hint?type=singer';
    fetch(url).then(res=>res.text()).then(data=>{
        document.getElementById('singerHint').textContent="가수 : "+data;
    }).catch(err=>{
        console.log("showSingerHint error");
    })
}

function showInitialHint(){
    let url='Game/hint?type=initial';
        fetch(url).then(res=>res.text()).then(data=>{
            document.getElementById('initialHint').textContent="초성 힌트 : "+data;
        }).catch(err=>{
            console.log("showInitialrHint error");
        })
}
function goHome(){
    window.location.href='/';
}