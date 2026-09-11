---
name: Ga Thực Hành
description: Night-station recall trainer for Android Test 1. Drills print on thermal tickets under a dot-matrix departure board.
colors:
  led: "#FFB21A"
  led-dim: "#B08A45"
  led-glow: "rgba(255, 178, 26, 0.32)"
  bad: "#A3322A"
  ground: "#0E1012"
  board: "#121417"
  board-row: "#1A1D21"
  board-line: "#262A30"
  paper: "#E2E5E7"
  paper-2: "#D5D9DC"
  paper-3: "#BCC2C7"
  ink: "#1B1D1F"
  ink-2: "#454A4F"
  ink-3: "#7C8287"
  ink-hover: "#33373B"
  ink-wash: "rgba(27, 29, 31, 0.08)"
  marker: "rgba(255, 178, 26, 0.16)"
  marker-strong: "rgba(255, 178, 26, 0.38)"
  bad-wash: "rgba(163, 50, 42, 0.08)"
  pane: "rgba(255, 255, 255, 0.35)"
typography:
  display:
    fontFamily: "Doto, JetBrains Mono, ui-monospace, monospace"
    fontSize: "40px"
    fontWeight: 900
    lineHeight: 0.9
    letterSpacing: "0.02em"
    fontFeature: "tnum"
  led-key:
    fontFamily: "Doto, JetBrains Mono, ui-monospace, monospace"
    fontSize: "20px"
    fontWeight: 900
    lineHeight: 1
  led-count:
    fontFamily: "Doto, JetBrains Mono, ui-monospace, monospace"
    fontSize: "17px"
    fontWeight: 900
    lineHeight: 1.05
    fontFeature: "tnum"
  headline:
    fontFamily: "Be Vietnam Pro, system-ui, -apple-system, Segoe UI, Roboto, sans-serif"
    fontSize: "24px"
    fontWeight: 700
    lineHeight: 1.2
  prompt:
    fontFamily: "Be Vietnam Pro, system-ui, -apple-system, Segoe UI, Roboto, sans-serif"
    fontSize: "16.5px"
    fontWeight: 500
    lineHeight: 1.5
  title:
    fontFamily: "Be Vietnam Pro, system-ui, -apple-system, Segoe UI, Roboto, sans-serif"
    fontSize: "15px"
    fontWeight: 600
    lineHeight: 1.3
  body:
    fontFamily: "Be Vietnam Pro, system-ui, -apple-system, Segoe UI, Roboto, sans-serif"
    fontSize: "15px"
    fontWeight: 400
    lineHeight: 1.55
  button:
    fontFamily: "Be Vietnam Pro, system-ui, -apple-system, Segoe UI, Roboto, sans-serif"
    fontSize: "14px"
    fontWeight: 600
    lineHeight: 1.3
  label:
    fontFamily: "Be Vietnam Pro, system-ui, -apple-system, Segoe UI, Roboto, sans-serif"
    fontSize: "10.5px"
    fontWeight: 600
    letterSpacing: "0.1em"
  code:
    fontFamily: "JetBrains Mono, ui-monospace, SF Mono, Menlo, Consolas, monospace"
    fontSize: "13px"
    fontWeight: 400
    lineHeight: 1.6
rounded:
  strip: "3px"
  control: "4px"
  ticket: "6px"
  pill: "999px"
spacing:
  hair: "6px"
  tight: "8px"
  snug: "12px"
  block: "14px"
  stack: "16px"
  ticket-inset-compact: "18px"
  board-inset: "20px"
  ticket-inset: "28px"
  platform: "56px"
components:
  button:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    typography: "{typography.button}"
    rounded: "{rounded.control}"
    padding: "7px 14px"
  button-hover:
    backgroundColor: "{colors.ink-wash}"
  button-solid:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.paper}"
    typography: "{typography.button}"
    rounded: "{rounded.control}"
    padding: "7px 14px"
  button-solid-hover:
    backgroundColor: "{colors.ink-hover}"
  button-ground:
    backgroundColor: "transparent"
    textColor: "{colors.led}"
    typography: "{typography.button}"
    rounded: "{rounded.control}"
    padding: "7px 14px"
  button-ground-hover:
    backgroundColor: "{colors.board-row}"
  segment-on:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.paper}"
    padding: "6px 12px"
  tear-handle:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.pill}"
    padding: "6px 16px"
  tear-handle-hover:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.paper}"
  board-row:
    backgroundColor: "{colors.board}"
    textColor: "{colors.led}"
    typography: "{typography.title}"
    padding: "8px 20px"
  board-row-hover:
    backgroundColor: "{colors.board-row}"
  board-row-current:
    backgroundColor: "{colors.led}"
    textColor: "{colors.board}"
  ticket-body:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.ticket}"
    padding: "20px 28px 26px"
  ticket-stub:
    backgroundColor: "{colors.paper-2}"
    textColor: "{colors.ink}"
    rounded: "{rounded.ticket}"
    padding: "18px 28px 24px"
  code-block:
    backgroundColor: "{colors.pane}"
    textColor: "{colors.ink}"
    typography: "{typography.code}"
    rounded: "{rounded.control}"
    padding: "14px 16px"
  code-strip:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.strip}"
    padding: "5px 10px"
  blank:
    backgroundColor: "{colors.marker}"
    textColor: "{colors.ink}"
    typography: "{typography.code}"
    padding: "0 3px"
  blank-focus:
    backgroundColor: "{colors.marker-strong}"
  blank-bad:
    backgroundColor: "{colors.bad-wash}"
    textColor: "{colors.bad}"
---

# Design System: Ga Thực Hành

Scope: the study-notebook web surface (`sotay-thuchanh.html`, single file, no build step). The Android apps in `app/` and `thuchanh/` follow the course slide wireframes and exam pictures and sit outside this system.

## Overview

**Creative North Star: "Cuống vé" (the ticket stub)**

A night station, seen by a student the evening before a practical exam. The ground is near-black. A departure board runs down the left edge in amber dot-matrix LED, showing each drill's key, the live clock and the due counts. The work itself is printed on a cool grey thermal-paper ticket in black ink, with red as the second thermal colour. Every drill is a ticket: the prompt sits on the body, and the answer waits under a round-hole perforation in a tear-off stub until the student commits and tears. The tear is the system's one signature interaction and behaves the same in every drill.

The density is operational: board rows are short and closely stacked, the ticket runs at working size (up to 880px wide), and reference material stays folded until opened. The world has two materials and never mixes their jobs. The board is light emitted on dark: amber, glowing, numeric. The ticket is ink on paper: flat, printed, verbatim. Colour is restrained because the product demands it: one lamp colour, two inks, no rewards.

It has one theme on purpose. The dark ground is the station at night, and paper only ever appears as an object lying on that ground.

**Key Characteristics:**
- Two materials: emitted amber LED on a near-black board, and black/red thermal ink on cool grey paper.
- The perforated stub holds every answer, and tearing it is the only way to reveal one.
- Doto dot-matrix for digits and keys, Be Vietnam Pro for every Vietnamese word, JetBrains Mono for all code.
- Flat everywhere except the ticket, which is the only object that casts a shadow.
- Progress is shown as board state (counts, due, remaining), never as rewards.

## Colors

One amber lamp on a night board, and two thermal inks on grey paper.

### Primary
- **Departure Amber** (`led`): the board's lamp colour. Row keys, row names, the clock, counts, the route list, ticket-to-ticket buttons on the ground, and the fill of the current board row. The only accent in the system.
- **Dimmed Lamp** (`led-dim`): secondary board text such as row subtitles, units, column heads, the footer and idle counts. It reads as the same lamp at lower brightness, never as a separate hue.
- **Lamp Halo** (`led-glow`): the soft text glow behind LED digits, and the fill of a picked line in the find-the-bug drill.

### Secondary
- **Thermal Red** (`bad`): the second ink on the paper, used only for wrong answers: a wrong blank, a misplaced strip, a mis-picked line, a missing diff line, a failed result line. **Red Wash** (`bad-wash`) is the faint red ground under those same wrong marks.

### Neutral
- **Station Night** (`ground`): the page itself, and the colour punched through the perforation holes and side notches.
- **Board Black** (`board`): the departure board panel; also the text colour on the amber current row.
- **Board Row Lit** (`board-row`): hover on board rows and on ground buttons.
- **Board Rule** (`board-line`): 1px rules between rows, the board edge, and the route links' underline.
- **Thermal Grey** (`paper`): the ticket body, the tear handle, and the Parsons strips.
- **Stub Grey** (`paper-2`): the stub below the perforation, a step darker so the stub reads as a separate piece.
- **Paper Rule** (`paper-3`): 1px rules and pane outlines on paper; dashed dividers inside tables, fare cells and the reference sheet.
- **Thermal Black** (`ink`): all printed text, control outlines (1.5px), the solid button and the pressed segment.
- **Faded Ink** (`ink-2`): hints, field labels, line numbers, code comments, secondary table text.
- **Ghost Ink** (`ink-3`): dashed outlines of the sealed stub, the Parsons lanes and the recall writer. Used for lines only, never for text.
- **Ink Hover** (`ink-hover`): hover on the solid button.
- **Ink Wash** (`ink-wash`): hover on outline buttons, and the ground under inline code.
- **Paper Pane** (`pane`): the lighter wash under code blocks, bug-line lists and diff lists, so code reads as printed on a slightly brighter panel of the same paper. The writer and the fare input use a slightly brighter pane, and code inside the stub a slightly dimmer one.
- **Marker** (`marker`, `marker-strong`): translucent amber under a blank you can type into, stronger while it has focus.

### Named Rules

**The One Lamp Rule.** Amber is the only accent, and it belongs to the board and the ground. On paper, amber appears only as a translucent marker under the thing being typed into or picked. It is never text, never an outline, never a solid fill on the ticket.

**The Two Inks Rule.** The ticket prints in black and red only. Correct is black ink made heavier: bold, a 3px double underline, an inset ink ring. Wrong is thermal red, dashed, over the red wash. There is no green, no success colour and no third ink.

**The Night Board Rule.** One theme. There is no light mode and no theme toggle. Paper is the only light surface, and it is always a ticket lying on the dark ground.

**The Ring Follows the Material Rule.** Every focus indicator is a 2px ring. It is amber on the dark board and ground, and ink on the paper. Anything inside the ticket body, the perforation or the stub takes the ink ring, including the writer and the fare input. The ticket footer's buttons sit on the dark ground, so they keep the amber ring.

## Typography

**Display Font:** Doto (with JetBrains Mono, ui-monospace)
**Body Font:** Be Vietnam Pro (with system-ui, -apple-system, Segoe UI, Roboto)
**Label/Mono Font:** JetBrains Mono (with ui-monospace, SF Mono, Menlo, Consolas)

**Character:** A dot-matrix LED face gives the board its numbers. A clear Vietnamese grotesque handles every word. A coding mono prints code exactly as Android Studio shows it.

### Hierarchy
- **Display** (Doto 900, 40px, line-height 0.9, 0.02em, tabular): the board clock. It turns into the countdown during the mock exam. The on-ticket mock timer uses the same face one step larger. The ticket number and the fare-answer input use the same face at 26 to 30px.
- **LED Key / LED Count** (Doto 900, 20px / 17px): the drill key digit on each board row, and the row's state count (`0/10`, `45`).
- **Headline** (Be Vietnam Pro 700, 24px, line-height 1.2; 20px under 880px): the ticket title. Balanced wrapping.
- **Prompt** (Be Vietnam Pro 500, 16.5px, line-height 1.5, max 62ch): the question printed on the ticket body. A flashcard question runs larger (20px, 600, max 40ch).
- **Title** (Be Vietnam Pro 600, 15px, line-height 1.3): board row names, reference-sheet summaries, board name at 17px 700.
- **Body** (Be Vietnam Pro 400, 15px, line-height 1.55, max 68 to 70ch): stub explanations and reference text. Hints drop to 13px in faded ink.
- **Button** (Be Vietnam Pro 600, 14px): every button and the tear handle (13.5px).
- **Label** (Be Vietnam Pro 600, 10.5 to 11px, 0.1 to 0.12em, uppercase): ticket field labels, board column heads, table heads, rubric group heads, the clock caption.
- **Code** (JetBrains Mono 400, 13px, line-height 1.6, tab-size 4): code blocks, recall writer, bug lines, diff (12.5px), Parsons strips (12.5px), inline code at 0.9em.

### Named Rules

**The LED Digits Rule.** Doto sets digits and short ASCII tokens only: keys, clock, counts, `Nº`, fares, ratings. Doto has no Vietnamese subset, so a Vietnamese word is never set in it. Board row names and units stay in Be Vietnam Pro even though they sit on the LED board.

**The Verbatim Code Rule.** Code, API names and Android attributes are set in JetBrains Mono exactly as written in Android Studio. They are never translated and never set in the body face. Keywords are bold, comments are italic faded ink, and that is the whole syntax treatment.

**The Field Label Rule.** An uppercase tracked micro-label always names the value or column directly next to it, the way a ticket prints `LOẠI` over its field. It never stands above a headline as an eyebrow or section kicker.

## Layout

The page is a station: a two-column grid with the board as a sticky, full-height left column (300 to 356px) and the platform filling the rest. The board's head (name and clock) and foot (storage note, reset) are always visible, and only the row list scrolls, fading out at the bottom when more rows remain. The platform centres a single ticket column (max 880px). It has 36px top padding, side padding that scales from 14px to 56px with the viewport, and 28px between the route list, the ticket and the ticket's footer.

The rhythm is tight: 6 to 8px between related items, 12 to 16px between blocks on paper, 20px board inset, 28px ticket inset. Board rows are 8px tall-padded (6px for reference rows). Rows align on a three-column grid: a 34px key, the name, and a right-aligned state.

Below 880px the board becomes a sticky top strip. It shows the name and a 28px clock, then drill rows as a single horizontally scrolling line (key, name, count), with reference rows as a second quieter line. Column heads, subtitles, units and the foot are hidden. The ticket insets drop from 28px to 18px, the route list moves below the ticket, two-column drill layouts stack, fare cells go from four columns to two, and keyboard hints are hidden.

## Elevation & Depth

The ground and board are flat. The ticket is the only object with depth, and it casts a single soft drop shadow drawn as a filter, so the shadow follows the punched holes and side notches instead of a rectangle. Inside the ticket there are no shadows; depth is tonal. The stub is a darker paper than the body, and code panes are a lighter wash of the same paper. On the board the only "depth" is emitted light: a soft glow behind LED digits.

### Shadow Vocabulary
- **Ticket drop** (`filter: drop-shadow(0 18px 28px rgba(0, 0, 0, 0.55))`): the ticket as a whole, and nothing else.
- **LED glow** (`text-shadow: 0 0 14px var(--led-glow)`; 12px on row keys and hot counts): Doto digits on the board. It is removed on the amber current row, where the digits are dark.

### Named Rules

**The One Object Rule.** Only the ticket lifts off the ground. Buttons, panes, strips and board rows stay flat. Hard offset shadows are not part of this world.

## Shapes

The ticket has gently rounded outer corners (6px). A stubbed ticket rounds the body's top corners and the stub's bottom corners. A ticket without a stub (a reference sheet) is one whole piece rounded on all four corners, with no perforation. Controls, panes, fields and lanes use 4px corners, and strips and inline code 3px. The tear handle is the system's only pill.

The perforation is the recurring silhouette. It is a 24px band of round holes on a 12px pitch, punched through to the ground colour, with a 24px half-circle notch cut into each side. After the tear, both edges keep half-holes at the same 12px pitch, and the stub's top carries the lower halves of the side notches.

Borders carry meaning by weight and dash. Interactive outlines are 1.5px solid ink. Pane outlines and header rules are 1px paper rule. Internal dividers are dashed. Drop zones and the sealed stub have a dashed ghost-ink outline, and the sealed stub is also cross-hatched at -45°.

## Components

### Buttons
Printed and plain: outlines drawn in ink, no fill until pressed.
- **Shape:** gently squared (4px), 1.5px ink outline, 7px 14px padding.
- **Outline (default):** transparent on paper, ink text; hover lays down the ink wash.
- **Solid:** ink fill, paper text; used for the one committing action in a set (for example "Nhớ chắc" or "Lưu lần thi này"); hover lifts to ink hover.
- **On the ground:** the ticket footer's previous/next/reference buttons sit on the night ground in amber text with a board-rule outline; hover lights the board-row tone.
- **Link-style:** secondary actions such as "Dán lại cuống, làm lại" or "Xóa tiến độ" are underlined text with a 3px offset, no box.
- **Keycaps:** shortcut keys ride inside buttons as small mono caps (11px, 1px currentColor border, 4px corners). Arrow keycaps are key names, not icons.
- **Focus:** a 2px ring that follows the material. It is amber on the board and ground, including the ticket-footer buttons, and ink on paper, including the tear handle.
- **Transitions:** background and colour, 120ms ease-out.

### Segmented Control
- **Style:** one 1.5px ink outline around 6px 12px segments, split by ink rules. The pressed segment is solid ink with paper text. It is used for level switches such as cloze level 1 and level 2.

### Board Row (Navigation)
The departure board is the navigation. Every drill is one row, one click from anywhere.
- **Default:** amber key (Doto), amber name (600), dim subtitle, and a right-aligned state with a Doto count over a small unit. A due or remaining count is "hot" (full amber, glowing); zero is dim.
- **Hover:** board-row tone. **Current:** the whole row fills amber, and the text turns board black.
- **Groups:** drill rows sit under column heads (Phím, Bài luyện, Trạng thái). Reference rows follow under their own head, shorter and at 500 weight, with no key or state.
- **Mobile:** a sticky horizontal strip of rows, the current row still filled amber.

### The Ticket (signature)
- **Head:** title (headline) with the ticket number at right (`Nº 01` in Doto over a label caption). Below that runs a row of label-over-value fields (type, topic, box, due, print time), then a 1px paper rule.
- **Body:** the prompt, hints, and the drill's working area.
- **Perforation and tear handle:** the round-hole band. It carries a centred pill handle in paper with an ink outline and a Space keycap, and turns solid ink on hover.
- **Sealed stub:** stub-grey paper holding a dashed, cross-hatched field that says the answer is under the stub.
- **Tear:** the handle disappears, both edges become scalloped half-holes, and the stub drops 12px and tilts -0.5° over 460ms on `cubic-bezier(0.16, 1, 0.3, 1)`. The answer, the verdict line and the reseal link then appear in the stub. Under reduced motion there is no transition and no tilt, just a 10px drop.
- **Foot:** ticket-to-ticket navigation on the ground, below the ticket.

### Inputs / Fields
- **Blank (cloze):** inline mono field with only a 1.5px ink underline over the amber marker; stronger marker on focus. Correct becomes bold ink on a 3px double underline; wrong becomes red text on a dashed red underline over the red wash.
- **Writer (recall):** a full-width mono textarea on the brighter pane with a 1px ghost-ink outline and 4px corners. It takes a 2px ink ring on focus.
- **Fare input:** a large Doto numeral field with a 1.5px ink outline on the brighter pane, labelled above in 12px faded ink. It takes a 2px ink ring on focus.
- **Checkbox (rubric):** native, tinted ink, on a three-column row of box, criterion and points.

### Code Surfaces
- **Code block:** printed on the paper pane with a 1px paper-rule outline and 4px corners. Never a dark editor card.
- **Parsons strip:** a paper strip (3px corners, paper-rule outline) with a faded Doto line number; hover darkens the outline. Correct gets an inset ink ring and bold; wrong gets a dashed red outline. Lanes are dashed ghost-ink drop zones with a label title.
- **Bug lines:** a numbered mono list on the pane; hover lays the ink wash, a picked line takes the lamp halo, the true bug is marked in ink, and a wrong pick gets a dashed red outline and a small "bạn chọn" tag.
- **Diff:** a mono list tagged per line: matched in ink, missing in red over the red wash, extra struck through in faded ink.

### Data on Paper
- **Fare cells:** a 4-up field grid inside a 1px paper-rule box, with dashed dividers. Each cell has a label over an 18px bold tabular value.
- **Tables (history, reference):** label-style heads over a solid paper rule; rows split by dashed paper rules; tabular numerals.
- **Reference sheet:** a stack of disclosure rows split by dashed rules. The summary is 15px 600, with a "Mở" / "Gập" label at right instead of an arrow.

## Do's and Don'ts

### Do:
- **Do** put every drill on a ticket: prompt on the body, answer held in the stub under the perforation, revealed only by the tear (handle or Space).
- **Do** keep the tear identical in every drill: 460ms on `cubic-bezier(0.16, 1, 0.3, 1)`, a 12px drop with a -0.5° tilt, and scalloped half-holes on the same 12px pitch as the perforation.
- **Do** give a stubless ticket (reference) all four 6px corners and no perforation. The perforation exists only where a stub hides something.
- **Do** show progress as board state in Doto: `0/10`, due counts and remaining counts, hot in full amber and idle in dimmed lamp.
- **Do** mark correct in heavier black ink and wrong in dashed thermal red over the red wash.
- **Do** set every Vietnamese word in Be Vietnam Pro, including words that sit on the LED board.
- **Do** keep money in the exam's own notation (`1,500K`, `2,340K`) in tabular figures.
- **Do** keep reference text folded by default in a disclosure sheet, with each block short enough to scan.
- **Do** draw focus as a 2px ring that follows the material: amber on the dark board and ground, ink on paper.

### Don't:
- **Don't** add a light theme, a theme toggle, or a second accent colour.
- **Don't** use amber as text, outline or solid fill on the paper. There it is only a translucent marker.
- **Don't** introduce green or any success colour, and don't add points, badges, streaks, confetti or celebratory states.
- **Don't** set Vietnamese text in Doto. It has no Vietnamese subset.
- **Don't** put code in dark editor cards or a docs-style sidebar layout. Code prints in ink on the paper.
- **Don't** add shadows to anything but the ticket, and never use hard offset shadows. The LED glow is light, not elevation.
- **Don't** stack an uppercase tracked label above a headline as an eyebrow or kicker. Labels only name an adjacent value or column.
- **Don't** use perforations or notches as ornament. Every one must mark something that tears.
- **Don't** draw icons from text glyphs. Keycaps name keys, and disclosure says "Mở" / "Gập" in words.
- **Don't** draw an amber focus ring on paper. It reaches only about 1.4:1 against thermal grey, so focus on paper is always ink.
