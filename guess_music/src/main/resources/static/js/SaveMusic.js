function fileUpload(e){
    e.preventDefault();
    let file=event.target.files[0];
    let formData=new FormData();
    formData.append('file',file);
}
