# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Stack

Static single-file HTML (inline CSS and JS, no build step), published as a claude.ai Artifact. Per-viewer study progress lives in `localStorage`. The earlier notebooks `sotay-bai03.html` and `sotay-bai04.html` follow the same stack.

## Users

One student, PTIT class CNPM1, studying the "Lập trình mobile" (Android) course. They are preparing the evening before a practical exam and again the next morning: mostly on a laptop (typing code, timed mock exam), sometimes on a phone (quick flashcard passes before the exam).

## Product Purpose

Make the student able to write Android XML layouts, drawables and Java event handling from memory, alone, inside 50 minutes, with no Internet or AI. Success means passing a blank-page mock of Test 1 (question 1: reproduce a screen from a picture in XML; question 2: a small form app with event handling and a calculation) without looking anything up.

## Positioning

Built around this exact exam format (`MP-TEST.docx`, Test 1) and the student's own course work (b01 to b04 slides and their Kotlin solutions in this repo), translated to the Java the exam uses. It drills recall of those specific patterns rather than teaching Android in general.

## Operating Context

- The exam room has no Internet and forbids AI, so the notebook is used only beforehand, as a trainer, never as a crib.
- The exam is written in Android Studio, Java, "Empty Views Activity" projects.
- A runnable Java reference project for the sample exam lives in `thuchanh/`, to compare against after a self-made attempt.
- The Android apps in `app/` (bai02 to bai04, Kotlin) are course exercises whose UI follows the slide wireframes and exam pictures; they are outside the design scope of this notebook.

## Capabilities and Constraints

- Active-recall drills: spaced-repetition flashcards, cloze code, Parsons line ordering, find-the-bug, write-from-memory with line comparison, mental calculation of the ticket total, a 50-minute timed mock with the exam's 5+5 rubric, and variant exam prompts.
- Short reference material: how to read the exam in 3 minutes, question 1 and question 2 walkthroughs, Java patterns from b01 to b04, XML and drawable essentials, common errors, Android Studio moves.
- Scope is Test 1 plus b01 to b04. Test 2 (SQLite, Room, SharedPreferences) and Test 3 are out of scope.
- Language: Vietnamese UI copy; code, API names and Android attributes stay in English exactly as written in Android Studio.

## Brand Commitments

The student explicitly rejected, for this notebook:
- long prose that must be scrolled through before reaching practice;
- decorative or many-colored styling that pulls focus;
- needing several clicks or nested menus to reach a drill;
- gamification: points, badges, confetti, streaks.

## Evidence on Hand

- `MP-TEST.docx`: sample exam text and the question 1 picture (`word/media/image1.png`).
- `slide/b01 … b04*.pdf`: course slides with required activities and behaviors.
- `app/src/main/java/ptit/cnpm1/tranxuanthanh/bai02 … bai04`: the student's Kotlin solutions, source for the Java patterns.
- `thuchanh/`: Java reference solution for the sample exam.
- No real exam results, grades or other students' data exist; none may be invented.

## Product Principles

1. Recall first, reading second: every screen opens on something to answer, with the explanation one tap away.
2. Every drill is reachable in one click from anywhere.
3. Reference text is short and scannable; nothing requires a long scroll.
4. Progress is shown as state (what is due, what was attempted), never as rewards.
5. Code shown is code that compiles and matches what the exam expects.
