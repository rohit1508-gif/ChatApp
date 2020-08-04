package com.example.chatapp.ModalClass;

public class User {
    private String uid;
    private String name;
    private String imageUrl;
    String status;
public User(){

}

public User(String uid,String name,String imageUrl,String status){
      this.uid = uid;
      this.name=name;
      this.imageUrl = imageUrl;
      this.status = status;
}

    public String getUid(){
      return uid;
}
public String getName(){
      return name;
}
public String getImageUrl(){
      return  imageUrl;
}
public String getStatus(){
    return status;
}
}

