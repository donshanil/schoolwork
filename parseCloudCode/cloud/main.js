
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("getPersonAlarms", function(request, response) {

});

Parse.Cloud.define("get_active_alarms", function(request, response) {
	//returns an array of still activated alarms.
	var query = new Parse.Query("Alarm");
	var num = 0;
	var last_alarms=[ ];
	var active_alarms = [];
	query.find({
		success: function(results) {
	//arr.push to add new things to array
	//imo it would be far less expensive to maintain a separate table
			for (var i = 0; i< results.length; i++){
				//we need to take the most recent alarm for a user, and check if it is still active or not.
				var obj = {}
				var d = new Date();
				var name = results[i].get("Name");
				var inlastalarm = 0;
				d=results[i].updatedAt;
				
				obj.time = d;
				obj.name = name;
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
					last_alarms.push(obj);
				}
							
				
				//var time = d.toJSON();
				
				//obj.shindiggles = num;
			}
			
			//now we must look through the last alarms to see if they are activated or not.
			for (var i = 0; i< last_alarms.length; i++)
			{
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