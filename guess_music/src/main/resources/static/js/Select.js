const url='/gameList'
const parentNode=document.getElementById("gameList");
fetch(url).then(res=>res.json()).then(data=>{
    data.forEach(function(game){
        let newnode=document.createElement("a");
        newnode.href="/testGame";
        newnode.textContent=game.title;
        parentNode.appendChild(newnode);
    })
})