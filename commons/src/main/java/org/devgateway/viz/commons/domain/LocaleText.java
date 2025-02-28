package org.devgateway.viz.commons.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "fk_index_text", columnList = "text"),
        @Index(name = "fk_index_language", columnList = "language_id")
})
public class LocaleText {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String text;

    private boolean machineTranslation;


    @ManyToOne(targetEntity = Language.class, fetch = jakarta.persistence.FetchType.EAGER)
    private Language language;

    public LocaleText() {
    }

    public LocaleText(String text, Language language) {
        this.text = text;
        this.language = language;
        this.machineTranslation = false;
    }

    public LocaleText(String text, Language language, Boolean machineTranslation) {
        this.text = text;
        this.language = language;
        this.machineTranslation = machineTranslation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public boolean isMachineTranslation() {
        return machineTranslation;
    }

    public void setMachineTranslation(boolean machineTranslation) {
        this.machineTranslation = machineTranslation;
    }
}
