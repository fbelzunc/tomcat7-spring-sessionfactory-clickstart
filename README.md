Welcome to Tomcat 7 Spring SessionFActory ClickStart

This is a "ClickStart" that gets you going with a simple Spring Hibernate SessionFactory "seed" project starting point, which will show you how to use the JNDI injection through Spring Profiles. You can launch it here:

<a href="https://grandcentral.cloudbees.com/?CB_clickstart=https://raw.github.com/fbelzunc/tomcat7-spring-hibernate-jpa-clickstart/master/clickstart.json"><img src="https://d3ko533tu1ozfq.cloudfront.net/clickstart/deployInstantly.png"/></a>

This will setup a continuous deployment pipeline - a CloudBees Git repository, a Jenkins build compiling and running the test suite (on each commit).
Should the build succeed, this seed app is deployed on a Tomcat 7 container.

## Application Overview

### Spring Profiles -datasource-config.xml ###

Spring Profiles will allow you to use different environments at CloudBees platform without doing any modification on your application. You can just create a many profiles as you would like for your application. For example, you could have a different profile for staging, testing and production. In this way, you just need one version of your application to work with the different environments.

```xml
    <beans profile="javaee" >
        <!-- JNDI DataSource for JEE environments -->
        <!-- use relative jndi-name + resource-ref=true for https://jira.springsource.org/browse/SPR-4585 -->
        <jee:jndi-lookup id="dataSource" jndi-name="jdbc/mydb" resource-ref="true"/>
    </beans>
```

# Create application manually

### Create Tomcat container

```sh
bees app:create -a product -t tomcat7
```

### Create CloudBees MySQL Database

```sh
bees db:create product-db
```

### Bind Tomcat container to database

```sh
bees app:bind -a product -db product-db -as product
```

### Activate Spring Profiles on CloudBees platform

```sh
bees config:set -Pspring.profiles.active=javaee
```

### Deploy your application

```sh
bees app:deploy -a product -t tomcat7 app.war
```

# Setting-up the development environment on your own machine

There are two different possibilities to do the JNDI configuration into your local Tomcat Java container. Please, have special attention to: name, password, url and username fields.

###Option 1. Using $CATALINA_HOME/conf/server.xml (preferable method)

####Add this to $CATALINA_HOME/conf/server.xml

```xml
  <GlobalNamingResources>
     <Resource auth="Container" driverClassName="com.mysql.jdbc.Driver"
        factory="org.apache.tomcat.jdbc.pool.DataSourceFactory" maxActive="20" maxIdle="10"
        minIdle="1"   name="jdbc/mydb" password="dbUserPassword" testOnBorrow="true"
        testWhileIdle="true" type="javax.sql.DataSource" url="jdbc:mysql://localhost:3306/dbName"
        username="dbUserName" validationInterval="5000" validationQuery="select 1"/>
  </GlobalNamingResources/>
```

####Create the ROOT.xml file under $CATALINA_HOME/conf/Catalina/localhost/

Now, you must declare the application-scope binding in addition to the global binding on a new file called $CATALINA_HOME/conf/[enginename]/[hostname]/ROOT.xml. For example $CATALINA_HOME/conf/Catalina/localhost/ROOT.xml

```xml
<Context>
<ResourceLink name="jdbc/mydb"
   global="jdbc/mydb"
   type="javax.sql.DataSource"/>
</Context>
```

###Option 2. Using the $CATALINA_HOME/conf/context.xml

You have to add this to the context.xml file. Please, change the the corresponding values.

```xml
<Resource auth="Container" driverClassName="com.mysql.jdbc.Driver" factory="org.apache.tomcat.jdbc.pool.DataSourceFactory" maxActive="20" maxIdle="10" minIdle="1" name="jdbc/mydb" password="dbUserPassword" testOnBorrow="true" testWhileIdle="true" type="javax.sql.DataSource" url="jdbc:mysql://localhost:3306/dbName" username="dbUserName" validationInterval="5000" validationQuery="select 1"/>
```
### Enable the javaee profile on your development environment

To enable this profile on your development environment you have to add this option on your VM.

```xml
-Dspring.profiles.active=javaee
```
# Configure JPA and Hibernate in your application

#### Declare Hibernate and JPA jars in your Maven pom.xml

```xml
<project ...>
    <dependencies>
        <dependency>
            <groupId>javax.persistence</groupId>
            <artifactId>persistence-api</artifactId>
            <version>1.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>4.2.4.Final</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <version>4.2.4.Final</version>
        </dependency>
        ...
    </dependencies>
</dependencies>
```

### Using Placeholder to inject the showSql and the generateDdl value from a configuration file

With Placeholder you can have a configuration file on your application and inject the value of these variables on the places that you would like on your application.

```xml
 <context:property-placeholder system-properties-mode="OVERRIDE" location="classpath:app.properties"/>
```

### Declare the SessionFactory -datasource-config.xml

```xml
    <!-- Hibernate SessionFactory-->
    <bean id="sessionFactory" class="org.springframework.orm.hibernate4.LocalSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="hibernateProperties">
            <value>
                hibernate.dialect=org.hibernate.dialect.HSQLDialect
            </value>
        </property>
        <property name="configLocation">
            <value>classpath:hibernate.cfg.xml</value>
        </property>
    </bean>

    <!-- Transaction manager for a single Hibernate Session Factory -->
    <bean id="transactionManager" class="org.springframework.orm.hibernate4.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory" />
        <property name="dataSource" ref="dataSource" />
    </bean>
```



