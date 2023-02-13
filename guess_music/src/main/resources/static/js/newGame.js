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
        block:false,
        skipDiv:false,
        nowVote:0,
        voteLimit:0,
        skipBtnMsg:'',
        skipStatus:'',
        skipText:'',
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
          this.sendMessage('LEAVE');
        },
        findRoom: function() {
            axios.get('/Game/room/'+this.roomId).then(response => { this.room = response.data;
              console.log("in room"+this.sender+" / " + this.room.ownerName);
              if(this.sender==this.room.ownerName){
                console.log("you are owner!");
                this.startDiv=true;
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
              this.startDiv=true;
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
                var half= Math.floor(this.userNum/2);
                this.voteLimit=half +1;
                console.log("now voteLimit:"+this.voteLimit+ " usernum :  "+this.userNum+ " / half : "+half);
            })

            if(recv.type=='START'){
                console.log("in recv it is START");
                this.startGame();
            }else{
                if(recv.type=='VOTE'){
                    this.nowVote=this.nowVote+1;
                    //누군가가 skip을 누르면 skipDiv를 보여준다
                    this.skipDiv=true;
                    this.checkSkip();
                }else if(recv.type=='ANSWER'){
                    this.findUsers();
                    this.skipSong();
                }
                else if(recv.type=='LEAVE'){
                    this.findUsers();
                    this.checkSkip();
                }
                this.messages.unshift({"type":recv.type,"sender":recv.type!='TALK'?'[알림]':recv.sender,"message":recv.message})
            }



        },
        checkSkip :function(){
            console.log("now vote in checkSKip:"+this.nowVote+" voteLimit:"+this.voteLimit);
            if(this.nowVote>=this.voteLimit){
                console.log("vote is over the limit nowVote:"+this.nowVote+" voteLimit:"+this.voteLimit);
                this.skipSong();
            }
        },
        sendStart : function(){
           console.log("send start");
           this.sendMessage('START');
        },
        startGame : function(){
          console.log("start game !");
          this.findUsers();
          this.introDiv=false;
          this.timer()
          this.gameDiv=true;
          this.audioSource="/"+this.room.gameIndex+"-"+this.room.seq+".mp3";
          this.skipBtnMsg="스킵 투표";
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

            if(this.skipStatus!="skipping"){
                this.skipSong();
            }else{
                console.log("now skipping by vote : "+this.skipStatus);
            }
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
            if(this.skipStatus=='skipping'){
                return;
            }
            axios.get('/Game/hint',{params:{roomId:this.roomId,type:"singer"}}).then(response=>{
                console.log("response data is : "+response.data);
                this.singerHintText=response.data;
                this.singerDiv=true;
            })
        },
        showInitialHint: function() {
            if(this.skipStatus=='skipping'){
                return;
            }
            axios.get("/Game/hint",{params:{roomId:this.roomId,type:"initial"}}).then(response=>{
                console.log("response data is : "+response.data);
                this.initialHintText=response.data;
                this.initialDiv=true;
            })
        },
        showAnswer: function() {

            this.singerHintText='';
            this.initialHintText='';
            this.singerDiv=false;
            this.initialDiv=false;
            this.gotAnswerDiv=true;
            axios.get("/Game/getAnswer/"+this.roomId).then(response=>{
                console.log("response data is : "+response.data);
                this.answerText=response.data[0];
                this.singerHintText=response.data[1];
            })
        },
        skipSong: function() {
            console.log("in skip song");
            this.showAnswer();
            this.block=true;
            this.skipStatus="skipping";
            this.skipText="투표로 인해 노래가 스킵됩니다 !"
            setTimeout(() => {
                this.skipStatus="";
                this.skipText="";
                var flag=false;
                if(this.countDown>0){
                    flag=true;
                }
                 this.countDown = 60;
                 this.stopAudio();
                 this.room.seq=this.room.seq+1;
                 this.remainSong=this.remainSong-1;
                 this.answerText='';
                 this.gotAnswerDiv=false;
                 this.block=false;
                 this.skipDiv=false;
                 this.skipBtnMsg='스킵 투표';
                 this.nowVote=0;
                 if(this.remainSong==0){
                     //game end
                     this.endGame();
                 }else{
                    console.log("name is"+this.room.ownerName+" / "+this.sender);
                    if(!flag){
                     this.timer();
                    }
                     this.audioSource="/"+this.room.gameIndex+"-"+this.room.seq+".mp3";
                     this.playAudio();
                 }
           }, 3000)


        },
        skipVote: function() {
            //버튼 누르면 투표 수 증가
            this.skipBtnMsg='투표완료';
            this.sendMessage('VOTE');
        },
        endGame : function(){
            //game end
            this.endGameDiv=true;
            this.gameDiv=false;
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


