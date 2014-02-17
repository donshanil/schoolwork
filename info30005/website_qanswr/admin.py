from django.contrib import admin 
from website_qanswr.models import TextContent, Tag, TextContentTag, Achievement, VotedOnBy, UserAchievement

admin.site.register(TextContent)
admin.site.register(Tag)
admin.site.register(TextContentTag)
admin.site.register(Achievement)
admin.site.register(VotedOnBy)
admin.site.register(UserAchievement)