const url='/gameList'
const parentNode=document.getElementById("gameList");
fetch(url).then(res=>res.json()).then(data=>{
    data.forEach(function(game){
        let newnode=document.createElement("a");
        newnode.href="/gameStart?gameIndex="+game.gameIndex;
        newnode.textContent=game.title;
        parentNode.appendChild(newnode);
        let br=document.createElement("br");
        parentNode.appendChild(br);
    })
})