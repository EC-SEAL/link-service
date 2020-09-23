# SEAL Linking Module

## Configuration

#### application.yml

Define the specific prooperties of your build:

* `spring.datasource`
  * `url`: database URL connection
  * `username`: database user
  * `password`: database user's password
  
* `linking.cm.url`: ConfigManager URL
  
* `linking.sm.url`: SessionManager URL

* `linking`
  * `keystore`
    * `path`: path to keystore file
    * `pass`: keysore password
    * `key.pass`: password from key to use in keystore
    * `httpsig.cert.alias`: alias from key to use in keysotre
    * `signing.secret`: token to verify signature
  * `request`
    * `expire`: time when a pending request will expire
    * `lloa`: request level of assurance
    * `validityDays`: days after an approve request is not valid
  * `resources`
    * `users.path`: local path to the list of officers file
    * `ms`
      * `path`: local path to the list of microservices file
      * `cache`: time when the microservices file will be requested to CM
  * auth.callback`: URL to callback after authentication
  
#### users-cm.json

List of officers than can operate the linking service. Each one requires next fields:

* `hashID`: MDS checksum from userID in auth set of attributes
* `nameAttr`: attribute when name will be found in auth set of attributes
* `surnameAttr`: attribute when name will be found in auth set of attributes
* `entitlements`: list of domains where the officer wil be able to operate
* `photoID` : officer's picture, in Base64 enconding, and specificating data type at the begining of the string (Ex: data:image/jpeg;base64, iVBORw0KGgoAA...)
* `email`: officer's mail contact

## Docker container

You will find a DockerFile and a docker-compose.yml to build the image and run the container. First of all, define MYSQL variables in the docker-compose files:

- MYSQL_ROOT_PASSWORD: password from root
- MYSQL_DATABASE: name of database, the same specified in application.yml
- MYSQL_USER: user of database, the same specified in application.yml
- MYSQL_PASSWORD: password of the user, the same specified in application.yml 

After that, define the volume when you will include your configuration files to be read by the conntainer, with the same structure (keystore and cm-users path). Also, put the application.yml file in this path because it is define to get read it from there.

- web-service.volumes

Also, configure the ports as you wish, in DockerFile and also in docker-compose.

Finally, just execute the next command in the source folder:

```
docker-compose up --build 
```

Notice that, in DockerFile, 'docker' profile is used to run the java application, so take it into account in application.yml file
