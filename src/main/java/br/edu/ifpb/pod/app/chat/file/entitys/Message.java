package br.edu.ifpb.pod.app.chat.file.entitys;

import java.io.Serializable;

/**
 *
 * @author João Marcos F <joaomarccos.ads@gmail.com>
 */
public class Message implements Comparable<Message>, Serializable{
    private long id;
    private String userName;
    private String msg;

    public Message(String userName, String msg) {
        this.id = System.currentTimeMillis();
        this.userName = userName;
        this.msg = msg;
    }

    public Message(long id, String userName, String msg) {
        this.id = id;
        this.userName = userName;
        this.msg = msg;
    }        

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return this.id+":"+this.userName+":"+this.msg;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((Message) obj).getId();
    }
    
    @Override
    public int hashCode() {
        return new Long(this.id).hashCode();
    }        

    @Override
    public int compareTo(Message m) {
       return (int) (this.getId()-m.getId());
    }
    
    
}
