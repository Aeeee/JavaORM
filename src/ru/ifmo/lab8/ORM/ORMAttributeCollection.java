package ru.ifmo.lab8.ORM;

import ru.ifmo.lab8.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ORMAttributeCollection {
    private List<ORMAttribute> attributes = new ArrayList<>();
    private ORMFieldRoot rootField;

    public List<ORMAttribute> getAttributesList() {
        return Collections.unmodifiableList(attributes);
    }

    public void addAttributes(Class from) {
        rootField = new ORMFieldRoot(from);
        addAttributesRecursively("", rootField, from);
    }

    public ORMFieldRoot getRootField() {
        return rootField;
    }

    private void addAttributesRecursively(String prefix, ORMFieldComplex prev, Class from) {
        Field[] fields = from.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (ignoreField(fields[i]))
                continue;

            if (ORMAttribute.isSimple(fields[i]))
                addAttribute(prefix, prev, fields[i]);
            else
                addAttributeRecursively(prefix, prev, fields[i]);
        }
    }

    private void addAttribute(String prefix, ORMFieldComplex prev, Field field) {
        String[] splittedName = field.getName().split(".");
        String name = (splittedName.length > 0) ? splittedName[splittedName.length - 1]
                : field.getName();

        ORMAttribute attr = new ORMAttribute(prefix + name, field);
        prev.addField(new ORMFieldSimple(name, attr));
        attributes.add(attr);
    }

    private void addAttributeRecursively(String prefix, ORMFieldComplex prev, Field field) {
        String[] splittedName = field.getName().split(".");
        String name = (splittedName.length > 0) ? splittedName[splittedName.length - 1]
                : field.getName();

        ORMFieldComplex nextField = new ORMFieldComplex(name, field.getType());
        prev.addField(nextField);

        addAttributesRecursively(prefix + name + "_", nextField, field.getType());
    }

    private boolean ignoreField(Field field) {
        if (!field.isAnnotationPresent(ORMAccessible.class))
            return true;
        
        if (Modifier.isStatic(field.getModifiers())) {
            Utils.printWarning("Field cannot be static: " + field.getName());
            return true;
        }
        if (Modifier.isTransient(field.getModifiers())) {
            Utils.printWarning("Field cannot be marked as transient: " + field.getName());
            return true;
        }
        if (field.getName().contains("_")) {
            Utils.printWarning("Field cannot contain underline character: " + field.getName());
            return true;
        }

        return false;
    }

    public boolean validateCollection() {
        if (attributes.size() == 0) {
            Utils.printError("Object cannot have 0 attributes. " +
                    "(Saved attributes should be marked as @ORMAccessible).");
            return false;
        }

        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).getName().isEmpty()) {
                Utils.printError("Found attribute with empty name! (id: " + i + ").");
                return false;
            }
            if (attributes.get(i).getName().equalsIgnoreCase("_id")) {
                Utils.printError("Saved fields cannot have name '_id'!");
                return false;
            }
            if (attributes.get(i).getPsqlType().equalsIgnoreCase("unknown")) {
                Utils.printError(i + "-th attribute has unknown data type!");
                return false;
            }
        }

        for (int i = 0; i < attributes.size(); i++) {
            String nameA = attributes.get(i).getName();
            for (int j = i+1; j < attributes.size(); j++) {
                String nameB = attributes.get(j).getName();
                if (nameA.equalsIgnoreCase(nameB)) {
                    Utils.printError(nameA + " looks the same as " + nameB + " for DBMS. " +
                            "Please choice different name for one of those fields.");
                    return false;
                }
            }
        }

        return true;
    }

    void debugPrint() {
        for (ORMAttribute attr : attributes) {
            Utils.print(attr.getName() + " : " + attr.getPsqlType());
        }
    }
}
