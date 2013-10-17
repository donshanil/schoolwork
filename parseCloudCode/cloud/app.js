
//		<%=# link_to(result[i].objectid, 'system/alert/'+result[i].objectid) %> </td>
// These two lines are required to initialize Express in Cloud Code.
var express = require('express');
var parseExpressHttpsRedirect = require('parse-express-https-redirect');
var parseExpressCookieSession = require('parse-express-cookie-session');
//var helpers = require('ejs_production.js')
//helpers(app);
var app = express();
//var helpers = require('express-helpers')(app);

// Global app configuration section
app.set('views', 'cloud/views');  // Specify the folder to find templates
app.set('view engine', 'ejs');    // Set the template engine
//app.use("/css", express.static(__dirname + "/css"));
app.use(parseExpressHttpsRedirect()); //force https
app.use(express.bodyParser());    // Middleware for reading request body
app.use(express.cookieParser('YOUR_SIGNING_SECRET'));
app.use(parseExpressCookieSession({ cookie: { maxAge: 3600000 } }));

// This is an example of hooking up a request handler with a specific request
// path and HTTP verb using the Express routing API.
app.get('/hello', function(req, res) {
  res.render('hello', { message: 'Congrats, you just set up your app!' });
});

// // Example reading from the request query string of an HTTP get request.
// app.get('/test', function(req, res) {
//   // GET http://example.parseapp.com/test?message=hello
//   res.send(req.query.message);
// });

// // Example reading from the request body of an HTTP post request.
// app.post('/test', function(req, res) {
//   // POST http://example.parseapp.com/test (with request body "message=hello")
//   res.send(req.body.message);
// });

 app.get('/login', function(req, res) {
    // Renders the login form asking for username and password.
    res.render('login.ejs');
  });


 // Clicking submit on the login form triggers this.
  app.post('/login', function(req, res) {
    Parse.User.logIn(req.body.username, req.body.password).then(function() {
      //check to see if user is operator.
	  
	  // Login succeeded, redirect to homepage.
      // parseExpressCookieSession will automatically set cookie.
      res.redirect('/system');
    },
    function(error) {
      // Login failed, redirect back to login form.
      res.redirect('/login');
    });
  });

  // You could have a "Log Out" link on your website pointing to this.
  app.get('/logout', function(req, res) {
    Parse.User.logOut();
    res.redirect('/');
  });

  app.get('/', function(req, res) {
    if (Parse.User.current()) {
      // user is logged in.
		res.redirect('/system');

    } else {
      // Render a public welcome page, with a link to the '/login' endpoint.
	 
	  res.render('hello.ejs', { message: 'you must login to access more features' });
    }
  });
  
 app.get('/system', function(req, res) {
      // No need to fetch the current user for querying Note objects.
	if (Parse.User.current()) {
	Parse.Cloud.run('get_active_alarms', {}, {
	 success: function(result2) {
        // Render the notes that the current user is allowed to see.
		//result2 is an array of json objects.
		var result = "";
		var stringed = "";
		//console.log(result2)
		//for (var i=0; i<result2.length; i++)
		//{
		//	console.log(result2.name);
		//}
		
		//we want to convert the timestamps to 'relative' times.
		result = String(result2);
		//res.render('system.ejs', {result: String(result2),});
		res.render('system.ejs', {message: result});
		//res.render('hello.ejs', {message: result});
		//res.render('hello.ejs', {message: result});
      },
     error: function(error) {
        // Render error page.
		res.render('hello.ejs', {message: 'ERROR'}) ;
		
	}	
      });
	} else {
		res.redirect('/');
	}	

  });
  
  app.get('/system/alert/:alarmID', function(req, res){
	Parse.Cloud.run('get_user_from_alert', {alertID: req.params.alarmID}, {
	success:function(result2) {
		//console.log(req.params.alarmID);
		//console.log(result);
		//we've got an alarmID, let's bring up the user info along with it
		var thisurl = "/system/alert/"+req.params.alarmID;
		var result = "";
		result = String(result2);
		res.render('alert.ejs', {message: result2, url:thisurl});
	},
	
	error: function(error){
	
	}
  }	);
 }); 
 
  app.get('/system/alert/:alarmID/dismiss', function(req, res){
	Parse.Cloud.run('dismiss_alarm', {alertID: req.params.alarmID}, {
	success:function(result2) {
		//console.log(req.params.alarmID);
		//console.log(result);
		//we've got an alarmID, let's bring up the user info along with it
		res.redirect('/system');
	},
	
	error: function(error){
	
	}
  }	);
 }); 
 


  // You could have a "Profile" link on your website pointing to this.
  app.get('/profile', function(req, res) {
    // Display the user profile if user is logged in.
    if (Parse.User.current()) {
      // We need to fetch because we need to show fields on the user object.
      Parse.User.current().fetch().then(function(user) {
        // Render the user profile information (e.g. email, phone, etc).
      },
      function(error) {
        // Render error page.
      });
    } else {
      // User not logged in, redirect to login form.
      res.redirect('/login');
    }
  });
  
  function errorHandler(err, req, res, next) {
  res.status(500);
  res.render('error', { error: err });
}

// Attach the Express app to Cloud Code.
app.listen();

