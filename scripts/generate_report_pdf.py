#!/usr/bin/env python3
"""Generate Report.pdf from database-testing-seminar-report-vi.md."""

from __future__ import annotations

import html
import re
import sys
from pathlib import Path

from PIL import Image as PILImage
from pypdf import PdfReader
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    BaseDocTemplate,
    Frame,
    Image,
    KeepTogether,
    ListFlowable,
    ListItem,
    PageBreak,
    PageTemplate,
    Paragraph,
    Preformatted,
    Spacer,
    Table,
    TableStyle,
)
from reportlab.platypus.tableofcontents import TableOfContents


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "database-testing-seminar-report-vi.md"
OUTPUT = ROOT / "Report.pdf"
TEMP = ROOT / "tmp" / "pdfs" / "Report.first-pass.pdf"

NAVY = colors.HexColor("#123B66")
TEAL = colors.HexColor("#0E8C8C")
INK = colors.HexColor("#24364B")
MUTED = colors.HexColor("#5D6D7E")
LINE = colors.HexColor("#D8E1EA")
PALE = colors.HexColor("#F4F7FA")
CORAL = colors.HexColor("#D86B5B")


def register_fonts() -> None:
    font_dir = Path("/System/Library/Fonts/Supplemental")
    pdfmetrics.registerFont(TTFont("Arial", str(font_dir / "Arial.ttf")))
    pdfmetrics.registerFont(TTFont("Arial-Bold", str(font_dir / "Arial Bold.ttf")))
    pdfmetrics.registerFont(TTFont("Arial-Italic", str(font_dir / "Arial Italic.ttf")))
    pdfmetrics.registerFontFamily(
        "Arial", normal="Arial", bold="Arial-Bold", italic="Arial-Italic"
    )


def inline_markup(text: str) -> str:
    escaped = html.escape(text, quote=False)
    escaped = re.sub(r"`([^`]+)`", r'<font name="Arial-Bold">\1</font>', escaped)
    escaped = re.sub(r"\*\*([^*]+)\*\*", r"<b>\1</b>", escaped)
    escaped = re.sub(r"(?<!\*)\*([^*]+)\*(?!\*)", r"<i>\1</i>", escaped)
    return escaped


def make_styles():
    styles = getSampleStyleSheet()
    styles.add(
        ParagraphStyle(
            name="BodyVI",
            fontName="Arial",
            fontSize=10.2,
            leading=15.2,
            textColor=INK,
            alignment=TA_JUSTIFY,
            spaceAfter=7,
            wordWrap="CJK",
        )
    )
    styles.add(
        ParagraphStyle(
            name="SectionVI",
            fontName="Arial-Bold",
            fontSize=18,
            leading=23,
            textColor=NAVY,
            spaceBefore=2,
            spaceAfter=12,
            keepWithNext=True,
            wordWrap="CJK",
        )
    )
    styles.add(
        ParagraphStyle(
            name="SubsectionVI",
            fontName="Arial-Bold",
            fontSize=13.2,
            leading=17,
            textColor=TEAL,
            spaceBefore=10,
            spaceAfter=7,
            keepWithNext=True,
            wordWrap="CJK",
        )
    )
    styles.add(
        ParagraphStyle(
            name="BulletVI",
            parent=styles["BodyVI"],
            leftIndent=15,
            firstLineIndent=0,
            spaceAfter=3,
        )
    )
    styles.add(
        ParagraphStyle(
            name="TableVI",
            fontName="Arial",
            fontSize=8.3,
            leading=11,
            textColor=INK,
            wordWrap="CJK",
        )
    )
    styles.add(
        ParagraphStyle(
            name="TableHeadVI",
            fontName="Arial-Bold",
            fontSize=8.5,
            leading=11,
            textColor=colors.white,
            wordWrap="CJK",
        )
    )
    styles.add(
        ParagraphStyle(
            name="CodeVI",
            fontName="Arial",
            fontSize=7.5,
            leading=10,
            textColor=INK,
            leftIndent=7,
            rightIndent=7,
            borderColor=LINE,
            borderWidth=0.6,
            borderPadding=7,
            backColor=PALE,
            spaceBefore=4,
            spaceAfter=8,
        )
    )
    styles.add(
        ParagraphStyle(
            name="QuoteVI",
            fontName="Arial-Italic",
            fontSize=10,
            leading=14,
            textColor=NAVY,
            leftIndent=13,
            borderColor=TEAL,
            borderWidth=0,
            borderPadding=8,
            backColor=colors.HexColor("#EAF5F5"),
            spaceBefore=5,
            spaceAfter=8,
        )
    )
    styles.add(
        ParagraphStyle(
            name="CaptionVI",
            fontName="Arial-Italic",
            fontSize=8.5,
            leading=11,
            alignment=TA_CENTER,
            textColor=MUTED,
            spaceBefore=4,
            spaceAfter=9,
        )
    )
    return styles


class SeminarDocTemplate(BaseDocTemplate):
    def __init__(self, filename: Path, total_pages: int | None, styles, **kwargs):
        self.total_pages = total_pages
        self.report_styles = styles
        super().__init__(str(filename), **kwargs)
        frame = Frame(
            self.leftMargin,
            self.bottomMargin,
            self.width,
            self.height,
            id="normal",
        )
        self.addPageTemplates(PageTemplate(id="report", frames=frame, onPage=self.draw_page))

    def draw_page(self, canvas, doc):
        page = doc.page
        width, height = A4
        canvas.saveState()
        if page > 1:
            canvas.setStrokeColor(LINE)
            canvas.setLineWidth(0.6)
            canvas.line(20 * mm, height - 17 * mm, width - 20 * mm, height - 17 * mm)
            canvas.setFont("Arial-Bold", 8)
            canvas.setFillColor(TEAL)
            canvas.drawString(20 * mm, height - 13.5 * mm, "BÁO CÁO SEMINAR")
            canvas.setFont("Arial", 8)
            canvas.setFillColor(MUTED)
            canvas.drawRightString(
                width - 20 * mm,
                height - 13.5 * mm,
                "KIỂM THỬ CƠ SỞ DỮ LIỆU CHO ESHOP",
            )
            canvas.line(20 * mm, 16 * mm, width - 20 * mm, 16 * mm)
            canvas.setFont("Arial", 8)
            canvas.setFillColor(MUTED)
            canvas.drawString(20 * mm, 11 * mm, "Seminar kiểm thử cơ sở dữ liệu EShop")
            total = self.total_pages if self.total_pages else "?"
            canvas.drawRightString(width - 20 * mm, 11 * mm, f"Trang {page} / {total}")
        canvas.restoreState()

    def afterFlowable(self, flowable):
        if isinstance(flowable, Paragraph):
            style_name = flowable.style.name
            if style_name == "SectionVI":
                text = flowable.getPlainText()
                key = "section-%s" % self.seq.nextf("section")
                self.canv.bookmarkPage(key)
                self.canv.addOutlineEntry(text, key, level=0, closed=False)
                self.notify("TOCEntry", (0, text, self.page, key))
            elif style_name == "SubsectionVI":
                text = flowable.getPlainText()
                key = "subsection-%s" % self.seq.nextf("subsection")
                self.canv.bookmarkPage(key)
                self.canv.addOutlineEntry(text, key, level=1, closed=False)
                self.notify("TOCEntry", (1, text, self.page, key))


def cover_story(styles):
    title_style = ParagraphStyle(
        "CoverTitle",
        fontName="Arial-Bold",
        fontSize=25,
        leading=31,
        alignment=TA_CENTER,
        textColor=NAVY,
        spaceAfter=10,
    )
    subtitle_style = ParagraphStyle(
        "CoverSubtitle",
        fontName="Arial-Bold",
        fontSize=15,
        leading=20,
        alignment=TA_CENTER,
        textColor=TEAL,
    )
    center = ParagraphStyle(
        "CoverCenter",
        fontName="Arial",
        fontSize=11,
        leading=17,
        alignment=TA_CENTER,
        textColor=INK,
    )
    label = ParagraphStyle(
        "CoverLabel",
        fontName="Arial-Bold",
        fontSize=10,
        leading=15,
        alignment=TA_LEFT,
        textColor=NAVY,
    )
    value = ParagraphStyle(
        "CoverValue",
        fontName="Arial",
        fontSize=10,
        leading=15,
        alignment=TA_LEFT,
        textColor=INK,
    )

    identity = Table(
        [
            [Paragraph("Giảng viên", label), Paragraph("Hồ Tuấn Thanh", value)],
            [Paragraph("Sinh viên", label), Paragraph("23127081 - Nguyễn Phan Hùng Linh", value)],
            ["", Paragraph("23127172 - Nguyễn Chí Đức", value)],
            ["", Paragraph("23127487 - Thôi Đặng Trường Thọ", value)],
        ],
        colWidths=[33 * mm, 87 * mm],
        hAlign="CENTER",
    )
    identity.setStyle(
        TableStyle(
            [
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LINEBELOW", (0, 0), (-1, -1), 0.4, LINE),
                ("TOPPADDING", (0, 0), (-1, -1), 7),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 7),
            ]
        )
    )

    return [
        Spacer(1, 12 * mm),
        Paragraph("ĐẠI HỌC QUỐC GIA THÀNH PHỐ HỒ CHÍ MINH", center),
        Paragraph("TRƯỜNG ĐẠI HỌC KHOA HỌC TỰ NHIÊN", center),
        Paragraph("KHOA CÔNG NGHỆ THÔNG TIN", center),
        Spacer(1, 34 * mm),
        Paragraph("BÁO CÁO SEMINAR", subtitle_style),
        Spacer(1, 6 * mm),
        Paragraph("Kiểm thử cơ sở dữ liệu cho EShop", title_style),
        Spacer(1, 25 * mm),
        identity,
        Spacer(1, 36 * mm),
        Paragraph("Thành phố Hồ Chí Minh, tháng 7 năm 2026", center),
        PageBreak(),
    ]


def toc_story(styles):
    toc_title = ParagraphStyle(
        "TOCTitle",
        fontName="Arial-Bold",
        fontSize=22,
        leading=28,
        textColor=NAVY,
        alignment=TA_CENTER,
        spaceAfter=16,
    )
    toc = TableOfContents()
    toc.levelStyles = [
        ParagraphStyle(
            "TOCLevel1",
            fontName="Arial-Bold",
            fontSize=10,
            leading=14,
            leftIndent=0,
            firstLineIndent=0,
            textColor=NAVY,
            spaceBefore=4,
        ),
        ParagraphStyle(
            "TOCLevel2",
            fontName="Arial",
            fontSize=8.5,
            leading=11,
            leftIndent=12,
            firstLineIndent=0,
            textColor=MUTED,
        ),
    ]
    return [Paragraph("Mục lục", toc_title), toc, PageBreak()]


def image_flowable(path_text: str, alt: str, styles):
    path_text = path_text.strip()
    if path_text.startswith("<") and path_text.endswith(">"):
        path_text = path_text[1:-1]
    image_path = ROOT / path_text
    if not image_path.exists():
        return Paragraph(f"[Không tìm thấy ảnh: {html.escape(path_text)}]", styles["QuoteVI"])
    with PILImage.open(image_path) as source:
        width, height = source.size
    max_width = 165 * mm
    max_height = 190 * mm
    scale = min(max_width / width, max_height / height, 1.0)
    image = Image(str(image_path), width=width * scale, height=height * scale)
    image.hAlign = "CENTER"
    caption = Paragraph(inline_markup(alt), styles["CaptionVI"])
    return KeepTogether([image, caption])


def markdown_table(lines, styles):
    parsed = []
    for line in lines:
        cells = [cell.strip() for cell in line.strip().strip("|").split("|")]
        if all(re.fullmatch(r":?-{3,}:?", cell) for cell in cells):
            continue
        parsed.append(cells)
    if not parsed:
        return Spacer(1, 1)
    columns = max(len(row) for row in parsed)
    for row in parsed:
        row.extend([""] * (columns - len(row)))
    usable_width = A4[0] - 40 * mm
    col_widths = [usable_width / columns] * columns
    data = []
    for row_index, row in enumerate(parsed):
        style = styles["TableHeadVI"] if row_index == 0 else styles["TableVI"]
        data.append([Paragraph(inline_markup(cell), style) for cell in row])
    table = Table(data, colWidths=col_widths, repeatRows=1, hAlign="LEFT")
    commands = [
        ("BACKGROUND", (0, 0), (-1, 0), NAVY),
        ("GRID", (0, 0), (-1, -1), 0.45, LINE),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 6),
        ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 6),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
    ]
    for row_index in range(1, len(data)):
        if row_index % 2 == 0:
            commands.append(("BACKGROUND", (0, row_index), (-1, row_index), PALE))
    table.setStyle(TableStyle(commands))
    return table


def markdown_story(styles):
    lines = SOURCE.read_text(encoding="utf-8").splitlines()
    story = []
    paragraph_lines = []
    first_section = True

    def flush_paragraph():
        if paragraph_lines:
            text = " ".join(part.strip() for part in paragraph_lines).strip()
            if text:
                story.append(Paragraph(inline_markup(text), styles["BodyVI"]))
            paragraph_lines.clear()

    index = 0
    while index < len(lines):
        line = lines[index]
        stripped = line.strip()

        if stripped.startswith("```"):
            flush_paragraph()
            index += 1
            code = []
            while index < len(lines) and not lines[index].strip().startswith("```"):
                code.append(lines[index])
                index += 1
            story.append(Preformatted("\n".join(code), styles["CodeVI"], maxLineLength=95))
        elif stripped.startswith("## "):
            flush_paragraph()
            if not first_section:
                story.append(PageBreak())
            first_section = False
            story.append(Paragraph(inline_markup(stripped[3:]), styles["SectionVI"]))
        elif stripped.startswith("### "):
            flush_paragraph()
            story.append(Paragraph(inline_markup(stripped[4:]), styles["SubsectionVI"]))
        elif stripped.startswith("# "):
            flush_paragraph()
        elif stripped.startswith("|"):
            flush_paragraph()
            table_lines = []
            while index < len(lines) and lines[index].strip().startswith("|"):
                table_lines.append(lines[index])
                index += 1
            index -= 1
            story.append(markdown_table(table_lines, styles))
            story.append(Spacer(1, 7))
        elif re.fullmatch(r"!\[.*?\]\(.+\)", stripped):
            flush_paragraph()
            match = re.fullmatch(r"!\[(.*?)\]\((.+)\)", stripped)
            story.append(image_flowable(match.group(2), match.group(1), styles))
        elif re.match(r"^-\s+", stripped):
            flush_paragraph()
            items = []
            while index < len(lines) and re.match(r"^-\s+", lines[index].strip()):
                item_text = re.sub(r"^-\s+", "", lines[index].strip())
                items.append(
                    ListItem(
                        Paragraph(inline_markup(item_text), styles["BulletVI"]),
                        leftIndent=10,
                    )
                )
                index += 1
            index -= 1
            story.append(
                ListFlowable(
                    items,
                    bulletType="bullet",
                    start="circle",
                    leftIndent=14,
                    bulletFontName="Arial",
                    bulletFontSize=7,
                    bulletColor=TEAL,
                    spaceAfter=7,
                )
            )
        elif re.match(r"^\d+\.\s+", stripped):
            flush_paragraph()
            items = []
            while index < len(lines) and re.match(r"^\d+\.\s+", lines[index].strip()):
                item_text = re.sub(r"^\d+\.\s+", "", lines[index].strip())
                items.append(
                    ListItem(
                        Paragraph(inline_markup(item_text), styles["BulletVI"]),
                        leftIndent=12,
                    )
                )
                index += 1
            index -= 1
            story.append(
                ListFlowable(
                    items,
                    bulletType="1",
                    leftIndent=16,
                    bulletFontName="Arial-Bold",
                    bulletFontSize=8,
                    bulletColor=NAVY,
                    spaceAfter=7,
                )
            )
        elif stripped.startswith(">"):
            flush_paragraph()
            quote = stripped.lstrip("> ")
            story.append(Paragraph(inline_markup(quote), styles["QuoteVI"]))
        elif not stripped:
            flush_paragraph()
        else:
            paragraph_lines.append(stripped)
        index += 1

    flush_paragraph()
    return story


def build_pdf(output: Path, total_pages: int | None) -> None:
    styles = make_styles()
    doc = SeminarDocTemplate(
        output,
        total_pages=total_pages,
        styles=styles,
        pagesize=A4,
        leftMargin=20 * mm,
        rightMargin=20 * mm,
        topMargin=23 * mm,
        bottomMargin=22 * mm,
        title="Báo cáo Seminar - Kiểm thử cơ sở dữ liệu cho EShop",
        author="Nguyễn Phan Hùng Linh, Nguyễn Chí Đức, Thôi Đặng Trường Thọ",
    )
    story = cover_story(styles) + toc_story(styles) + markdown_story(styles)
    doc.multiBuild(story)


def main() -> int:
    register_fonts()
    TEMP.parent.mkdir(parents=True, exist_ok=True)
    build_pdf(TEMP, total_pages=None)
    total_pages = len(PdfReader(str(TEMP)).pages)
    build_pdf(OUTPUT, total_pages=total_pages)
    final_pages = len(PdfReader(str(OUTPUT)).pages)
    if final_pages != total_pages:
        build_pdf(OUTPUT, total_pages=final_pages)
    TEMP.unlink(missing_ok=True)
    print(f"Generated {OUTPUT} ({final_pages} pages)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
