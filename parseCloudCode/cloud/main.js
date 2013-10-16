function timeDifference(current, previous) {

    var msPerMinute = 60 * 1000;
    var msPerHour = msPerMinute * 60;
    var msPerDay = msPerHour * 24;
    var msPerMonth = msPerDay * 30;
    var msPerYear = msPerDay * 365;

    var elapsed = current - previous;

    if (elapsed < msPerMinute) {
         return Math.round(elapsed/1000) + ' seconds ago';   
    }

    else if (elapsed < msPerHour) {
         return Math.round(elapsed/msPerMinute) + ' minutes ago';   
    }

    else if (elapsed < msPerDay ) {
         return Math.round(elapsed/msPerHour ) + ' hours ago';   
    }

    else if (elapsed < msPerMonth) {
        return 'approximately ' + Math.round(elapsed/msPerDay) + ' days ago';   
    }

    else if (elapsed < msPerYear) {
        return 'approximately ' + Math.round(elapsed/msPerMonth) + ' months ago';   
    }

    else {
        return 'approximately ' + Math.round(elapsed/msPerYear ) + ' years ago';   
    }
}

require('cloud/app.js');

Parse.Cloud.job("check_last_check_in_time", function(request, status) {
	Parse.Cloud.useMasterKey();
	var userquery = new Parse.Query("User");
	var checkinquery;
	userquery.equalTo("Type", "Caree");
	var timeout = 60;
	//var timeout = 60*60*24//in seconds - seconds, minutes, hours - 24 hours
	userquery.each(function(user) {
		//iterate through every user
		checkinquery = new Parse.Query("Check_In");
		var checkinID = user.get("lastCheckInId");
		//console.log(user);
		
		checkinquery.get(checkinID, {
			success: function(checkin_object) 
			{
				console.log(checkinID);
				console.log(checkin_object);
				var time_sent = new Date();
				time_sent = checkin_object.updatedAt;
				var diff = Math.abs(new Date() - time_sent);
				diff = diff/1000; //time in seconds since last check in
				console.log(diff)
				
				if(diff>timeout)
				{
					//aka, this person is over the check in timer. grab the list of active alarms to see if they're in there or not.
					Parse.Cloud.run('get_active_alarms', {}, {
					success: function(result2) {
						//console.log(result2);
						
						//i have no fucking idea why this is necessary but it doesn't work otherwise
						var result3 = ""			
						result3 = String(result2);
						var result = JSON.parse(result3);
						var found = -1;
						//console.log(result);
						
						for(var i=0; i<result.length; i++)
						{
							if(result[i].username == user.get("username"))
							{
								console.log(result[i].username);
								console.log(user.get("username"));
								//ie we already have an activated alarm
								found = i;
								console.log('activated alarm found already');
								break;
							}
						}
						
						if(found == -1)
						{
							//add new alarm object
							console.log('must add alarm!');
						    
							var firstName = user.get("firstName");
							var lastName = user.get("lastName");
							
							// Send Alarm to server
							var Stuff = Parse.Object.extend("Alarm")
							var obj = new Stuff();
							obj.set("Name", firstName + " " + lastName);
							obj.set("username", user.get("username"));
							obj.set("Activated", true);
							obj.save(null,
							{
								success: function(obj)
								{
									response.success("yayifications!");
									
									Parse.Push.send({
									channels: [user.get("username")],
									data: {
										alert: firstName + " " + lastName + " needs HELP bro!!!"
									}
							},{
								success: function() {
									// Push was successful
									console.log("push_successful!");
								},
							    error: function(error) {
									// Handle error
									console.error(error);
								}
							});
									
								},
								error:function (obj, error)
								{
									response.error("error!");
								}
							});
							console.log(obj);
								
							// Notify Carers on Alarm

							
						}
					},
					error: function(error){
					
					}
					});
				//calculate time elapsed since checkin
				}
			},	
			error: function(error)
			{
				status.error("ERROR");
			}
		});
		
		
	})
	
});
	
	

// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("get_user_from_alert", function(request, response){
	//given an alertID, will retrieve user information and stringify
	var alertID = request.params.alertID;
	var alarmquery = new Parse.Query("Alarm");
	var userquery = new Parse.Query("User");
	alarmquery.get(alertID, {
	success: function (alarm_object)	{
		//using this object, we get the username
		var a_username = alarm_object.get("username");
		//console.log(a_username);
		userquery.equalTo("username", a_username);
		userquery.find({
			success: function(user_object) {
				var json_objects = new Array();
				json_objects = [{"alarm": alarm_object}, {"user": user_object[0]}];
				//console.log(json_objects);
				response.success(JSON.stringify(json_objects));
		},
		error: function(error)
		{
		
		}
		})
		
	},	
	error: function(error)
	{
	
	}
	
	})
});

Parse.Cloud.define("get_check_in_list", function(request, response) {
	//THIS FUNCTION gets the table of check ins
	//it then finds when was the last check_in from each user
	var query = new Parse.Query("Check_In");
	var last_check_in = [];
	query.find({
		success: function(results) {
		
		for(var i = 0; i<results.length; i++)
		{
			var obj = {}
			var d = new Date();
			var time_string = time;
			var name = results[i].get("Name");
			var inlastcheckin = 0;
			d=results[i].updatedAt;			
			obj.time = d;
			obj.name = name;
			
			for(var j = 0; j< last_check_in.length; j++){
				//check through already active alarms for already existing entries
				if (last_check_in[j].name == name)
				{
					inlastcheckin = 1;
					if(d > last_check_in[j].time) //CONFIRM THAT DATE FORMATS ARE CORRECT PLEASE
					{
						//we found a newer check in the one already in the system!
						last_check_in.splice(j, 1, obj);
					
					}
				}
			}
			if(inlastcheckin == 0){
				//if our alarm isn't already in the array, add it to the list
					last_check_in.push(obj);
			}
		}
		response.success(JSON.stringify(last_check_in));
		
	},
	error: function(error) {
		reponse.error("something went very terribly wrong");
	}
	
})
});

Parse.Cloud.define("Logger", function(request, response) {
  console.log(request.params);
  response.success();
});

Parse.Cloud.define("dismiss_alarm", function(request, response) {
  //given an alarmID, will set its activated state to false.
	var alertID = request.params.alertID;
	var alarmquery = new Parse.Query("Alarm");
	alarmquery.get(alertID, {
	success: function (alarm_object)
	{
		alarm_object.set("Activated", false);
		alarm_object.save(null,
		{
			success: function()
			{
				response.success("yayifications!");
			},
			error:function (error)
			{
				response.error("error!");
			}
		});		
	},
	error: function (error)
	{
	
	}});
});

Parse.Cloud.define("get_active_alarms", function(request, response) {
	//THIS FUNCTION will return an object containing any activated alarms
	//ie. the last message was not a cancel
	var query = new Parse.Query("Alarm");
	var num = 0;
	var last_alarms=[ ];
	var active_alarms = [];
	query.limit(1000);
	query.descending("updatedAt"); //due to parse limits, pull 1000 most recent alarms
	query.find({
		success: function(results) {
	//arr.push to add new things to array
	//imo it would be far less expensive to maintain a separate table
			//response.success(JSON.stringify(results));
			for (var i = 0; i< results.length; i++){
				//we need to take the most recent alarm for a user, and check if it is still active or not.
				var obj = {}
				var d = new Date();
				var name = results[i].get("Name");
				var username = results[i].get("username");
				var inlastalarm = 0;
				d=results[i].updatedAt;			
				obj.time = d;
				obj.name = name;
				obj.username = username;
				obj.objectid = results[i].id;
				obj.activated = results[i].get("Activated");	
				
				for(var j = 0; j< last_alarms.length; j++){
					//check through already active alarms for already existing entries
					if (last_alarms[j].name == name)
					{
						inlastalarm = 1;
						if(d > last_alarms[j].time) //CONFIRM THAT DATE FORMATS ARE CORRECT PLEASE
						{
							//we found a newer alert than the one already in the system!
							last_alarms.splice(j, 1, obj);
							
						}
					}
				} 
				if(inlastalarm == 0){
					//if our alarm isn't already in the array, add it to the list
					last_alarms.push(obj);
				}
			}
			
			//sort last alarms back to ascending order
			last_alarms.sort(function(a,b){
			  a = new Date(a.time);
			  b = new Date(b.time);
			  return a<b?-1:a>b?1:0;
			});
			
			var date = new Date();
			date.toISOString(); //"2011-12-19T15:28:46.493Z"
			//now we must look through the last alarms to see if they are activated or not.
			for (var i = 0; i< last_alarms.length; i++)
			{
				last_alarms[i].time = timeDifference(date ,last_alarms[i].time)
				if (last_alarms[i].activated == false)
				{
					last_alarms.splice(i, 1); //delete the current value
					i--; //decrement i by one to make up for that fact we've deleted a value. i will be incremented in the next iteration.
				}
			}
			
			
			response.success(JSON.stringify(last_alarms));
			//response.success(JSON.stringify(active_alarms));
			//response.success(JSON.stringify(results));
	},
	error: function(error) {
	
		reponse.error("something went very terribly wrong");
	}
	
})
});