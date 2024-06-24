package mg.itu.prom16;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
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

import mg.itu.prom16.annotation.ObjectParameter;

import mg.itu.prom16.annotation.Param;
import mg.itu.prom16.exception.DuplicateLinkException;
import mg.itu.prom16.exception.ReturnTypeException;
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
            
        } catch (DuplicateLinkException f){
            throw new Error(f);
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

                    // sprint4 + sprint6
                    Object instance = map.getControlleClass().getDeclaredConstructor().newInstance();

                    // sprint 6
                    Parameter[] params = map.getMethod().getParameters();
                    HashMap<String,Object> listValueF = new HashMap<>();
                    Object[] listValueParameter = new Object[params.length];
                    for (Parameter param : params) {
                        String nameParam = param.getName();
                        String valueF = "";
                        if (param.getAnnotation(Param.class) != null) {
                            nameParam = param.getAnnotation(Param.class).value();
                            valueF = request.getParameter(nameParam);
                        }
                        else if (param.getAnnotation(ObjectParameter.class) != null) {
                            Field[] objParamFields = param.getType().getDeclaredFields(); // les attributs d'objet
                            Field.setAccessible(objParamFields, true);
                            for (Field field : objParamFields) {
                                listValueF.put(field.getName(), request.getParameter(field.getName()));
                            }
                            continue;
                        }
                        listValueF.put(nameParam,valueF);
                    }
                    int i = 0;
                    for (Parameter param : params) {
                        // sprint 7
                        if (param.getAnnotation(ObjectParameter.class) != null) {
                            Object objParam = param.getType().getDeclaredConstructor().newInstance(); // declarer instance de l'objets
                            Field[] objParamFields = param.getType().getDeclaredFields(); // les attributs d'objet
                            Field.setAccessible(objParamFields, true);
                            for (Field field : objParamFields) {
                                field.set(objParam, listValueF.get(field.getName()));
                                System.out.println(field.getName());
                            }
                            listValueParameter[i] = objParam;
                        }
                        else if (param.getAnnotation(Param.class) != null) {
                            listValueParameter[i] = listValueF.get(param.getAnnotation(Param.class).value());
                        }
                        i++;
                    }
                    Object valueFunction = map.getMethod().invoke(instance, listValueParameter);

                    Parameter[] params = map.getMethod().getParameters();
                    List<Object> listValueF = new ArrayList<>();
                    for (Parameter param : params) {
                        String valueF = "";
                        if (param.getAnnotation(Param.class) != null) {
                            String annotValue = param.getAnnotation(Param.class).value();
                            valueF = request.getParameter(annotValue);
                        }
                        else{
                            String nameParam = param.getName();
                            valueF = request.getParameter(nameParam);
                        }
                        listValueF.add(valueF);
                    }
                    Object valueFunction = map.getMethod().invoke(instance, listValueF.toArray());


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
                    else if (valueFunction instanceof String) {
                        System.out.println(valueFunction);
                    }
                    else{
                        try {
                            throw new ReturnTypeException();
                        } catch (ReturnTypeException e) {
                            throw new Error(e);
                        }
                    }
                }

                else{
                    response.sendError(404); 
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
