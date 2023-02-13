//alert(document.title);
// websocket & stomp initialize
var sock = new SockJS("/ws");
var ws = Stomp.over(sock);
var reconnect = 0;
// vue.js
var vm = new Vue({
    el: '#app',
    data: {
        roomId: '',
        room: {},
        sender: '',
        message: '',
        messages: [],
        startDiv: false,
        introDiv: true,
        gameDiv: false,
        remainSong:'',
        totalSong:'',
        audioSource:'',
        countDown:60,
        gotAnswerDiv:false,
        endGameDiv:false,
        singerHintText:'',
        initialHintText:'',
        audio:'',
        initialDiv:false,
        singerDiv:false,
        answerText:'',
        userNum:0,
        users:[],
        block:false
    },
    mounted() {
        window.addEventListener('beforeunload', this.unLoadEvent);
        window.onpageshow = function(event) {
            if ( event.persisted || (window.performance && window.performance.navigation.type == 2)) {
                // Back Forward Cache로 브라우저가 로딩될 경우 혹은 브라우저 뒤로가기 했을 경우

                alert("해당 기능은 현재 페이지에서 제한되어 있습니다 게임 목록에서 다시 입장해 주세요");
                location.href="/Game/roomList";
            }
        }
    },
    beforeUnmount() {
        window.removeEventListener('beforeunload', this.unLoadEvent);
    },
    created() {
        this.roomId = localStorage.getItem('wschat.roomId');
        this.findRoom();
        this.findUser();
    },
    methods: {
        unLoadEvent: function (event) {
          event.preventDefault();
          //event.returnValue = '';
          //ws.send("/app/Game/message", {}, JSON.stringify({type:'LEAVE',roomId:this.roomId,sender:this.sender,message:this.message}));
          this.sendMessage('LEAVE');
        },
        findRoom: function() {
            axios.get('/Game/room/'+this.roomId).then(response => { this.room = response.data;
              console.log("in room"+this.sender+" / " + this.room.ownerName);
              if(this.sender==this.room.ownerName){
                console.log("you are owner!");
                this.startDiv=!this.startDiv;
              }
              this.totalSong=this.room.songNum;
              this.remainSong=this.room.songNum;
            });
        },
        findUser: function(){
          axios.get('/Game/getUser').then(response=>{
            this.sender=response.data;
            if(this.sender==this.room.ownerName){
              console.log("you are owner!");
              this.startDiv=!this.startDiv;
            }
          });
        },
        findUsers:function(){
            axios.get("/Game/getUsers/"+this.roomId).then(response=>{
                            this.users=response.data;
                            this.users.sort((a,b)=>{
                                return b.score-a.score;
                            })
                       })
        },
        sendMessage: function(MsgType) {
            if(this.block){
                this.message = '';
                return;
            }

            ws.send("/app/Game/message", {}, JSON.stringify({type:MsgType, roomId:this.roomId, sender:this.sender, message:this.message}));
            //checkAnswer(message);
            this.message = '';
        },
        recvMessage: function(recv) {
            axios.get('/Game/getUserCount/'+this.roomId).then(response=>{
                this.userNum=response.data;
            })

            if(recv.type=='START'){
                console.log("in recv it is START");
                this.startGame();
            }else{
                if(recv.type=='ANSWER'||recv.type=='SKIP'){
                //원래는 skip하는 동작과 같이 만들어야 하지만 일단 skipvote로 처리
                // 추후에 skip에서 3초 딜레이를 걸고 타이머랑 vote에서 딜레이 없애는 식으로 변경 필요
                    this.skipSong();
                    if(recv.type=='ANSWER'){
                        this.findUsers();
                    }
                }
                if(recv.type=='LEAVE'){
                    this.findUsers();
                }
                this.messages.unshift({"type":recv.type,"sender":recv.type!='TALK'?'[알림]':recv.sender,"message":recv.message})
            }



        },
        sendStart : function(){
           console.log("send start");
           this.sendMessage('START');
        },
        startGame : function(){
          console.log("start game !");
          this.findUsers();
          this.introDiv=!this.introDiv;
          this.timer()
          this.gameDiv=!this.gameDiv;
          this.audioSource="/"+this.room.gameIndex+"-"+this.room.seq+".mp3";
          this.playAudio();
        },
        timer(){
          if(this.countDown==30){
            this.showSingerHint();
          }
          if(this.countDown==15){
            this.showInitialHint();
          }
          if (this.countDown > 0) {
              setTimeout(() => {
                  this.countDown -= 1
                  this.timer()
              }, 1000)

          }else{
            //show answer message
            this.showAnswer();
            setTimeout(() => {
                  this.skipSong();
                  this.countDown = 60;
                  this.timer();
            }, 3000)
          }

        },
        playAudio : function(){
            if(this.audioSource){
                this.audio=new Audio(this.audioSource);
                this.audio.play();
            }
        },
        stopAudio : function(){
            if(this.audioSource){
                this.audio.pause();
            }
        },
        showSingerHint: function() {
            axios.get('/Game/hint',{params:{roomId:this.roomId,type:"singer"}}).then(response=>{
                console.log("response data is : "+response.data);
                this.singerHintText=response.data;
                this.singerDiv=!this.singerDiv;
            })
        },
        showInitialHint: function() {
            axios.get("/Game/hint",{params:{roomId:this.roomId,type:"initial"}}).then(response=>{
                console.log("response data is : "+response.data);
                this.initialHintText=response.data;
                this.initialDiv=!this.initialDiv;
            })
        },
        showAnswer: function() {
            this.singerHintText='';
            this.initialHintText='';
            this.singerDiv=false;
            this.initialDiv=false;
            this.gotAnswerDiv=true;
            axios.get("/Game/getAnswer/"+this.roomId).then(response=>{
                this.answerText=response.data;
            })
        },
        skipSong: function() {
            console.log("in skip song");
            this.showAnswer();
            this.block=true;
            setTimeout(() => {
                 this.countDown = 60;
                 this.stopAudio();
                 this.room.seq=this.room.seq+1;
                 this.remainSong=this.remainSong-1;
                 this.answerText='';
                 this.gotAnswerDiv=false;
                 this.block=false;
                 if(this.remainSong==0){
                     //game end
                     this.endGame();
                 }else{
                     //send skip message to server for change seq and hints
                     //뭐 다른 방식으로 구현하는게 더 좋을 듯 이러면 방장 나가고 나면 안되니깐..
                     if(this.room.ownerName==this.sender){
                         axios.post('/Game/skip/'+this.roomId).then(response=>{});
                     }
                     this.audioSource="/"+this.room.gameIndex+"-"+this.room.seq+".mp3";
                     this.playAudio();
                 }
           }, 3000)


        },
        skipVote: function() {
            console.log("in skip vote");
            //뭐 스킵 투표 받고 일정 인원 넘으면 skip song을 호출하는게 아니라 message를 보내서 구독자들이 모두 skipSong을 호출하게 끔 수정
            //스킵 투표 받고 뭐 어쩌고 저쩌고 해서 스킵하게 된 경우를 가정
            //ws.send("/app/Game/message", {}, JSON.stringify({type:'SKIP', roomId:this.roomId, sender:this.sender, message:"skipvote"}));
            this.sendMessage('SKIP');
        },
        endGame : function(){
            //game end
            this.endGameDiv=!this.endGameDiv;
            this.gameDiv=!this.gameDiv;
            setTimeout(() => {
                axios.post('/Game/deleteRoom/'+this.roomId).then(response=>{

                })
                window.removeEventListener('beforeunload', this.unLoadEvent);
                location.href="/Game/roomList";
            }, 5000)
        },
        goHome : function(){
            window.removeEventListener('beforeunload', this.unLoadEvent);
            this.sendMessage('LEAVE');
            location.href="/";
        }

    }
});

function connect() {
    // pub/sub event
    ws.connect({}, function(frame) {
        ws.subscribe("/topic/room/"+vm.$data.roomId, function(message) {
            var recv = JSON.parse(message.body);
            vm.recvMessage(recv);
        });
        ws.send("/app/Game/message", {}, JSON.stringify({type:'ENTER', roomId:vm.$data.roomId, sender:vm.$data.sender}));
    }, function(error) {
        if(reconnect++ <= 5) {
            setTimeout(function() {
                console.log("connection reconnect");
                sock = new SockJS("/ws");
                ws = Stomp.over(sock);
                connect();
            },10*1000);
        }
    });
}
connect();

function NotReload(){
    console.log("in Not Reload");
    if( (event.ctrlKey == true && (event.keyCode == 78 || event.keyCode == 82)) || (event.keyCode == 116)||(event.metaKey&&event.key==='r') ) {
        console.log("prevent reload");
        event.keyCode = 0;
        event.cancelBubble = true;
        event.returnValue = false;
    }
}
window.document.onkeydown = NotReload;
//var audio=document.getElementById('musicPlayer');
//    audio.volume=0.5;
//    audio.src="/1-1.mp3";

