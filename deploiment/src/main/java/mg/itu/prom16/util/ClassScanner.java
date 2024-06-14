package mg.itu.prom16.util;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;

import java.lang.reflect.Method;

import mg.itu.prom16.mapping.Mapping;
import mg.itu.prom16.annotation.Get;
import mg.itu.prom16.exception.DuplicateLinkException;
import mg.itu.prom16.exception.NotFoundPackageException;
public class ClassScanner {

    public static Map<String,Mapping> getMapping(String packageName,Class<? extends Annotation> annotationClass) throws ClassNotFoundException, IOException, Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        System.err.println(packageName);
        String packagePath = packageName.replace(".", "/");

        URL url = Thread.currentThread().getContextClassLoader().getResource(packagePath);
        if (url == null) {
            
            // throw new Exception("Package :" + packageName + "nom trouve");
            throw new NotFoundPackageException(packageName);

        }
    
        System.out.println(url.toString());
        File directory = new File(url.toURI());
        File[] files = directory.listFiles();


        Enumeration<URL> resources = classLoader.getResources(packagePath);
    
        Map<String,Mapping> map=new HashMap<String,Mapping>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                // File directory = new File(resource.getPath());

                System.out.println("Name dir : " + directory.getName() + " " + directory.listFiles().length);

                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                        Class<?> class1=Class.forName(className);
                        System.out.println("class name : " + class1.getName()+ "ann class : " + annotationClass.getName()); 
                        if (class1.isAnnotationPresent(annotationClass)) {
                            System.out.println("taf   ");
                            map.putAll(getMethods(class1));                            
                        }
                    }
                }
            }
        }
        return map;
    }    
    private static Map<String,Mapping> getMethods(Class<?> clazz) throws DuplicateLinkException{
        
        Map<String,Mapping> methods = new HashMap<String,Mapping>();
        for (Method method : clazz.getDeclaredMethods()) {
            Get an= method.getAnnotation(Get.class);

            if(an!=null&& !an.url().isBlank()){
                String key = an.url().trim();
                if (methods.containsKey(key)) {
                    throw new DuplicateLinkException(key);
                }
                methods.put(key,new Mapping(clazz,method));
            }
        }
        return methods;
    }
    
}
