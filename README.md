# spring-simple
一个spring的简单实现，主要实现了 IOC 和 AOP 功能

## IOC
完成了 @ComponentScan 组件扫描
包括：Bean后处理器 [BeanPostProcessor](src/com/mafei/spring/interfaces/BeanPostProcessor.java) 的调用、各种 Aware 接口回调。

依赖注入使用 @Autowired 查找依赖

## AOP
完成了常用的 @Before、@After的解析，对符合切点的目标对象进行代理增强


## 功能测试 Demo
在 `com.yx.test` 包下