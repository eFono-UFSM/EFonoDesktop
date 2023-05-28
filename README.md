## Guidelines

### Database Access

At this moment, you'll need to create a `config.properties` following the structure. If this file couldn't be found, then the default credentials will be used. 

Example:
```
# Mysql credentials
mysql.server=localhost
mysql.port=22
mysql.user=root
mysql.pass=joaoteste
mysql.database=mydb
```

Then, copy the file path and pass it as first parameter to main function.


### Execute and Build

1. `mvn dependency:resolve -U`
2. `mvn clean install`

