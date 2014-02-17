# -*- coding: utf-8 -*-
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'Tag'
        db.create_table(u'website_qanswr_tag', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('tag', self.gf('django.db.models.fields.CharField')(max_length=128)),
        ))
        db.send_create_signal(u'website_qanswr', ['Tag'])

        # Adding model 'TextContent'
        db.create_table(u'website_qanswr_textcontent', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('rank', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['auth.User'])),
            ('title', self.gf('django.db.models.fields.CharField')(max_length=128)),
            ('body', self.gf('django.db.models.fields.CharField')(max_length=1024)),
            ('isQuestion', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('isAnswer', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('isDiscussion', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('isCitation', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('questionURL', self.gf('django.db.models.fields.CharField')(max_length=512)),
            ('responseTo', self.gf('django.db.models.fields.related.ForeignKey')(related_name='response_to_text_content', null=True, to=orm['website_qanswr.TextContent'])),
            ('topAnswer', self.gf('django.db.models.fields.related.OneToOneField')(related_name='top_answer_to_question', unique=True, null=True, to=orm['website_qanswr.TextContent'])),
        ))
        db.send_create_signal(u'website_qanswr', ['TextContent'])

        # Adding model 'TextContentTag'
        db.create_table(u'website_qanswr_textcontenttag', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('tag', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['website_qanswr.Tag'])),
            ('textContent', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['website_qanswr.TextContent'])),
        ))
        db.send_create_signal(u'website_qanswr', ['TextContentTag'])

        # Adding model 'VotedOnBy'
        db.create_table(u'website_qanswr_votedonby', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['auth.User'])),
            ('textContent', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['website_qanswr.TextContent'])),
        ))
        db.send_create_signal(u'website_qanswr', ['VotedOnBy'])

        # Adding model 'Achievement'
        db.create_table(u'website_qanswr_achievement', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=128)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=1024)),
        ))
        db.send_create_signal(u'website_qanswr', ['Achievement'])

        # Adding model 'UserAchievement'
        db.create_table(u'website_qanswr_userachievement', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['auth.User'])),
            ('achievement', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['website_qanswr.Achievement'])),
        ))
        db.send_create_signal(u'website_qanswr', ['UserAchievement'])


    def backwards(self, orm):
        # Deleting model 'Tag'
        db.delete_table(u'website_qanswr_tag')

        # Deleting model 'TextContent'
        db.delete_table(u'website_qanswr_textcontent')

        # Deleting model 'TextContentTag'
        db.delete_table(u'website_qanswr_textcontenttag')

        # Deleting model 'VotedOnBy'
        db.delete_table(u'website_qanswr_votedonby')

        # Deleting model 'Achievement'
        db.delete_table(u'website_qanswr_achievement')

        # Deleting model 'UserAchievement'
        db.delete_table(u'website_qanswr_userachievement')


    models = {
        u'auth.group': {
            'Meta': {'object_name': 'Group'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '80'}),
            'permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'})
        },
        u'auth.permission': {
            'Meta': {'ordering': "(u'content_type__app_label', u'content_type__model', u'codename')", 'unique_together': "((u'content_type', u'codename'),)", 'object_name': 'Permission'},
            'codename': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'content_type': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['contenttypes.ContentType']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        u'auth.user': {
            'Meta': {'object_name': 'User'},
            'date_joined': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'email': ('django.db.models.fields.EmailField', [], {'max_length': '75', 'blank': 'True'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'groups': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Group']", 'symmetrical': 'False', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_active': ('django.db.models.fields.BooleanField', [], {'default': 'True'}),
            'is_staff': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_superuser': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'last_login': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'user_permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'}),
            'username': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '30'})
        },
        u'contenttypes.contenttype': {
            'Meta': {'ordering': "('name',)", 'unique_together': "(('app_label', 'model'),)", 'object_name': 'ContentType', 'db_table': "'django_content_type'"},
            'app_label': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'model': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        },
        u'website_qanswr.achievement': {
            'Meta': {'object_name': 'Achievement'},
            'description': ('django.db.models.fields.CharField', [], {'max_length': '1024'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'users': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.User']", 'through': u"orm['website_qanswr.UserAchievement']", 'symmetrical': 'False'})
        },
        u'website_qanswr.tag': {
            'Meta': {'object_name': 'Tag'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'tag': ('django.db.models.fields.CharField', [], {'max_length': '128'})
        },
        u'website_qanswr.textcontent': {
            'Meta': {'object_name': 'TextContent'},
            'body': ('django.db.models.fields.CharField', [], {'max_length': '1024'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'isAnswer': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'isCitation': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'isDiscussion': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'isQuestion': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'questionURL': ('django.db.models.fields.CharField', [], {'max_length': '512'}),
            'rank': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'responseTo': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'response_to_text_content'", 'null': 'True', 'to': u"orm['website_qanswr.TextContent']"}),
            'tags': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['website_qanswr.Tag']", 'null': 'True', 'through': u"orm['website_qanswr.TextContentTag']", 'symmetrical': 'False'}),
            'title': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'topAnswer': ('django.db.models.fields.related.OneToOneField', [], {'related_name': "'top_answer_to_question'", 'unique': 'True', 'null': 'True', 'to': u"orm['website_qanswr.TextContent']"}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['auth.User']"}),
            'votedOnByUsers': ('django.db.models.fields.related.ManyToManyField', [], {'symmetrical': 'False', 'related_name': "'voted_on_by'", 'null': 'True', 'through': u"orm['website_qanswr.VotedOnBy']", 'to': u"orm['auth.User']"})
        },
        u'website_qanswr.textcontenttag': {
            'Meta': {'object_name': 'TextContentTag'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'tag': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['website_qanswr.Tag']"}),
            'textContent': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['website_qanswr.TextContent']"})
        },
        u'website_qanswr.userachievement': {
            'Meta': {'object_name': 'UserAchievement'},
            'achievement': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['website_qanswr.Achievement']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['auth.User']"})
        },
        u'website_qanswr.votedonby': {
            'Meta': {'object_name': 'VotedOnBy'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'textContent': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['website_qanswr.TextContent']"}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['auth.User']"})
        }
    }

    complete_apps = ['website_qanswr']