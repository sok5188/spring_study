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
        tmp:''
    },
    created() {
        this.roomId = localStorage.getItem('wschat.roomId');
        this.findRoom();
        this.findUser();
    },
    methods: {
        findRoom: function() {
            axios.get('/room/'+this.roomId).then(response => { this.room = response.data;
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
            console.log("in user"+this.sender+" / " + this.room.ownerName);
            if(this.sender==this.room.ownerName){
              console.log("you are owner!");
              this.startDiv=!this.startDiv;
            }
          });
        },
        sendMessage: function() {
            ws.send("/app/Game/message", {}, JSON.stringify({type:'TALK', roomId:this.roomId, sender:this.sender, message:this.message}));
            //checkAnswer(message);
            this.message = '';
        },
        recvMessage: function(recv) {
            console.log("!!recv!!"+recv.type);

            if(recv.type=='START'){
                console.log("in recv it is START");
                this.startGame();
            }
            else{
                this.messages.unshift({"type":recv.type,"sender":recv.type=='ENTER'?'[알림]':recv.sender,"message":recv.message})
            }
            console.log(recv);
            console.log(recv.message);
            console.log(recv.message.message);


        },
        sendStart : function(){
           console.log("send start");
           ws.send("/app/Game/message", {}, JSON.stringify({type:'START',roomId:this.roomId,sender:this.sender,message:this.message}));
        },
        startGame : function(){
          console.log("start game !");
          this.tmp="wowowowowo";
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

            this.stopAudio();
            this.room.seq=this.room.seq+1;
            this.remainSong=this.remainSong-1;

            this.answerText='';
            this.gotAnswerDiv=false;
            if(this.remainSong==0){
                //game end
                this.endGame();
            }else{
                //send skip message to server for change seq and hints
                axios.post('/Game/skip/'+this.roomId).then(response=>{});
                this.audioSource="/"+this.room.gameIndex+"-"+this.room.seq+".mp3";
                this.playAudio();
            }
        },
        skipVote: function() {
            //일단 바로 skip
           this.showAnswer();
           setTimeout(() => {
                 this.skipSong();
                 this.countDown = 60;
                 //this.timer();
           }, 3000)
        },
        checkAnswer : function(msg){
          //if game is now on going return nothing

          //else check answer

          // if correct stop checking answer and show the full ans

          // else just keep going
        },
        endGame : function(){
            //game end
            this.endGameDiv=!this.endGameDiv;
        },
        goHome : function(){
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

//var audio=document.getElementById('musicPlayer');
//    audio.volume=0.5;
//    audio.src="/1-1.mp3";

