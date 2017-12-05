
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Cette classe représente le modèle de données lu par une JTable afin d'être
 * rendu et affiché. <br/>
 * Gère l'affichage par ligne. Pour chaque ligne, un objet est lu en plaçant ses
 * valeurs dans chaque colonne en fonction de la position de la colonne. La
 * position de la colonne est utilisée pour connaître le nom de l'attribut à
 * lire : les noms d'attributs sont récupèrés par index dans un tableau.
 *
 *
 * @param <T> la classe des objets à lister dans la JTable
 */
public class GenericTableModel<T> extends AbstractTableModel {

    /**
     * last update : 08 / 11 /2015
     */
    private static final long serialVersionUID = 20151108L;

    private final String[] attributeNames;
    private final List<T> values;

    public GenericTableModel(List<T> values, String[] attributes) {
        this.attributeNames = attributes;
        this.values = values;
    }

    public GenericTableModel(List<T> values, Class<T> clazz) {
        this(values, readAttributes(clazz));
    }

    private static String[] readAttributes(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    @Override
    public int getColumnCount() {
        return attributeNames.length;
    }

    public String getColumnName(int col) {
        return attributeNames[col];
    }

    @Override
    public int getRowCount() {
        return values.size();
    }

    public void addValue(T value) {
        int firstRow = values.size() - 1;
        values.add(value);
        int lastRow = values.size() - 1;
        fireTableRowsInserted(firstRow, lastRow);
    }

    public T getObjectAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex <= values.size() - 1) {
            return values.get(rowIndex);
        }
        return null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        T object = getObjectAt(rowIndex);
        String attribute = attributeNames[columnIndex];

        return getFieldValue(object, attribute);
    }

    private Field getField(T object, String fieldName) {
        Class<?> c = object.getClass();
        Field f;
        try {
            f = c.getDeclaredField(fieldName);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            f = null;
        }
        return f;
    }

    private Object getFieldValue(T object, String fieldName) {
        Field f = getField(object, fieldName);
        if (f == null) {
            return null;
        }
        boolean accessible = f.isAccessible();
        f.setAccessible(true);
        Object value;
        try {
            value = f.get(object);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            value = null;
        }
        f.setAccessible(accessible);
        return value;
    }
}
