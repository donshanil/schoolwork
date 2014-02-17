from django import template

register = template.Library()

@register.filter
def get_item_array(array, index):
    return array[index]

@register.filter
def get_item_dict(dictionary, key):
	return dictionary.get(key)