from __future__ import annotations

from dataclasses import dataclass

from django.http import HttpRequest, HttpResponse
from django.shortcuts import render

from .forms import StoryForm


@dataclass
class Story:
    """Simple data structure representing a story instance."""

    title: str
    author: str
    status: str
    summary: str
    content: str
    tags: list[str]

    @property
    def formatted_tags(self) -> str:
        """Return tags as a comma separated string."""
        return ", ".join(self.tags)


def edit_story(request: HttpRequest) -> HttpResponse:
    """Render the edit story page with a populated form."""
    story = Story(
        title="The Enchanted Forest",
        author="Avery Quinn",
        status="in_review",
        summary="An adventurous tale following explorers who uncover the secrets of an enchanted forest.",
        content=(
            "Once upon a time, in a forest hidden from the maps of the world, a band of explorers found more than they expected. "
            "Guided by ancient runes and a talking fox, they discovered that the forest held the power to reshape reality."
        ),
        tags=["fantasy", "adventure", "mystery"],
    )

    form = StoryForm(
        initial={
            "title": story.title,
            "author": story.author,
            "status": story.status,
            "summary": story.summary,
            "content": story.content,
            "tags": story.formatted_tags,
        }
    )

    return render(
        request,
        "story_editor/edit_story.html",
        {
            "story": story,
            "form": form,
        },
    )
