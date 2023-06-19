/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service

import org.apache.poi.xwpf.usermodel.*
import org.ppfc.api.model.service.change.ChangeResponse
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat


class ChangesWordDocumentGenerator {

    private fun createTitle(document: XWPFDocument) = with(document) {
        val paragraph = createParagraph()
        paragraph.alignment = ParagraphAlignment.CENTER

        paragraph.createRun().apply {
            setText("ЗМІНИ")
            isBold = true
            fontFamily = defaultFontFamily
            fontSize = 20
            characterSpacing = 100
        }
    }

    private fun createSubtitle(
        document: XWPFDocument,
        date: String,
        day: String,
        weekAlternation: String
    ) = with(document) {
        val paragraph = createParagraph()
        paragraph.alignment = ParagraphAlignment.CENTER
        paragraph.spacingBetween = 1.5

        paragraph.createRun().apply {
            setText("у розкладі навчальних занять груп денного відділення")
            addBreak(BreakType.TEXT_WRAPPING)
            setText("на $date $day($weekAlternation):")
            isBold = true
            fontFamily = defaultFontFamily
            fontSize = 14
        }
    }

    private fun createChangesTable(
        document: XWPFDocument,
        changesStates: List<ChangeState>
    ) = with(document) {
        val table: XWPFTable = createTable(changesStates.size, 3)

        table.setWidth("100%")
        table.setTopBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")
        table.setBottomBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")
        table.setLeftBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")
        table.setRightBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")
        table.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")
        table.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")

        table.getRow(0).apply {
            getCell(0).setWidth("20%")
            getCell(1).setWidth("20%")
            getCell(2).setWidth("60%")
        }

        changesStates.forEachIndexed { index, changeState ->
            val row: XWPFTableRow = table.getRow(index)

            row.getCell(0).apply {
                val paragraph = paragraphs[0]
                val run = paragraph.createRun()
                run.setText(changeState.groupsColumn)
                run.fontFamily = defaultFontFamily
                run.fontSize = 14
            }

            row.getCell(1).apply {
                val paragraph = paragraphs[0]
                val run = paragraph.createRun()
                run.setText(changeState.lessonNumberColumn)
                run.fontFamily = defaultFontFamily
                run.fontSize = 14
            }

            row.getCell(2).apply {
                val paragraph = paragraphs[0]
                val run = paragraph.createRun()
                run.setText(changeState.eventColumn)
                run.fontFamily = defaultFontFamily
                run.fontSize = 14
            }
        }
    }

    private fun createSignatureField(document: XWPFDocument) = with(document) {
        val table: XWPFTable = createTable(1, 2)
        table.setWidth("100%")
        table.setTopBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")
        table.setBottomBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")
        table.setLeftBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")
        table.setRightBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")
        table.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")
        table.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "")

        table.getRow(0).getCell(0).apply {
            setWidth("50%")
            val paragraph = paragraphs[0]
            val run = paragraph.createRun()
            run.setText("Заступник директора з НР")
            paragraph.alignment = ParagraphAlignment.LEFT
            run.fontFamily = defaultFontFamily
            run.isBold = true
            run.fontSize = 12
        }

        table.getRow(0).getCell(1).apply {
            setWidth("50%")
            val paragraph = paragraphs[0]
            val run = paragraph.createRun()
            run.setText("Олена БАБИЧ")
            paragraph.alignment = ParagraphAlignment.RIGHT
            run.fontFamily = defaultFontFamily
            run.isBold = true
            run.fontSize = 12
        }
    }

    private fun convertChangesToStates(changes: List<ChangeResponse>): List<ChangeState> {
        return changes.mapIndexedNotNull { index, change ->
            if (change.subject == null && change.eventName == null) return@mapIndexedNotNull null

            val groupColumn = changes.getOrNull(index - 1)?.let { previousElement ->
                if (previousElement.groups == change.groups) {
                    ""
                } else {
                    null
                }
            } ?: change.groups.joinToString(
                separator = ", ",
                postfix = " гр."
            ) { it.number.toString() }

            val lessonNumberColumn = change.lessonNumber?.toString()?.plus(" пара") ?: ""

            val eventColumn = if (change.subject != null) {
                change.subject.name
            } else {
                change.eventName
            } + (change.classroom?.name?.let { " — $it" } ?: "")


            ChangeState(
                groupsColumn = groupColumn,
                lessonNumberColumn = lessonNumberColumn,
                eventColumn = eventColumn
            )
        }
    }

    private fun dateISO8601ToDMY(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("dd.MM.yyyy")

        return outputFormat.format(inputFormat.parse(date))
    }

    private fun dayNumberToName(dayNumber: Long) = when (dayNumber) {
        1L -> "ПОНЕДІЛОК"
        2L -> "ВІВТОРОК"
        3L -> "СЕРЕДА"
        4L -> "ЧЕТВЕР"
        5L -> "П'ЯТНИЦЯ"
        6L -> "СУБОТА"
        else -> "НЕДІЛЯ"
    }

    private fun weekAlternationToName(isNumerator: Boolean) = if (isNumerator) "чисельник" else "знаменник"

    fun generate(changes: List<ChangeResponse>): ByteArray? {
        println(changes)
        if (changes.isEmpty()) return null

        val changesStates = convertChangesToStates(changes = changes)
        val firstChange = changes.first()

        val document = XWPFDocument()

        createTitle(document = document)
        createSubtitle(
            document = document,
            date = dateISO8601ToDMY(date = firstChange.date),
            day = dayNumberToName(dayNumber = firstChange.dayNumber),
            weekAlternation = weekAlternationToName(isNumerator = firstChange.isNumerator)
        )
        createChangesTable(
            document = document,
            changesStates = changesStates
        )
        document.createParagraph()
        createSignatureField(document = document)

        val fileOutputStream = FileOutputStream("doc.docx")
        val bytesOutputStream = ByteArrayOutputStream()
        document.write(fileOutputStream)
        document.write(bytesOutputStream)
        fileOutputStream.close()
        bytesOutputStream.close()

        return bytesOutputStream.toByteArray()
    }

    private data class ChangeState(
        val groupsColumn: String,
        val lessonNumberColumn: String,
        val eventColumn: String
    )

    private companion object {
        const val defaultFontFamily = "Times New Roman"
    }
}