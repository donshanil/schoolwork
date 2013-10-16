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
				var inlastalarm = 0;
				d=results[i].updatedAt;			
				obj.time = d;
				obj.name = name;
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