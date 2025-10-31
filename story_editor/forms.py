from __future__ import annotations

from typing import Iterable

from django import forms


STATUS_CHOICES: Iterable[tuple[str, str]] = (
    ("draft", "Draft"),
    ("in_review", "In Review"),
    ("published", "Published"),
)


class StoryForm(forms.Form):
    """Form used to edit a story instance."""

    title = forms.CharField(max_length=200, help_text="The display title for the story.")
    author = forms.CharField(max_length=100, help_text="Name of the primary author.")
    status = forms.ChoiceField(choices=STATUS_CHOICES)
    summary = forms.CharField(widget=forms.Textarea(attrs={"rows": 3}))
    content = forms.CharField(widget=forms.Textarea(attrs={"rows": 10}))
    tags = forms.CharField(
        required=False,
        help_text="Comma-separated keywords to help users find this story.",
    )

    def clean_tags(self) -> str:
        """Normalise the tag list into a comma separated string."""
        tags_raw = self.cleaned_data.get("tags", "")
        parts = [part.strip() for part in tags_raw.split(",") if part.strip()]
        return ", ".join(sorted(set(parts)))
