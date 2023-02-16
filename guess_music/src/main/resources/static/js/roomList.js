var vm = new Vue({
        el: '#app',
        data: {
            room_name : '',
            chatrooms: [
            ],
            gameList:[],
            listStatus:false
        },
        created() {
            this.findAllRoom();
            this.findAllGame();
        },
        methods: {
            findAllRoom: function() {
                console.log("will load room list");
                axios.get('/Game/rooms').then(response => { this.chatrooms = response.data;
                });
            },
            findAllGame: function(){
                axios.get('/Game/gameList').then(response => {
                console.log("got all game List");
                this.gameList = response.data; });
            }
            ,
            createRoom: function(gameIndex) {
                if("" === this.room_name) {
                    alert("방 제목을 입력해 주십시요.");
                    return;
                } else {
                    console.log("room created"+gameIndex);
                    var params = new URLSearchParams();
                    params.append("name",this.room_name);
                    params.append("gameIndex",gameIndex);
                    axios.post('/Game/createRoom',params).then(res=>{
                        console.log("res : "+res.data);
                        this.room_name = '';
                        this.findAllRoom();
                        //move to waiting room
                        localStorage.setItem('wschat.roomId',res.data);
                        location.href="/Game/gameRoom/"+res.data;
                    }).catch(err=>{alert("error");});
                }
            },
            enterRoom: function(roomId,Status) {
                if(Status=="START"){
                    alert("게임이 이미 진행중 입니다.");
                }else{
                    localStorage.setItem('wschat.roomId',roomId);
                    location.href="/Game/gameRoom/"+roomId;
                }

            },
            selectGame: function(){
                this.listStatus=!this.listStatus;
            }
        }
    });