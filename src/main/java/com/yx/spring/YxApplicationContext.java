package com.yx.spring;

import com.yx.spring.annotation.Autowired;
import com.yx.spring.annotation.Component;
import com.yx.spring.annotation.ComponentScan;
import com.yx.spring.annotation.Scope;
import com.yx.spring.aop.AnnotationAwareAspectJAutoProxyCreator;
import com.yx.spring.core.BeanDefinition;
import com.yx.spring.enums.ScopeType;
import com.yx.spring.iface.*;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiao
 * @date 2021/5/30 15:27
 */
public class YxApplicationContext {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private final List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    /**
     * Cache of singleton factories: bean name to ObjectFactory.
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

    /**
     * Cache of early singleton objects: bean name to bean instance.
     */
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

    /**
     * 单例池： beanName -> beanObj
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * Names of beans that are currently in creation.
     */
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    /**
     * Names of Prototype beans that are currently in creation.
     */
    private final ThreadLocal<Object> prototypesCurrentlyInCreation = new ThreadLocal<>();

    public YxApplicationContext(Class<?> configClass){

        scan(configClass);

        registerBeanPostProcessors();

        // 将单例bean实例化
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            if (beanDefinition.isSingleton()) {
                getBean(beanName);
            }
        });
    }

    private void registerBeanPostProcessors(){
        // 注册后置处理器，用于aop
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setType(AnnotationAwareAspectJAutoProxyCreator.class);
        beanDefinition.setScope(ScopeType.SINGLETON.getValue());
        beanDefinitionMap.put("autoProxyCreator", beanDefinition);

        //储存BeanPostProcessor
        beanDefinitionMap.entrySet().stream().filter(entry -> BeanPostProcessor.class.isAssignableFrom(entry.getValue().getType()))
                .forEach(entry -> {
                    BeanPostProcessor curPostProcessor = (BeanPostProcessor) getBean(entry.getKey());
                    beanPostProcessorList.add(curPostProcessor);
                });

    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new RuntimeException("获取beanName[" + beanName + "]失败");
        } else {
            // 单例
            if (beanDefinition.isSingleton()) {
                Object singletonObject = getSingleton(beanName, true);
                //若未获取到，则创建
                if (singletonObject == null) {
                    singletonObject = createBean(beanName, beanDefinition);
                    //放入一级缓存（广义单例池）
                    this.singletonObjects.put(beanName, singletonObject);
                    this.earlySingletonObjects.remove(beanName);
                    this.singletonFactories.remove(beanName);
                }
                return singletonObject;
            } else {
                return createBean(beanName, beanDefinition);
            }
        }
    }

    public List<Class<?>> getAllBeanClass() {
        return beanDefinitionMap.values()
                .stream()
                .map((Function<BeanDefinition, Class<?>>) BeanDefinition::getType)
                .collect(Collectors.toList());
    }

    /**
     * 单例创建中
     * @param beanName
     */
    private void beforeSingletonCreation(String beanName) {
        if (this.singletonsCurrentlyInCreation.contains(beanName)) {
            throw new IllegalStateException("Error creating singleton bean with name '" + beanName + "': "
                    + "Requested bean is currently in creation: Is there an unresolvable circular reference?");
        }
        this.singletonsCurrentlyInCreation.add(beanName);
    }

    /**
     * 原型创建中
     * @param beanName
     */
    private void beforePrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal != null &&
                (curVal.equals(beanName) || (curVal instanceof Set && ((Set<?>) curVal).contains(beanName)))) {
            throw new IllegalStateException("Error creating prototype bean with name '" + beanName + "': "
                    + "Requested bean is currently in creation: Is there an unresolvable circular reference?");
        }
        // 放入ThreadLocal
        if (curVal == null) {
            this.prototypesCurrentlyInCreation.set(beanName);
        } else if (curVal instanceof String) {
            Set<String> beanNameSet = new HashSet<>();
            beanNameSet.add((String) curVal);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
        } else {
            Set<String> beanNameSet = (Set<String>) curVal;
            beanNameSet.add(beanName);
        }
    }

    /**
     * 根据类型放入创建中集合
     * @param beanName
     * @param beanDefinition
     */
    private void beforeCreation(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton()) {
            beforeSingletonCreation(beanName);
        } else {
            beforePrototypeCreation(beanName);
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        // 判断是否在创建中
        beforeCreation(beanName, beanDefinition);
        try {
            // 创建对象
            Object bean = createBeanInstance(beanName, beanDefinition);

            // 单例提前放入三级缓存
            if (beanDefinition.isSingleton()) {
                this.singletonFactories.put(beanName, new ObjectFactory<Object>() {
                    @Override
                    public Object getObject() throws RuntimeException {
                        Object exposedObject = bean;
                        for (BeanPostProcessor beanPostProcessor : YxApplicationContext.this.beanPostProcessorList) {
                            if (beanPostProcessor instanceof SmartInstantiationAwareBeanPostProcessor) {
                                exposedObject = ((SmartInstantiationAwareBeanPostProcessor) beanPostProcessor).getEarlyBeanReference(exposedObject, beanName);
                            }
                        }
                        return exposedObject;
                    }
                });
                this.earlySingletonObjects.remove(beanName);
            }

            Object exposedObject = bean;
            populateBean(beanName, beanDefinition, bean);
            exposedObject = initializeBean(beanName, beanDefinition, exposedObject);

            return exposedObject;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            afterCreation(beanName, beanDefinition);
        }
    }

    /**
     * 创建结束处理
     * @param beanName
     * @param beanDefinition
     */
    private void afterCreation(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton()) {
            afterSingletonCreation(beanName);
        } else {
            afterPrototypeCreation(beanName);
        }
    }

    /**
     * 原型创建结束
     * @param beanName
     */
    private void afterPrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
        } else if (curVal instanceof Set) {
            Set<String> beanNameSet = (Set<String>) curVal;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }
    }

    /**
     * 单例创建结束
     * @param beanName
     */
    private void afterSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.contains(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
        this.singletonsCurrentlyInCreation.remove(beanName);
    }

    /**
     * 初始化
     *
     * @param beanName
     * @param beanDefinition
     * @param bean
     * @return
     */
    private Object initializeBean(String beanName, BeanDefinition beanDefinition, Object bean) {
        //aware回调
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) (bean)).setBeanName(beanName);
        }
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) (bean)).setApplicationContext(this);
        }

        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            bean = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
        }

        if (bean instanceof InitializingBean) {
            ((InitializingBean) (bean)).afterPropertiesSet();
        }

        // aop代理实现
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            bean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
        }
        return bean;
    }


    /**
     * 依赖注入，这里只解析Autowired
     *
     * @param beanName
     * @param beanDefinition
     * @param bean
     */
    private void populateBean(String beanName, BeanDefinition beanDefinition, Object bean) throws IllegalAccessException, InvocationTargetException {
        Class clazz = beanDefinition.getType();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Autowired.class)) {
                String paramName = method.getParameters()[0].getName();
                method.invoke(bean, getBean(paramName));
            }
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                field.set(bean, getBean(field.getName()));
            }
        }

    }

    /**
     * 获取单例
     * @param beanName
     * @param allowEarlyReference
     * @return
     */
    private Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {
                // 三级缓存
                ObjectFactory<?> objectFactory = this.singletonFactories.get(beanName);
                if (objectFactory != null) {
                    singletonObject = objectFactory.getObject();
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    this.singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) throws Throwable {
        Class<?> clazz = beanDefinition.getType();
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return constructor.newInstance();
            }
        }
        // 没有无参构造，选第一个构造器
        Constructor<?> constructor = constructors[0];
        Object[] args = new Object[constructor.getParameterCount()];
        Parameter[] parameters = constructor.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = null;
            if (parameter.getType().equals(ObjectFactory.class)) {
                arg = buildLazyObjectFactory(parameter.getName());
            } else {
                // 从容器中拿
                arg = getBean(parameter.getName());
            }
            args[i] = arg;
        }
        return constructor.newInstance(args);
    }

    private Object buildLazyObjectFactory(String requestBeanName) {
        return new ObjectFactory<Object>() {
            @Override
            public Object getObject() throws RuntimeException {
                return getBean(requestBeanName);
            }
        };
    }


    /**
     * 解析bean实例
     * @param configClass
     */
    private void scan(Class<?> configClass){
        if(configClass.isAnnotationPresent(ComponentScan.class)){
            // 解析扫描路径，放入beanDefinitionMap
            ComponentScan componentScanAnnotation = configClass.getAnnotation(ComponentScan.class);
            String scanPath = componentScanAnnotation.value();
            scanPath = scanPath.replace(".", "/");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(scanPath);
            if(resource == null){
                System.out.println(scanPath + "文件资源不存在");
                return;
            }
            File targetFile = new File(resource.getFile());
            File[] files = targetFile.listFiles();
            if(files == null){
                System.out.println(scanPath + "获取文件资源失败");
                return;
            }
            for(File scanFile : files){
                String curFileName = scanFile.getAbsolutePath();
                if(!curFileName.endsWith(".class")){
                    continue;
                }
                //取出类的全限定名
                String className = curFileName.substring(curFileName.indexOf("com"), curFileName.indexOf(".class")).replace(File.separator, ".");
                try {
                    Class<?> curScanClass = classLoader.loadClass(className);
                    if(curScanClass.isAnnotationPresent(Component.class)){
                        Component componentAnnotation = curScanClass.getAnnotation(Component.class);
                        String beanName = componentAnnotation.value();
                        if("".equals(beanName)){
                            // 未指定beanName，使用类名首字母小写
                            beanName = Introspector.decapitalize(curScanClass.getSimpleName());
                        }
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setType(curScanClass);
                        if (curScanClass.isAnnotationPresent(Scope.class)) {
                            Scope scopeAnnotation = curScanClass.getAnnotation(Scope.class);
                            beanDefinition.setScope(Arrays.asList(ScopeType.SINGLETON.getValue(), ScopeType.PROTOTYPE.getValue()).contains(scopeAnnotation.value())?scopeAnnotation.value():ScopeType.SINGLETON.getValue());
                        } else {
                            beanDefinition.setScope(ScopeType.SINGLETON.getValue());
                        }
                        beanDefinitionMap.put(beanName, beanDefinition);
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }


        }




    }


}
