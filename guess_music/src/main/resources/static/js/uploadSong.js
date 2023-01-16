function addAnswer(){
        var ans=document.getElementById("ansP");
        var child=document.createElement("input");
        child.type="text";
        child.name="answer";
        child.placeholder="enterAnswer";
        ans.appendChild(child);
    }
function finish(){
    var idx=document.getElementById("gameIndex").textContent;
    window.location.href='/manage/modifyGame?gameIndex='+idx;
}