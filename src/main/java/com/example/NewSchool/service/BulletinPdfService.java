package com.example.NewSchool.service;

import com.example.NewSchool.model.Eleve;
import com.example.NewSchool.model.Note;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class BulletinPdfService {

    private static final BaseColor BLEU_FONCE  = new BaseColor(15, 23, 42);
    private static final BaseColor BLEU        = new BaseColor(59, 130, 246);
    private static final BaseColor BLEU_CLAIR  = new BaseColor(219, 234, 254);
    private static final BaseColor GRIS_CLAIR  = new BaseColor(241, 245, 249);
    private static final BaseColor VERT        = new BaseColor(21, 128, 61);
    private static final BaseColor VERT_CLAIR  = new BaseColor(220, 252, 231);
    private static final BaseColor ROUGE       = new BaseColor(185, 28, 28);
    private static final BaseColor ROUGE_CLAIR = new BaseColor(254, 226, 226);
    private static final BaseColor BLANC       = BaseColor.WHITE;

    public byte[] genererBulletin(Eleve eleve, List<Note> notes,
                                   double moyenne, int rang, int trimestre) throws Exception {

        Document doc = new Document(PageSize.A4, 36, 36, 48, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(doc, out);

        // Lecture seulment — pa ka modifye
        writer.setEncryption(null, null,
            PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY,
            PdfWriter.ENCRYPTION_AES_128);

        doc.open();

        Font fontTitrePrincipal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BLEU_FONCE);
        Font fontSousTitre      = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.GRAY);
        Font fontEntete         = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BLANC);
        Font fontNormal         = FontFactory.getFont(FontFactory.HELVETICA, 10, BLEU_FONCE);
        Font fontBold           = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BLEU_FONCE);
        Font fontMoyenne        = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, BLANC);
        Font fontMentionGrande  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BLANC);
        Font fontLabel          = FontFactory.getFont(FontFactory.HELVETICA, 9, BLANC);

        // ===== EN-TETE ECOLE =====
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingAfter(16);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(BLEU_FONCE);
        headerCell.setPadding(18);
        headerCell.setBorder(Rectangle.NO_BORDER);

        Paragraph titre = new Paragraph("NEW SCOOL", fontTitrePrincipal);
        titre.setAlignment(Element.ALIGN_CENTER);
        Font ftBlanc = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BLANC);
        titre = new Paragraph("NEW SCOOL", ftBlanc);
        titre.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(titre);

        Font fSousBlanc = FontFactory.getFont(FontFactory.HELVETICA, 11, new BaseColor(148, 163, 184));
        Paragraph sous = new Paragraph("Bulletin Scolaire Officiel — Trimestre " + trimestre, fSousBlanc);
        sous.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(sous);

        header.addCell(headerCell);
        doc.add(header);

        // ===== INFO ELEVE =====
        PdfPTable infoTable = new PdfPTable(3);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(16);
        infoTable.setWidths(new float[]{3f, 2f, 2f});

        addInfoCell(infoTable, "Élève", eleve.getNom() + " " + eleve.getPrenom(), GRIS_CLAIR);
        addInfoCell(infoTable, "Classe", eleve.getClasse() != null ? eleve.getClasse().getNomClasse() : "-", GRIS_CLAIR);
        addInfoCell(infoTable, "Rang", rang + "ème de la classe", GRIS_CLAIR);

        

        // ===== TABLEAU NOTES =====
        PdfPTable notesTable = new PdfPTable(6);
        notesTable.setWidthPercentage(100);
        notesTable.setSpacingAfter(20);
        notesTable.setWidths(new float[]{3.5f, 1.5f, 1.5f, 1.5f, 1.5f, 2f});

        // Entetes
        String[] headers = {"Matière", "Note 1", "Note 2", "Examen", "Moyenne", "Mention"};
        for (String h : headers) {
            PdfPCell c = new PdfPCell(new Phrase(h, fontEntete));
            c.setBackgroundColor(BLEU);
            c.setPadding(10);
            c.setBorderColor(BLEU);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            notesTable.addCell(c);
        }

        // Lignes notes
        boolean altRow = false;
        for (Note note : notes) {
            BaseColor rowColor = altRow ? GRIS_CLAIR : BLANC;
            altRow = !altRow;

            addNoteCell(notesTable, note.getMatiere().getNom(), fontBold, rowColor, Element.ALIGN_LEFT);
            addNoteCell(notesTable, fmt(note.getNote1()), fontNormal, rowColor, Element.ALIGN_CENTER);
            addNoteCell(notesTable, fmt(note.getNote2()), fontNormal, rowColor, Element.ALIGN_CENTER);
            addNoteCell(notesTable, fmt(note.getNoteExamen()), fontNormal, rowColor, Element.ALIGN_CENTER);

            Double moy = note.getMoyenne();
            Font fMoy = moy != null && moy >= 10
                ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, VERT)
                : FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, ROUGE);
            PdfPCell moyCell = new PdfPCell(new Phrase(fmt(moy), fMoy));
            moyCell.setBackgroundColor(rowColor);
            moyCell.setPadding(8);
            moyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            moyCell.setBorderColor(new BaseColor(226, 232, 240));
            notesTable.addCell(moyCell);

            String mention = note.getMention();
            BaseColor mentionBg = moy != null && moy >= 10 ? VERT_CLAIR : ROUGE_CLAIR;
            Font fMention = moy != null && moy >= 10
                ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, VERT)
                : FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, ROUGE);
            PdfPCell mCell = new PdfPCell(new Phrase(mention, fMention));
            mCell.setBackgroundColor(mentionBg);
            mCell.setPadding(8);
            mCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mCell.setBorderColor(new BaseColor(226, 232, 240));
            notesTable.addCell(mCell);
        }

        if (notes.isEmpty()) {
            PdfPCell vide = new PdfPCell(new Phrase("Aucune note disponible pour ce trimestre.", fontNormal));
            vide.setColspan(6);
            vide.setPadding(14);
            vide.setHorizontalAlignment(Element.ALIGN_CENTER);
            notesTable.addCell(vide);
        }

        doc.add(notesTable);

        // ===== BOX MOYENNE GENERALE =====
        PdfPTable moyBox = new PdfPTable(2);
        moyBox.setWidthPercentage(100);
        moyBox.setWidths(new float[]{1f, 1f});
        moyBox.setSpacingAfter(20);

        // Moyenne
        PdfPCell moyenneCell = new PdfPCell();
        moyenneCell.setBackgroundColor(BLEU_FONCE);
        moyenneCell.setPadding(20);
        moyenneCell.setBorder(Rectangle.NO_BORDER);
        Paragraph moyP = new Paragraph();
        moyP.setAlignment(Element.ALIGN_CENTER);
        moyP.add(new Chunk("MOYENNE GÉNÉRALE\n", fontLabel));
        String moyStr = moyenne > 0 ? String.format("%.2f", moyenne) + " / 20" : "N/A";
        moyP.add(new Chunk(moyStr, fontMoyenne));
        moyenneCell.addElement(moyP);
        moyBox.addCell(moyenneCell);

        // Statut
        PdfPCell statutCell = new PdfPCell();
        boolean admis = moyenne >= 10;
        statutCell.setBackgroundColor(admis ? VERT : ROUGE);
        statutCell.setPadding(20);
        statutCell.setBorder(Rectangle.NO_BORDER);
        Paragraph statP = new Paragraph();
        statP.setAlignment(Element.ALIGN_CENTER);
        statP.add(new Chunk("DÉCISION\n", fontLabel));
        statP.add(new Chunk(admis ? "✓ ADMIS(E)" : "✗ EN DIFFICULTÉ", fontMentionGrande));
        statutCell.addElement(statP);
        moyBox.addCell(statutCell);

        doc.add(moyBox);

        // ===== PIED DE PAGE =====
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);
        PdfPCell footCell = new PdfPCell();
        footCell.setBackgroundColor(GRIS_CLAIR);
        footCell.setPadding(10);
        footCell.setBorder(Rectangle.NO_BORDER);
        Font fGray = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
        Paragraph footP = new Paragraph(
            "Ce bulletin est généré automatiquement par NewScool. Document officiel — Ne peut être modifié.", fGray);
        footP.setAlignment(Element.ALIGN_CENTER);
        footCell.addElement(footP);
        footer.addCell(footCell);
        doc.add(footer);

        doc.close();
        return out.toByteArray();
    }

    private void addInfoCell(PdfPTable t, String label, String value, BaseColor bg) {
        PdfPCell c = new PdfPCell();
        c.setBackgroundColor(bg);
        c.setPadding(10);
        c.setBorderColor(new BaseColor(226, 232, 240));
        Font fl = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
        Font fv = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BLEU_FONCE);
        c.addElement(new Paragraph(label, fl));
        c.addElement(new Paragraph(value != null ? value : "-", fv));
        t.addCell(c);
    }

    private void addNoteCell(PdfPTable t, String val, Font f, BaseColor bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(val, f));
        c.setBackgroundColor(bg);
        c.setPadding(8);
        c.setHorizontalAlignment(align);
        c.setBorderColor(new BaseColor(226, 232, 240));
        t.addCell(c);
    }

    private String fmt(Double v) {
        if (v == null) return "-";
        return String.format("%.2f", v);
    }
}
