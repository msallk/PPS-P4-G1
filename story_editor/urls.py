from __future__ import annotations

from django.urls import path

from . import views

app_name = "story_editor"

urlpatterns = [
    path("edit/", views.edit_story, name="edit"),
]
