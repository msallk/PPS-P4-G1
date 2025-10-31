from __future__ import annotations

from django.test import SimpleTestCase
from django.urls import reverse


class EditStoryViewTests(SimpleTestCase):
    def test_edit_story_page_renders(self) -> None:
        response = self.client.get(reverse("story_editor:edit"))
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, "story_editor/edit_story.html")
