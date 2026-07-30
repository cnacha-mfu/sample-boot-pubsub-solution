"""Generate the overview diagram for the sample-boot-pubsub README.

Run from a repo root:  python docs/make_pubsub_diagram.py .
Then copy the PNG to the student repo. Needs: pip install pillow
Re-running must reproduce the committed PNG byte for byte.
"""
from PIL import Image, ImageDraw, ImageFont

S = 2
F = "C:/Windows/Fonts/"
f_title = ImageFont.truetype(F + "segoeuib.ttf", 19 * S)
f_head = ImageFont.truetype(F + "segoeuib.ttf", 16 * S)
f_port = ImageFont.truetype(F + "consolab.ttf", 15 * S)
f_body = ImageFont.truetype(F + "consola.ttf", 13 * S)
f_lblb = ImageFont.truetype(F + "segoeuib.ttf", 13 * S)
f_note = ImageFont.truetype(F + "segoeui.ttf", 12 * S)
f_tag = ImageFont.truetype(F + "consolab.ttf", 12 * S)
f_cell = ImageFont.truetype(F + "consolab.ttf", 12 * S)

INK = "#1f2328"
MUTED = "#6b7280"
LINE = "#374151"

PURPLE = dict(head="#ede9fe", txt="#5b21b6", border="#a78bfa")
GREEN = dict(head="#dcfce7", txt="#14532d", border="#4ade80")
BLUE = dict(head="#dbeafe", txt="#1e3a8a", border="#60a5fa")
GREY = dict(head="#f3f4f6", txt="#374151", border="#9ca3af")
AMBER = "#b45309"


def box(d, x, y, w, name, port, rows, pal, head_h=44, row_h=24, pad=14):
    h = head_h + row_h * len(rows) + 12
    d.rounded_rectangle([x * S, y * S, (x + w) * S, (y + h) * S], radius=8 * S,
                        fill="white", outline=pal["border"], width=2 * S)
    d.rounded_rectangle([x * S, y * S, (x + w) * S, (y + head_h) * S], radius=8 * S,
                        fill=pal["head"], outline=pal["border"], width=2 * S)
    d.rectangle([x * S, (y + head_h - 10) * S, (x + w) * S, (y + head_h) * S],
                fill=pal["head"])
    d.line([x * S, (y + head_h) * S, (x + w) * S, (y + head_h) * S],
           fill=pal["border"], width=2 * S)
    d.text(((x + pad) * S, (y + 8) * S), name, font=f_head, fill=pal["txt"])
    if port:
        d.text(((x + w - pad) * S, (y + 10) * S), port, font=f_port,
               fill=pal["txt"], anchor="ra")
    ry = y + head_h + 8
    for row in rows:
        d.text(((x + pad) * S, ry * S), row, font=f_body, fill=INK)
        ry += row_h
    return h


def arrow(d, x1, y1, x2, y2, colour=LINE, width=2):
    import math
    d.line([x1 * S, y1 * S, x2 * S, y2 * S], fill=colour, width=width * S)
    ang = math.atan2(y2 - y1, x2 - x1)
    for sign in (1, -1):
        a = ang + sign * 0.42
        d.line([x2 * S, y2 * S, (x2 - 13 * math.cos(a)) * S,
                (y2 - 13 * math.sin(a)) * S], fill=colour, width=width * S)


def text(d, x, y, s, font=f_note, fill=INK, anchor="la"):
    d.text((x * S, y * S), s, font=font, fill=fill, anchor=anchor)


def draw(path):
    W, H = 1240, 620
    img = Image.new("RGB", (W * S, H * S), "white")
    d = ImageDraw.Draw(img)

    text(d, 40, 24, "One announcement, two reactions", font=f_title)
    text(d, 40, 50, "The producer never calls the subscribers. It hands one "
                    "event to the broker, and every subscribed group gets its "
                    "own copy.", fill=MUTED)

    # ---- client -------------------------------------------------- (left)
    d.rounded_rectangle([40 * S, 120 * S, 250 * S, 162 * S], radius=8 * S,
                        fill=GREY["head"], outline=GREY["border"], width=2 * S)
    text(d, 54, 132, "Browser / Postman", font=f_head, fill=GREY["txt"])

    arrow(d, 145, 162, 145, 232)
    text(d, 160, 172, "POST /borrows", font=f_lblb)
    text(d, 160, 192, '{"bookTitle":"1984",', font=f_body, fill=MUTED)
    text(d, 160, 208, ' "memberName":"Alice Johnson"}', font=f_body, fill=MUTED)

    # ---- producer -------------------------------------------------
    box(d, 40, 232, 270, "borrow-service", ":8200",
        ["the PRODUCER", "kafkaTemplate.send(", '    "borrows", eventJson)'],
        PURPLE)
    text(d, 40, 372, "no database - it announces", fill=MUTED)
    text(d, 40, 390, "and moves on. It does not wait,", fill=MUTED)
    text(d, 40, 408, "and does not know who listens.", fill=MUTED)

    # ---- broker ---------------------------------------------------
    bx, by, bw = 420, 160, 360
    d.rounded_rectangle([bx * S, by * S, (bx + bw) * S, (by + 240) * S],
                        radius=10 * S, fill="#f8fafc", outline=BLUE["border"],
                        width=2 * S)
    text(d, bx + 16, by + 10, "Kafka broker", font=f_head, fill=BLUE["txt"])
    text(d, bx + bw - 16, by + 12, ":9094 / :9092", font=f_port,
         fill=BLUE["txt"], anchor="ra")
    text(d, bx + 16, by + 40, "topic \"borrows\" - an append-only log:",
         font=f_lblb)

    # the log cells
    cy = by + 68
    labels = ["e1", "e2", "e3", "e4", "e5"]
    cw, chh = 52, 40
    for i, lab in enumerate(labels):
        cx = bx + 20 + i * (cw + 6)
        d.rectangle([cx * S, cy * S, (cx + cw) * S, (cy + chh) * S],
                    fill=BLUE["head"], outline=BLUE["border"], width=2 * S)
        d.text(((cx + cw / 2) * S, (cy + 12) * S), lab, font=f_cell,
               fill=BLUE["txt"], anchor="ma")
    # next slot, dashed
    cx = bx + 20 + 5 * (cw + 6)
    for t in range(0, chh, 8):
        d.line([cx * S, (cy + t) * S, cx * S, (cy + min(t + 4, chh)) * S],
               fill=BLUE["border"], width=2 * S)
        d.line([(cx + cw) * S, (cy + t) * S, (cx + cw) * S,
                (cy + min(t + 4, chh)) * S], fill=BLUE["border"], width=2 * S)
    d.line([cx * S, cy * S, (cx + cw) * S, cy * S], fill=BLUE["border"], width=2 * S)
    d.line([cx * S, (cy + chh) * S, (cx + cw) * S, (cy + chh) * S],
           fill=BLUE["border"], width=2 * S)
    d.text(((cx + cw / 2) * S, (cy + 12) * S), "e6", font=f_cell,
           fill=MUTED, anchor="ma")

    text(d, bx + 16, cy + 54, "events are only APPENDED, never changed -",
         fill=MUTED)
    text(d, bx + 16, cy + 72, "they stay here even after everyone has read them",
         fill=MUTED)

    # zookeeper, attached below
    d.rounded_rectangle([bx * S, (by + 254) * S, (bx + bw) * S, (by + 296) * S],
                        radius=8 * S, fill=GREY["head"], outline=GREY["border"],
                        width=2 * S)
    text(d, bx + 16, by + 264, "ZooKeeper", font=f_lblb, fill=GREY["txt"])
    text(d, bx + bw - 16, by + 266, "the broker's own bookkeeping", fill=MUTED,
         anchor="ra")

    # producer -> broker
    arrow(d, 310, 292, bx, 292)
    text(d, 318, 268, "publish", font=f_lblb)
    text(d, 318, 300, "1 event, sent once", fill=MUTED)

    # ---- subscribers ---------------------------------------------- (right)
    sx = 900
    h1 = box(d, sx, 120, 300, "notification-service", ":8201",
             ["@KafkaListener(", '  groupId="notification-group")',
              "one Notification PER EVENT"], GREEN)
    h2 = box(d, sx, 330, 300, "popularity-service", ":8202",
             ["@KafkaListener(", '  groupId="popularity-group")',
              "counts DERIVED from events"], GREEN)

    # broker -> subscribers: each group gets every event
    arrow(d, bx + bw, 240, sx, 185, colour=AMBER)
    text(d, 800, 176, "every event", font=f_lblb, fill=AMBER)
    arrow(d, bx + bw, 300, sx, 395, colour=AMBER)
    text(d, 792, 404, "every event, again", font=f_lblb, fill=AMBER)

    text(d, sx, 262, "page: http://localhost:8201/", font=f_body, fill=MUTED)
    text(d, sx, 472, "page: http://localhost:8202/", font=f_body, fill=MUTED)

    # ---- footer ---------------------------------------------------
    d.line([40 * S, (H - 76) * S, (W - 40) * S, (H - 76) * S],
           fill="#e5e7eb", width=1 * S)
    text(d, 40, H - 64,
         "Different group ids are why BOTH services receive every event. Two "
         "copies of ONE service would share a group id - and split the stream "
         "between them instead.", fill=MUTED)
    text(d, 40, H - 44,
         "Stop a subscriber and nothing else fails: its events wait in the "
         "log, and it catches up when it returns.", fill=MUTED)
    text(d, 40, H - 24,
         "Doors: your machine reaches the broker at localhost:9094, programs "
         "inside Docker at kafka:9092. Same broker.", fill=MUTED)

    img.save(path, "PNG", optimize=True)
    print("wrote", path, img.size)


if __name__ == "__main__":
    import sys
    draw(sys.argv[1].rstrip("/") + "/pubsub-overview.png")
