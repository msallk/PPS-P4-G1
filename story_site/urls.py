"""URL configuration for story_site project."""
from __future__ import annotations

from django.contrib import admin
from django.urls import include, path

urlpatterns = [
    path("admin/", admin.site.urls),
    path("stories/", include("story_editor.urls")),
]
