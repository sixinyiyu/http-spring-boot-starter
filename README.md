# http-spring-boot-starter

基于SpringBoot autoconfigure 将Http服务做成自动注入服务

## 安装使用

```
mvn install -Dmaven.test.skip=true
```

pom:

```
<dependency>
    <groupId>com.github.sixinyiyu</groupId>
	<artifactId>http-spring-boot-starter</artifactId>
    <version>${http-spring-boot-starter.version}</version>
</dependency>
```

当前版本：1.0.0.RELEASE


最后即可在项目中注解注入 ```OkHttpClient``` 接口
