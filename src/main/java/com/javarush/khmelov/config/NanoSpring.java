package com.javarush.khmelov.config;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

@UtilityClass
public class NanoSpring {

    private static final Map<Class<?>, Object> beans = new HashMap<>();
    public static final String CLASSES = File.separator + "classes" + File.separator;
    public static final String EXT = ".class";
    public static final String DOT = ".";
    public static final String EMPTY = "";

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> T find(Class<T> type) {
        if (beanDefinitions.isEmpty()) {
            init(); //1.add abstraction<?>
        }
        Object component = beans.get(type);
        if (component == null) {
            Constructor<?> constructor = type.getConstructors()[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Type[] genericParameterTypes = constructor.getGenericParameterTypes(); //2.
            Object[] parameters = new Object[parameterTypes.length];
            for (int i = 0; i < parameters.length; i++) {
                Class<?> impl = findImpl(parameterTypes[i], genericParameterTypes[i]); //3.
                parameters[i] = find(impl);
            }
            //Object newInstance = constructor.newInstance(parameters);
            Object newInstance = checkTransactional(type)
                    ? constructProxyInstance(type, parameterTypes, parameters)
                    : constructor.newInstance(parameters);
            beans.put(type, newInstance);
        }
        return (T) beans.get(type);
    }

    //********************* add support abstraction<?>  ************************
    private final List<Class<?>> beanDefinitions = new ArrayList<>();

    @SneakyThrows
    private void init() {
        URL resource = NanoSpring.class.getResource("NanoSpring.class");
        URI uri = Objects.requireNonNull(resource).toURI();
        Path appRoot = Path.of(uri).getParent().getParent();
        scanPackages(appRoot, "Controller", "Servlet", "Filter");
    }

    public void scanPackages(Path appPackage, String... excludes) {
        try (Stream<Path> walk = Files.walk(appPackage)) {           //в app root
            List<String> names = walk.map(Path::toString)           //рекурсия по
                    .filter(o -> o.endsWith(EXT))                  //всем классам
                    .filter(o -> Arrays.stream(excludes)          //кроме
                            .noneMatch(o::contains))             //запрещенных
                    .map(s -> s.substring(getStartClassName(s)))//".../classes/"
                    .map(s -> s.replace(EXT, EMPTY))           //и ".class" удалим
                    .map(s -> s.replace(File.separator, DOT)) //имена через точки
                    .toList();                               //соберем как строки
            for (String name : names) {                     //которые переведем
                beanDefinitions.add(Class.forName(name));  //в классы
            }                                             //готово
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private int getStartClassName(String s) {
        return s.indexOf(NanoSpring.CLASSES) + NanoSpring.CLASSES.length();
    }

    private Class<?> findImpl(Class<?> aClass, Type type) {
        for (Class<?> beanDefinition : beanDefinitions) {
            boolean assignable = aClass.isAssignableFrom(beanDefinition);
            boolean nonGeneric = beanDefinition.getTypeParameters().length == 0;
            boolean nonInterface = !beanDefinition.isInterface();
            boolean nonAbstract = !Modifier.isAbstract(beanDefinition.getModifiers());
            boolean checkGenerics = checkGenerics(type, beanDefinition);
            if (assignable & nonGeneric & nonInterface & nonAbstract && checkGenerics) {
                return beanDefinition;
            }
        }
        throw new RuntimeException("Not found impl for %s (type=%s)".formatted(aClass, type));
    }

    private boolean checkGenerics(Type type, Class<?> impl) {
        var typeContractGeneric = NanoSpring.getContractGeneric(type);
        return Objects.nonNull(impl) &&
               Stream.iterate(impl, Objects::nonNull, (Class<?> c) -> c.getSuperclass())
                       .flatMap(c -> Stream.concat(
                               Stream.of(c.getGenericSuperclass()),
                               Stream.of(c.getGenericInterfaces())))
                       .filter(Objects::nonNull)
                       .map(NanoSpring::getContractGeneric)
                       .anyMatch(typeContractGeneric::equals);
    }

    private List<? extends Class<?>> getContractGeneric(Type type) {
        var typeName = type.getTypeName();
        return !typeName.contains("<")
                ? List.of()
                : Arrays.stream(typeName
                        .replaceFirst(".+<", EMPTY)
                        .replace(">", EMPTY)
                        .split(","))
                .map(NanoSpring::getaClassOrNull)
                .toList();
    }

    private Class<?> getaClassOrNull(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    //********************* add proxy (for @Transactional)  ************************

    private <T> boolean checkTransactional(Class<T> type) {
        //тут вообще-то довольно грубо сделано,
        //надо бы лучше в динамике проверять каждый метод если класс не отмечен
        //и запускать прокси только в нужных местах.
        return type.isAnnotationPresent(Transactional.class)
               || Arrays.stream(type.getMethods())
                       .anyMatch(method -> method.isAnnotationPresent(Transactional.class));
    }

    @SneakyThrows
    private Object constructProxyInstance(Class<?> type, Class<?>[] parameterTypes, Object[] parameters) {
        Class<?> proxy = new ByteBuddy()
                .subclass(type)
                .method(isDeclaredBy(ElementMatchers.isAnnotatedWith(Transactional.class))
                        .or(ElementMatchers.isAnnotatedWith(Transactional.class)))
                .intercept(MethodDelegation.to(Interceptor.class))
                .make()
                .load(type.getClassLoader())
                .getLoaded();
        Constructor<?> constructor = proxy.getConstructor(parameterTypes);
        return constructor.newInstance(parameters);
    }


    public class Interceptor {
        @RuntimeType
        public static Object intercept(@This Object self,
                                       @Origin Method method,
                                       @AllArguments Object[] args,
                                       @SuperMethod Method superMethod) throws Throwable {
            SessionCreator sessionCreator = find(SessionCreator.class);
            sessionCreator.beginTransactional();
            try {
                return superMethod.invoke(self, args);
            } finally {
                sessionCreator.endTransactional();
            }
        }
    }
}
