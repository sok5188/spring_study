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
        remainSong:0,
        totalSong:0,
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
        this.findUser();
        this.findRoom();
    },
    methods: {
        unLoadEvent: function (event) {
          event.preventDefault();
          this.sendMessage('LEAVE');
        },
        findRoom:async function() {
            console.log("findRoom Called");
            const response= await axios.get('/Game/room/'+this.roomId)
            this.room = response.data;
            if(this.room.roomStatus=="WAITING"){
                console.log("now waiting");
                this.totalSong=this.room.songNum;
                this.remainSong=this.room.songNum;
                if(this.sender==this.room.ownerName)
                    this.startDiv=true;
            }
            console.log("findRoom Exit");
        },
        findUser: async function(){
            //sender를 찾는 함수( 사실 create된 시점에만 필요함.)
            console.log("findUser Called");
          const response=await axios.get('/Game/getUser')
          this.sender=response.data;
          console.log("findUser return");
        },
        findUsers:async function(){
        //방 내의 유저 목록 및 유저 수를 갱신하는 함수
            console.log("findUsers called");
            const res=await axios.get("/Game/getUsers/"+this.roomId)
            this.users=res.data;
            this.users.sort((a,b)=>{
                return b.score-a.score;
            })
            this.userNum=this.users.length;
            console.log("userNum :"+this.userNum);
            var half= Math.floor(this.userNum/2);
            this.voteLimit=half +1;
            console.log("findUsers return");
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
        recvMessage: async function(recv) {
            if(recv.type=="ENTER"){
                console.log("entered!")
                const res=await axios.get("/Game/getUserCount/"+this.roomId);
                this.userNum=res.data;

                console.log("enter return, roomNum :"+this.userNum);
            }
            if(recv.type=='START'){
                console.log("in recv it is START");
                this.findUsers();
                this.startGame();
            }else{
                if(recv.type=='VOTE'){
                    this.nowVote=this.nowVote+1;
                    //누군가가 skip을 누르면 skipDiv를 보여준다
                    this.skipDiv=true;
                    //굳이 checkskip불러서 api호출 하는 것 보다 코드적으로만 스킵 체크 하고 스킵 호출 !
                    if(this.nowVote>=this.voteLimit){
                        console.log("vote is over the limit nowVote:"+this.nowVote+" voteLimit:"+this.voteLimit);
                        this.skipSong();
                    }
                }else if(recv.type=='ANSWER'){
                    this.findUsers();
                    this.skipSong();
                }
                else if(recv.type=='LEAVE'){
                    await this.findRoom();
                    if(this.room.roomStatus=="WAITING"){
                        axios.get("/Game/getUserCount/"+this.roomId).then(res=>{this.userNum=res.data;})
                    }else{
                        await this.findUsers();
                        this.checkSkip();
                    }
                }
                this.messages.unshift({"type":recv.type,"sender":recv.type!='TALK'?'[알림]':recv.sender,"message":recv.message})
            }



        },
        checkSkip :async function(){
            console.log("checkSkip called");
            console.log("now vote in checkSKip:"+this.nowVote+" voteLimit:"+this.voteLimit);
            if(this.nowVote>=this.voteLimit){
                console.log("vote is over the limit nowVote:"+this.nowVote+" voteLimit:"+this.voteLimit);
                this.skipSong();
            }
            console.log("checkSkip return");
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
          this.fetchAudioSource();
          this.skipBtnMsg="스킵 투표";
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
        fetchAudioSource:function(){
            this.audio=new Audio();
            fetch("/Game/getMusic/"+this.roomId).then(response=>response.body).then(rs=>{
                const reader = rs.getReader();
                return new ReadableStream({
                  async start(controller) {
                    while (true) {
                      const { done, value } = await reader.read();
                      // When no more data needs to be consumed, break the reading
                      if (done) {
                        break;
                      }
                      // Enqueue the next data chunk into our target stream
                      controller.enqueue(value);
                    }
                    // Close the stream
                    controller.close();
                    reader.releaseLock();
                  }
                })
            }).then(rs=>new Response(rs)).then(response=>response.blob()).then(blob=>URL.createObjectURL(blob)).then(url=>{
                this.audio.src=url;
            }).then(rs=>this.audio.play()).catch(console.error);
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
        skipSong: async function() {
            console.log("in skip song");
            this.block=true;
            this.skipStatus="skipping";
            this.skipText="투표로 인해 노래가 스킵됩니다 !"
            this.singerHintText='';
            this.initialHintText='';
            this.singerDiv=false;
            this.initialDiv=false;
            this.gotAnswerDiv=true;
            console.log("in skip, call get answer")
            const response=await axios.get("/Game/getAnswer/"+this.roomId)
            this.answerText=response.data[0];
            this.singerHintText=response.data[1];
            console.log("in skip, after get answer and call if or else ..")
            if(this.remainSong>1){
                 let ownFlag='nope';
                 if(this.sender==this.room.ownerName){
                     console.log("owner will send owner flag")
                     ownFlag='owner'
                 }
                 await axios.get("/Game/skip",{params:{roomId:this.roomId,type:ownFlag}})
            }
            console.log("all done to skip will call time out ");
            setTimeout(() => {
                console.log("Time Out! will reset game set")
                if(this.remainSong==1){
                     //game end
                     this.endGame();
                }
                this.skipStatus="";
                this.skipText="";
                var flag=false;
                if(this.countDown>0){
                    flag=true;
                }
                 this.countDown = 60;
                 this.room.seq=this.room.seq+1;
                 this.remainSong=this.remainSong-1;
                 this.answerText='';
                 this.gotAnswerDiv=false;
                 this.block=false;
                 this.skipDiv=false;
                 this.skipBtnMsg='스킵 투표';
                 this.nowVote=0;
                 if(!flag){
                  this.timer();
                 }
                 this.audio.pause();
                 this.fetchAudioSource();
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


