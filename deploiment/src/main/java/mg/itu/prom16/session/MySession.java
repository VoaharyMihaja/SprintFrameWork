package mg.itu.prom16.session;

import jakarta.servlet.http.HttpSession;

public class MySession{
    private HttpSession session;

    public MySession(HttpSession session) {
        this.session = session;
    }

    public MySession() {
    }


    public HttpSession getHttpSession(){
        return session;
    }

    public void setHttpSession(HttpSession session){
        this.session = session;
    }

    public Object get(String key){
        return session.getAttribute(key);
    }

    public void set(String key, Object obj){
        session.setAttribute(key, obj);
    }

    public void delete(String key){
        session.removeAttribute(key);
    }
}