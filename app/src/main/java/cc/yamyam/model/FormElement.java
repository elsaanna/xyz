package cc.yamyam.model;

import android.content.Context;

import java.util.ArrayList;

import cc.yamyam.Utils;

/**
 * Created by siyuan on 10.08.15.
 */
public class FormElement {
    private String id;
    private String label;
    private String description;
    private String descriptionLink;
    private ArrayList<FormSelection> selection;
    private Object value;
    private Object defaultValue;
    private int specificType;
    private int type;

    private Boolean initSelection = false;
    private Boolean removable = false;
    private Boolean hidden = false;
    private Boolean ignore = false;

    private Boolean simple = false;

    public static int FORM_ELEMENT_TEXTFIELD = 10;
    public static int FORM_ELEMENT_PASSWORDFIELD = 11;
    public static int FORM_ELEMENT_TEXTAREA = 12;

    public static int FORM_ELEMENT_CHECKBOX = 1;
    public static int FORM_ELEMENT_IMAGE = 2;

    public static int FORM_ELEMENT_SELECT = 5;
    public static int FORM_ELEMENT_SELECT_CATEGORY = 6;

    public FormElement() {

    }

    public FormElement(Context c, String key, int type) {

        this.id = key;

        key = "setting_"+key.replace("[]", "");
        this.label = Utils.getStringByKey(c, key);
        this.description = Utils.getStringByKey(c, key+"_description");
        setSpecificType(type);

    }

    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public int getSpecificType() {
        return specificType;
    }


    public void setSpecificType(int type) {
        this.specificType = type;

        if(type >= FORM_ELEMENT_TEXTFIELD) {
            this.type = FORM_ELEMENT_TEXTFIELD;
        } else if(type >= FORM_ELEMENT_SELECT) {
            this.type = FORM_ELEMENT_SELECT;
        } else {
            this.type = type;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<FormSelection> getSelection() {
        return selection;
    }

    public void setSelection(ArrayList<FormSelection> selection) {
        this.selection = selection;
    }

    public Boolean getInitSelection() {
        return initSelection;
    }

    public void setInitSelection(Boolean initSelection) {
        this.initSelection = initSelection;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getRemovable() {
        return removable;
    }

    public void setRemovable(Boolean removable) {
        this.removable = removable;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getSimple() {
        return simple;
    }

    public void setSimple(Boolean simple) {
        this.simple = simple;
    }

    public String getDescriptionLink() {
        return descriptionLink;
    }

    public void setDescriptionLink(String descriptionLink) {
        this.descriptionLink = descriptionLink;
    }

    public Boolean getIgnore() {
        return ignore;
    }

    public void setIgnore(Boolean ignore) {
        this.ignore = ignore;
    }





}
