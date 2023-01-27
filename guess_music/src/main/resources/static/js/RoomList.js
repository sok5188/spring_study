function init(){
    //get Room List
    fetch('/roomList',{method:"POST"}).then(res=>res.json()).then(data=>{
        console.log("got room List"+data);

    })
    //add List with buttons to table

    //show List to view
}
init();



function createRoom(){
    // 방 생성으로 이동시 생성자 정보 넘긴다? or 방생성시 옮긴다.
    window.location.href='/createRoom';
}