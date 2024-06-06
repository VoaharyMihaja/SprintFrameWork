package mg.itu.prom16;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.util.ClassScanner;
import mg.itu.prom16.annotation.Controller;
import mg.itu.prom16.mapping.Mapping;
import mg.itu.prom16.modelView.ModelView;

public class FrontController extends HttpServlet {

    Map<String,Mapping> controllerList;
    Class<? extends Annotation > annClass=Controller.class;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            controllerList=ClassScanner.getMapping(getInitParameter("basePackageName"), annClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            response.setContentType("text/html");

            try (PrintWriter out = response.getWriter()) {
                out.println("<html><body>");
                out.println("<h1>Servlet Path: " + request.getServletPath() + "</h1>");
                String path = request.getServletPath().trim();
                Mapping map = controllerList.get(path);
                if (map!=null) {
                    out.println(map.getMethod().getName()+"--"+map.getControlleClass().getSimpleName());    

                    //--sprint3
                    // Object instance = map.getControlleClass().getDeclaredConstructor().newInstance();
                    // Object value = map.getMethod().invoke(instance);

                    // out.println("Valeur methode :" + value.toString());
                    //--end sprint3

                    // sprint4
                    Object instance = map.getControlleClass().getDeclaredConstructor().newInstance();
                    Object valueFunction = map.getMethod().invoke(instance);
                    System.out.println(valueFunction);

                    // rehefa modelView le Objet azo amle valueFunction dia avadika Objet ModelVIiew le izy
                    if(valueFunction instanceof ModelView){
                        ModelView modelAndView = (ModelView)valueFunction;

                        // j'ai recuperer son nom et ses donnees
                        String nameView = modelAndView.getNameView();
                        HashMap<String, Object> listKeyAndValue = modelAndView.getListValue();

                        for (Map.Entry<String, Object> mapp : listKeyAndValue.entrySet()) {
                            request.setAttribute(mapp.getKey(), mapp.getValue());
                        }
                        RequestDispatcher dispatcher = request.getRequestDispatcher(nameView);
                        dispatcher.forward(request, response);
                    }
                }
                else{
                    out.println("Nothing found!");    
                }
                
                
                out.println("</body></html>");
            }
        } catch (Exception e) {
            throw new ServletException(e);    
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
}
