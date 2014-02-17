# Create your views here.
from django.http import HttpResponse, HttpResponseRedirect
from django.template import Template, Context, RequestContext
from django.shortcuts import render, get_object_or_404, render_to_response
from django.contrib import auth
from django.contrib.auth.models import User
from website_qanswr.models import TextContent, Tag, TextContentTag, VotedOnBy
from django.core.validators import validate_email
from django.core.exceptions import ValidationError
from django.utils.html import escape
from django.utils import simplejson
from django.conf import settings
from recaptcha.client.captcha import displayhtml, submit
import sys





def home(request):
	textContent = TextContent.objects.filter(isQuestion=True).order_by('-rank')
	return render(request, 'website_qanswr/home.html', {'questions': textContent})

def login_register_page(request):
	public_key = settings.RECAPTCHA_PUBLIC_KEY
	script = displayhtml(public_key=public_key)
	return render_to_response('website_qanswr/login-register.html', 
		{'script':script}, 
		context_instance=RequestContext(request))

def check_captcha(request):
	remote_ip = request.META['REMOTE_ADDR']
	challenge = request.POST['recaptcha_challenge_field']
	response = request.POST['recaptcha_response_field']
	private_key = settings.RECAPTCHA_PRIVATE_KEY
	return submit(challenge, response, private_key, remote_ip)

def register(request):
	username = request.POST.get('username', '')
	password = request.POST.get('password', '')
	email = request.POST.get('email', '')
	result = check_captcha(request)
	public_key = settings.RECAPTCHA_PUBLIC_KEY
	script = displayhtml(public_key=public_key)

	if result.is_valid:
	#Gravatar image
	#image = ''.join(['http://www.gravatar.com/avatar/',hashlib.md5(email).hexdigest(),'.jpg'])
		if User.objects.filter(username = username).count() == 0:
			try:
				validate_email(email)
				user = User.objects.create_user(username=username, email=email, password=password)
				user.save()
				user = auth.authenticate(username=username, password=password)
				if user is not None and user.is_active:
					auth.login(request,user)
					return HttpResponseRedirect("/")
				else:
					return render_to_response('website_qanswr/login-register.html', 
					{'script':script,"loginError":True}, 
					context_instance=RequestContext(request))
			except ValidationError:
				return render_to_response('website_qanswr/login-register.html', 
				{'script':script,"invalidEmail":True}, 
				context_instance=RequestContext(request))
		else:
			return render_to_response('website_qanswr/login-register.html', 
			{'script':script,"nameTaken":True}, 
			context_instance=RequestContext(request))
	else:
		return render_to_response('website_qanswr/login-register.html', 
		{'script':script,"captchaFail":True}, 
		context_instance=RequestContext(request))

def login(request):	
	#ADD CAPTCHA IF THE USER FAILS TOO MANY TIMES
	username = request.POST.get('l_username', '')
	password = request.POST.get('l_password', '')
	user = auth.authenticate(username=username, password=password)
	if user is not None and user.is_active:
		auth.login(request, user)
		#Make this redirect the user to the page they were previously on
		return HttpResponseRedirect('/')
	else:
		#Make this update the current page to say the login was incorrect
		#Make this also count how many failed attempts, and if too many, hit them with a captcha
		return render(request, "website_qanswr/login-register.html", {"incorrectLogin":True})

def logout(request):
	auth.logout(request)
	return HttpResponseRedirect('/')

def create_question(request):
	return render(request, 'website_qanswr/create-question.html')

def submit_question(request):
	title = request.POST.get("title", "")
	title = title.strip()
	question_text = request.POST.get("question", "")
	question_text = question_text.strip()
	questionURL = ''.join(e for e in title if e.isalnum())
	tags = request.POST.get("tags", "")
	tags = [x.strip() for x in tags.split(',')]
	#Associate user and set the ranking
	#Check if title already exists, if so, let the user know they need a unique title
	
	if request.user.is_authenticated():
		if questionURL != "" and TextContent.objects.filter(isQuestion = True, questionURL=questionURL).count() == 0:
			question = TextContent(title=title, body=question_text, isQuestion=True, user=request.user, rank=0, questionURL=questionURL)
			question.save()
			for tag in tags:
				tag_db, tag_created = Tag.objects.get_or_create(tag=tag)
				textContentTag, textContentTag_created = TextContentTag.objects.get_or_create(tag=tag_db, textContent=question)

			return HttpResponseRedirect('/question/' + questionURL)
				
		else:
			#Update page to notify the user the title must be unique
			return render(request,'website_qanswr/question/' + questionURL, {"titleNotUnique":True})
	else:
		#User is not authenticated
		return render(request,'website_qanswr/question/' + questionURL, {"notLoggedIn":True})

def question(request, questionURL):

		questionToRender = get_object_or_404(TextContent, questionURL=questionURL)
		answers_sorted = TextContent.objects.filter(isAnswer=True,responseTo=questionToRender).order_by('-rank')
		#answersArray is of the form [[answer, [citation1, citation2, ...], [discussion1, discussion2, ...]], ...]	
		answerArray = []
		for answer in answers_sorted:
			citationArray = []
			discussionArray = []
			for citation in TextContent.objects.filter(isCitation=True, responseTo=answer).order_by('-rank'):
				citationArray.append(citation)
			for discussion in TextContent.objects.filter(isDiscussion=True, responseTo=answer).order_by('-rank'):
				discussionArray.append(discussion)

			answerArray.append([answer, citationArray, discussionArray])

		request.session['question'] = questionToRender
		request.session['questionURL'] = questionURL

		# Make hasVotedOn a dictionary, with pk of a textContent as it's key, and upvote/downvote as its value
		
		hasVotedOn = {}
		if request.user.is_authenticated():
			votedOn = VotedOnBy.objects.filter(user = request.user)
			for item in votedOn:
				hasVotedOn[item.textContent.pk] = item.voteType


		return render(request, 'website_qanswr/question.html', {'question': questionToRender, 'answers':answerArray, 'hasVotedOn':hasVotedOn})
		#Render the page that allows you to answer, discuss and so on.



def submit_answer(request):
	answer_text = request.POST.get("answer", "")
	answer_text = answer_text.strip()
	citation_text = request.POST.get("citation", "")
	citation_text = citation_text.strip()

	if request.user.is_authenticated():
		if answer_text != "" and TextContent.objects.filter(isAnswer = True, body=answer_text).count() == 0:
			answer = TextContent(body=answer_text, isAnswer=True, user=request.user, rank=0, responseTo=request.session.get('question'))
			answer.save()
			if citation_text != "" and TextContent.objects.filter(isCitation = True, body=citation_text).count() == 0:
				citation = TextContent(body=citation_text, isCitation=True, user=request.user, rank=0, responseTo=answer)
				citation.save()


	return HttpResponseRedirect('/question/' + request.session.get('questionURL'))

def submit_discussion(request):
	discussion_text = request.POST.get("discussion", "")
	discussion_text = discussion_text.strip()
	if discussion_text != "" and TextContent.objects.filter(isDiscussion = True, body=discussion_text).count() == 0:
		discussion = TextContent( body=discussion_text, isDiscussion = True, user=request.user, rank=0, responseTo=TextContent.objects.get( body = str( request.POST.get("responseTo","") ) ) )
		discussion.save()

	return HttpResponseRedirect('/question/' + request.session.get('questionURL')) 

def search(request):
	return HttpResponseRedirect('http://www.google.com/cse/publicurl?cx=009950086738392433385:mwowl3yfys4')

def user(request, username):
	userInfo = get_object_or_404(User, username=username)
	activity = TextContent.objects.filter(user=userInfo)
	return render(request, 'website_qanswr/user.html', {'activity': activity, 'userInfo':userInfo})

def vote(request):
	results = {'success':False}
	if request.method == u'GET':
		GET = request.GET
		if GET.has_key(u'pk') and GET.has_key(u'vote') and GET.has_key(u'user_pk'):
			pk = int(GET[u'pk'])
			vote = GET[u'vote']
			user_pk = int(GET[u'user_pk'])
			textContent = TextContent.objects.get(pk=pk)
			user = User.objects.get(pk=user_pk)
			if VotedOnBy.objects.filter(user = user, textContent = textContent).count() == 0:
				votedOnByCurrent, votedOnByCurrent_created = VotedOnBy.objects.get_or_create(user=user, textContent=textContent)
				if vote == u'up':
					textContent.rank += 1
					textContent.save()
					votedOnByCurrent.voteType='up'
					votedOnByCurrent.save()
				elif vote == u'down':
					textContent.rank -= 1
					textContent.save()
					votedOnByCurrent.voteType='down'
					votedOnByCurrent.save()
				results = {'success':True}
		json = simplejson.dumps(results)
		return HttpResponse(json, mimetype='application/json')

def sitemap(request):
	users = User.objects.filter()
	questions = TextContent.objects.filter(isQuestion=True)

	return render(request, 'website_qanswr/sitemap.html', {"users": users, "questions":questions})


