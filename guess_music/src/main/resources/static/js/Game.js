
function checkAnswer(){
    var textBox=document.getElementById('answer');
    if(textBox!=null){
        var target=textBox.value;
        var url='/testGame?target='+target;
        console.log(url);
        fetch(url,{method:'post'}).then(res=>res.json()).then(data=>{
            console.log("it is work");
            console.log(data.result);
            if(data.result=="Game End"){
                console.log("game end gogo!")
                window.location.href='http://localhost:8080';
            }else if(data.result=="Next Song"){
                console.log("next song gogo!")
                window.location.href='http://localhost:8080/testGame';
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