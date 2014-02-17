from django.conf.urls import patterns, include, url
from django.conf import settings
from django.contrib import admin
admin.autodiscover()

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'qanswr.views.home', name='home'),
    # url(r'^qanswr/', include('qanswr.foo.urls')),
    url(r'^$', 'website_qanswr.views.home', name='home'),
    url(r'^login-register/', 'website_qanswr.views.login_register_page', name='login-register'),
    url(r'^login/', 'website_qanswr.views.login', name='login'),
    url(r'^register/', 'website_qanswr.views.register', name='register'),
    url(r'^logout/', 'website_qanswr.views.logout', name='logout'),
    url(r'^create-question/', 'website_qanswr.views.create_question', name='create-question'),
    url(r'^submit-question/', 'website_qanswr.views.submit_question', name='submit-question'),
    url(ur'^question/(.*)', 'website_qanswr.views.question', name='question'),
    url(r'^submit-answer/', 'website_qanswr.views.submit_answer', name="submit-answer"),
    url(r'^submit-discussion/', 'website_qanswr.views.submit_discussion', name='submit-discussion'),
    url(r'^search/', 'website_qanswr.views.search', name='search'),
    url(r'^user/(.*)', 'website_qanswr.views.user', name='user'),
    url(r'^vote/', 'website_qanswr.views.vote', name='vote'),
    url(r'^sitemap/', 'website_qanswr.views.sitemap', name='sitemap'),
    
    # Uncomment the admin/doc line below to enable admin documentation:
    url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
)

urlpatterns += patterns('',
    (r'^static/(?P<path>.*)$', 'django.views.static.serve', {'document_root': settings.STATIC_ROOT}),
)
