# Eventacs
### Description
Trabajo Practico TACS - 2° Cuatrimestre 2018 - Tecnologías Avanzadas en la Construcción de Software

### Pasos con docker
Modificar el archivo hosts con el siguiente comando:
``sudo nano /etc/hosts``

Agregar al archivo hosts las siguientes tres líneas:

``172.10.0.8	backend``

``172.10.0.9 	oauth-server``

``172.10.0.10 	frontend``

(esto solo es necesario hacerlo una vez)

Hacer cd al directorio raíz de evectacs-tacs-2c-2018 y ejecutar el siguiente comando para levantar la app:
``sudo docker-compose up``

Una vez terminado se puede probar el bot de telegram mandandole mensajes a @TacsBot (ver sección de telegram de este documento).

También se puede probar el front accediendo desde un navegador a http://frontend:3000/ 

Actualmente funciona desde el frontend login, logout y creación de usuario (el password debe tener una letra mayuscula, una minuscula, un caracter especial, un número y al menos 8 caracteres).

Al finalizar las pruebas, ejecutar:
``sudo docker-compose down``

Si al probar una nueva versión de la app los cambios no se reflejan al levantar al app con docker, puede deberse a que docker esté usando imágenes viejas.

Por lo tanto para probar una nueva versión de la app, puede ser necesario correr primero el siguiente comando:
``sudo docker rmi $(sudo docker images)``

### Usuarios disponibles para las pruebas

Username: ``User1``, password: ``Pw1``, Rol: usuario

Username: ``usuario``, password: ``clave``, Rol: usuario

Username: ``usuario2``, password: ``clave``, Rol: usuario

Username: ``admin``, password: ``admin``, Rol: administrador

Username: ``admin1``, password: ``111``, Rol: administrador

### Telegram
El bot de telegram implementado es **@TacsBot**. El bot provee de un teclado con botones para usar los comandos sin necesidad de escribirlos. También según sea necesario se ofrece opciones si/no con el teclado. Para el caso en que sea necesario introducir un id como por ejemplo el id de una lista de eventos, se puede presionar sobre el id del mismo que haya sido recibido por mensaje para enviarlo como si fuera un comando ya que aparecen con una "/".

### Comandos disponibles Telegram

**/ayuda:** muestra los comandos disponibles

**/buscarevento:** busca eventos en eventbrite. Pide de forma separada los siguientes parámetros: keyword, Id de categoría (muestra las categorías disponibles y pregunta si se desea agregar otra categoría), fecha de inicio (pide por separado día, mes y año), y fecha de fin. Retorna una lista de eventos con listId y nombre.

**/crearalarma:** crea una alarma que son los parametros de una busqueda para ser realizada después. La búsqueda se realiza todos los días a las 11 am y en caso de encontrar eventos, el usuario recibirá los nuevos eventos encontrados por la alarma por telegram. Las alarmas que terminen teniendo una fecha de fin de evento menos a la fecha actual, serán eliminadas y no se ejecutarán. Pide los mismos parametros que /buscarevento y además un nombre para la alarma.

**/agregarevento:** agrega un evento a una lista de eventos ya existente del usuario. Los parámetros que pide son Id de la lista de eventos, y Id del evento a agregar (primero usar /buscarevento para saber el listId). 

**/revisareventos:** muestra los eventos de una lista de eventos del usuario. Solamente pide el listId de la lista de eventos a consultar. 

**/crearlista:** crea una lista de eventos a la cual se le pueden agregar eventos. Pide el nombre para la nueva lista.

**/eliminarlista:** elimina una lista de eventos. Pide el id de la lista a eliminar.

**/cambiarnombrelista:** cambia el nombre de una lista de eventos. Pide el id de la lista y el nuevo nombre.

**/login:** verifica a qué usuario de la app pertenece la cuenta de telegram. Sólo es necesario utilizarlo una vez. Pide Usuario y contraseña.

**/cancelar:** cancela el uso de un comando para volver al menú inicial. Por ejemplo si precionó por error /buscarevento, se puede salir sin tener que pasar por todo el proceso de pedido de parámetros.
 
### Backend API
### Routes
**Puerto:** 9000 **Basepath:** "/eventacs" 
Example:
`backend:9000/eventacs`

Para los métodos POST, se debe agregar el header ``Content-Type`` con el valor ``application/json``.

Para los métodos que requieran autenticación, agregar el header ``Authorization`` con el valor ``Bearer <token>``. El token se obtiene al hacer login. Ejemplo ``Bearer 7f04d156-6759-4268-9dc9-df29535b656a``. Para los servicios que requieran de permisos de administrador, se debe utilizar un token correspondiente a un administrador, sino de lo contrario se obtendrá acceso denegado.

El body debe ser escrito en formato json como se ve en los ejemplos a continuación.


#### Account Services
**Signup**
- Method: POST
- Rol: User
- URI: /signup
- Body Example:
```
 {
"fullName": "nombre completo", 
"email": "Correo Electronico", 
"password": "clave",
"username": "User2"
}
```

**Login**
- Method: POST
- Rol: User
- URI: /login
- Body Example:
```
 {	
"password": "clave",
"username": "usuario"
}
```

**Logout**
- Method: POST
- Rol: User
- URI: /logout

Utilizar el token del usuario que desea hacer logout

#### User Rol Services
**Get Events**
- Method: GET
- Rol: User
- URI: /events?keyword={someCriteria}

`Parameters:
  keyword Optional[String], 
  endDate Optional[LocalDate], 
  startDate Optional[LocalDate]
  categories Optional[List[String]]`
  
  Example: ``http://backend:9000/eventacs/events?keyword=hola&categories=113&startDate=2019-01-01&endDate=2019-01-15``
  
  **Add event to event list**
- Method: PUT
- Rol: User
- URI: /event-lists/:listId/:eventId
- Body: userId
```
  {
    "userId": "User1"
  }
```

**Create event list**
- Method: POST
- Rol: User
- URI: /event-lists
- Body Example:
```
  {
    "userId": "User1",
    "listName": "listNameExample"
  }
```

**Delete event list**
- Method: DELETE
- Rol: User
- URI: /event-lists/:listId

**Modify event list name**
- Method: PUT
- Rol: User
- URI: /event-lists/:listId
- Body Example:
```
  {
    "listName": "listNameExample"
  }
```

**Create Alarm**
- Method: POST
- Rol: User
- URI: /users/:userId/alarms
- Body Example:
```
  {
    "keyword": "keywordExample",
    "categories": ["105"],
    "startDate": "2018-12-12",
    "endDate": "2018-12-24",
    "alarmName": "name example",
    "changed": "2018-12-11"
  }
```

`Parameters:
  keyword Optional[String], 
  endDate Optional[LocalDate], 
  startDate Optional[LocalDate],
  categories Optional[List[String]],
  alarmName String, 
  changed Optional[LocalDate]`
  
  changed corresponde a la maxima fecha de modificacion de entre todos los eventos que fueron encontrados por la alarma. Sirve para identificar nuevos eventos al volver a ejecutar la búsqueda.

#### Admin Rol Services
**Get User info**
- Method: GET
- Rol: Administrator
- URI: /users/:userId

Response example: ``{"userName":"admin","listCount":0,"alarmsCount":0,"lastAccess":[2018,12,13,0,42,43]}``

**Get shared events between two lists**
- Method: GET
- Rol: Administrator
- URI: /event-lists/shared-events?listId=1&anotherListId=2

Response example: ``[{"id":"53398893528","name":"Polar Express Party","description":"Grab the kids, grab their favorite P.J's and come join the fun! The fun begins with pictures with Santa, making \"reindeer food,\" writing letters to Santa, eating snacks and more! Then we will settle down and watch the classic movie \"The Polar Express\" and relive the wonders of the season!","category":"116","logoUrl":"https://img.evbuc.com/https%3A%2F%2Fcdn.evbuc.com%2Fimages%2F53652103%2F7042342723%2F1%2Foriginal.jpg?h=200&w=450&auto=compress&rect=0%2C203%2C694%2C347&s=4cf847eae6112ca6b629bc61147d4649","registerDate":[2018,12,13,0,7,27],"start":[2018,12,15,15,0],"end":[2018,12,15,18,0],"changed":[2018,12,6,18,13,13]}]``

**Get Watchers**
- Method: GET
- Rol: Administrator
- URI: /events/:eventId/watchers

Response example: ``2``

**Count Events**
- Method: GET
- Rol: Administrator
- URI: /events/count?timelapse={someTimelapse}

`Timelapses = TODAY, FEW_DAYS, WEEK, MONTH, ALL`

Response example: ``8``


#### Mongo

En mongo guardamos 2 colecciones con el siguiente formato:

**EventLists

{
	"_id" : ObjectId("5bfb193b3f751cb4ca497a44"),
	"listId" : "NumberLong(1)",
	"listName" : "Lista figo",
	"userId" : "Figo",
	"events" : [{
		"name" : "alto event",
		"start" : "Sun Nov 25 18:50:54 ART 2018",
		"description" : "un re evento",
		"end" : "Sun Nov 25 18:50:54 ART 2018",
		"id" : "98765",
		"category" : "900",
		"logoUrl" : "http"
	          }]
}

**Alarms

{
	"_id" : ObjectId("5bfb193b3f751cb4ca497a44"),
	"alarmId" : "NumberLong(1)",
	"userId" : "Figo",
	"search" : {
		"keyword" : "buenos",
		"start" : "Sun Nov 25 18:50:54 ART 2018",
		"categories" : ["120"],
		"end" : "Sun Nov 25 18:50:54 ART 2018",
		"changed" : "Sun Nov 24 18:50:54 ART 2018",
		"alarmName": "nombre de alarma"
	}
}

Tenemos dos métodos de idGenerator que cada vez que se crea una entrada, nos devuelve un id único, el alarmId y el listId para manejarlo desde el modelo.



### Pasos viejos sin docker
### Creacion de certificados (se quitaron, ya no se usan)
La variable de entorno JAVA_HOME debe estar seteada
En el directorio resources del servidor de recursos borrar los archivos eventacskeystore.p12 y eventacsCertificate.cer, luego ejecutar los siguientes 3 comandos:

 ``keytool -genkeypair -alias eventacs -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore eventacskeystore.p12 -validity 3650``
 (contraseña eventacs, nombre y organización eventacs.com, los demás datos no es necesario completarlos)
 
 ``keytool -export -keystore eventacskeystore.p12 -alias eventacs -file eventacsCertificate.cer``
 (contraseña eventacs)
 
 ``sudo keytool -import -trustcacerts -file eventacsCertificate.cer -alias eventacs -keystore $(find $JAVA_HOME -name cacerts)``
(contraseña changeit o changeme)

Si el tercer comando ya había sido ejecutado anteriormente, es necesario primero ejecutar el siguiente comando:
``sudo keytool -delete -alias eventacs -keystore $(find $JAVA_HOME -name cacerts)``
(contraseña changeit o changeme)

 -archivo hosts (ubicado en linux en /etc/hosts) agregar la siguiente linea sin comentar
``127.0.0.1       eventacs.com    eventacs        localhost``

 -copiar los eventacskeystore.p12 y eventacskeystore.p12 generados a la carpeta resources del servidor de oauth

###Para los certificados del front
 Crear el directorio certificate (en el raiz de la app, del front). Pararse en ese directorio y ejecutar los siguientes comandos
 
 ``openssl genrsa -out server.key 2048``
 
 ``openssl rsa -in server.key -out server.key``
 
 ``openssl req -sha256 -new -key server.key -out server.csr -subj '/CN=eventacs.com'``
 
 ``openssl x509 -req -sha256 -days 365 -in server.csr -signkey server.key -out server.crt``
 
 ``cat server.crt server.key > server.pem``


### Mysql
Instalar Mysql y crear el usuario pds. Para ello loguearse en Mysql como root y ejecutar los siguientes comandos:

``CREATE USER 'pds'@'localhost' IDENTIFIED BY 'clave';``

``GRANT ALL PRIVILEGES ON * . * TO 'pds'@'localhost';``

### Install
Luego de instalar los certificados y crear el usuario de Mysql, realizar los siguientes pasos:
Primero levantar el servidor de Mongo y el de Redis.
Luego ubicarse en la carpeta oauth-authorization-server y ejecutar desde una consola el siguiente comando para levantar el servidor de autenticación:

$- ``mvn clean compile exec:java``

Finalmente ubicarse en la carpeta raíz (eventacs-tacs-2c-2018) y ejecutar desde una consola el siguiente comando para levantar el servidor de recursos (levanta al bot de telegram):

$- ``mvn clean compile exec:java``

