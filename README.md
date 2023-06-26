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

# MongoDB credentials
mongodb.server=localhost
mongodb.port=27017
mongodb.user=efono
mongodb.password=efono1234
mongodb.database=efono
```

Then, copy the file path and pass it as first parameter to main function.


### Execute and Build

1. `mvn dependency:resolve -U`
2. `mvn clean install`

