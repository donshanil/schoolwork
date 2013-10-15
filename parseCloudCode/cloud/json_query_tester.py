'''import json,httplib
connection = httplib.HTTPSConnection('api.parse.com', 443)
connection.connect()
connection.request('GET', '/1/classes/Alarm/', '', {
       "X-Parse-Application-Id": "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa",
       "X-Parse-REST-API-Key": "jSnTsnBTx89Bz68dJlRy8EAFRYwpdaeNgigjGZZQ"
     })
result = json.loads(connection.getresponse().read())
print result '''


import json,httplib
connection = httplib.HTTPSConnection('api.parse.com', 443)
connection.connect()
connection.request('POST', '/1/functions/get_active_alarms', json.dumps({
     }), {
       "X-Parse-Application-Id": "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa",
       "X-Parse-REST-API-Key": "jSnTsnBTx89Bz68dJlRy8EAFRYwpdaeNgigjGZZQ",
       "Content-Type": "application/json"
     })
result = json.loads(connection.getresponse().read())
print result

# import json,httplib
# connection = httplib.HTTPSConnection('api.parse.com', 443)
# connection.connect()
# connection.request('POST', '/1/functions/get_check_in_list', json.dumps({
#      }), {
#        "X-Parse-Application-Id": "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa",
#        "X-Parse-REST-API-Key": "jSnTsnBTx89Bz68dJlRy8EAFRYwpdaeNgigjGZZQ",
#        "Content-Type": "application/json"
#      })
# result = json.loads(connection.getresponse().read())
# print result