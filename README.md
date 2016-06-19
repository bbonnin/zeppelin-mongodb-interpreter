# zeppelin-mongodb-interpreter
MongoDB interpreter for Apache Zeppelin

## Deployment

* Update `$ZEPPELIN_HOME/conf/zeppeln-site.xml`
```xml
<><property>
  <name>zeppelin.interpreters</name>
  <value>...,org.apache.zeppelin.mongodb.MongoDbInterpreter</value>
</property>
```
* Create `$ZEPPELIN_HOME/interpreter/mongodb`
* Copy interpreter jar in `$ZEPPELIN_HOME/interpreter/mongodb`


## Configuration

<table>
  <tr><th>Parameter</th><th>Default value</th><th>Description</th></tr>
  <tr><td>mongo.shell.path</td><td>mongo</td><td>Mongo shell path</td></tr>
  <tr><td>mongo.shell.command.timeout</td><td>60000</td><td>Mongo command timeout</td></tr>
  <tr><td>mongo.server.host</td><td>localhost</td><td>Host of the MongDB server</td></tr>
  <tr><td>mongo.server.port</td><td>27017</td><td>Port of the MongDB server</td></tr>
  <tr><td>mongo.server.username</td><td></td><td>Username for authentication</td></tr>
  <tr><td>mongo.server.password</td><td></td><td>Password for authentication</td></tr>
</table>

