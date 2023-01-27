fetch("/getsong").then(res=>res.blob()).then(blob=>{
    console.log("pass1");
    var file = window.URL.createObjectURL(blob);
    console.log(file);
    console.log("pass2");
    var audio = document.getElementById('music');
    console.log("pass3");
    audio.src=file;
    console.log("pass4");
    audio.play();
    //window.location.assign(file);
})
