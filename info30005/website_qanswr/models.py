from django.db import models
from django.contrib.auth.models import User

class Ranking(models.Model):
	rank = models.IntegerField(default=0)
	user = models.ForeignKey(User, editable=False) #The user that created the bit of content

	class Meta:
		abstract = True



# class UserProfile(models.Model):
# 	user = models.ForeignKey(User, unique=True)
# 	#Gravatar Image
# 	image = models.CharField(max_length=128)

# class User(Ranking):
# 	username = models.CharField(max_length=256)
# 	#PUT SECURE PASSWORD STUFF HERE
# 	emailAddress = models.EmailField(max_length=256)
# 	realName = models.CharField(max_length=256)
# 	image = ''.join(['http://www.gravatar.com/avatar/',hashlib.md5(emailAddress).hexdigest(),'.jpg']) #Gravatar image


########################
##Figure out how best give moderator access to users. Django's inbuilt User stuff seems fit to do that
########################


class Tag(models.Model):
	tag = models.CharField(max_length=128)

class TextContent(Ranking):
	title = models.CharField(max_length=128)
	body = models.CharField(max_length=1024)

	isQuestion = models.BooleanField(default=False)
	isAnswer = models.BooleanField(default=False)
	isDiscussion = models.BooleanField(default=False)
	isCitation = models.BooleanField(default=False)

	questionURL = models.CharField(max_length=512)

	responseTo = models.ForeignKey('self', related_name='response_to_text_content', null=True)
	topAnswer = models.OneToOneField('self', related_name='top_answer_to_question', null=True)
	
	tags = models.ManyToManyField(Tag, null=True, through='TextContentTag')
	votedOnByUsers = models.ManyToManyField(User, null=True, through='VotedOnBy', related_name='voted_on_by')	


class TextContentTag(models.Model):
	tag = models.ForeignKey(Tag)
	textContent = models.ForeignKey(TextContent)

class VotedOnBy(models.Model):
	user = models.ForeignKey(User)
	textContent = models.ForeignKey(TextContent)
	voteType = models.CharField(max_length=32, null=True)


'''
Probably best to just generate a bounty board through code
'''

# class BountyBoard(models.Model):
# 	user = models.OneToOneField(user)
# 	questions = models.ManyToManyField(TextContent)


class Achievement(models.Model):
	name = models.CharField(max_length=128)
	description = models.CharField(max_length=1024)

	users = models.ManyToManyField(User, through="UserAchievement")

class UserAchievement(models.Model):
	user = models.ForeignKey(User)
	achievement = models.ForeignKey(Achievement)


