var cnt=0;
function addAnswer(){
        var ans=document.getElementById("ansP");
        var child=document.createElement("input");
        child.type="text";
        child.name="answer";
        child.placeholder="enterAnswer";
        child.id="songAnswer"+cnt;
        ans.appendChild(child);

        var child2=document.createElement("input");
        child2.type="button";
        child2.value="삭제";
        child2.id="undo"+cnt;
        child2.setAttribute("onclick","undo("+cnt+")");
        ans.appendChild(child2);

        cnt++;
    }
function finish(){
    var idx=document.getElementById("gameIndex").textContent;
    window.location.href='/manage/modifyGame?gameIndex='+idx;
}

function undo(count){
    let ans=document.getElementById("songAnswer"+count);
    let btn=document.getElementById("undo"+count);
    ans.remove();
    btn.remove();
}