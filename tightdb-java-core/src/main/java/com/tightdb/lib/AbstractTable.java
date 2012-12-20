package com.tightdb.lib;

import com.tightdb.ColumnType;
import com.tightdb.Group;
import com.tightdb.TableBase;
import com.tightdb.TableSpec;

public abstract class AbstractTable<Cursor, View, Query> extends AbstractTableOrView<Cursor, View, Query> {

	static {
		TightDB.loadLibrary();
	}

	protected final TableBase table;

	public AbstractTable(EntityTypes<?, View, Cursor, Query> types) {
		this(types, new TableBase());
	}

	public AbstractTable(EntityTypes<?, View, Cursor, Query> types, Group group) {
		this(types, group.getTable(types.getTableClass().getSimpleName()));
	}

	@SuppressWarnings("unchecked")
	protected AbstractTable(EntityTypes<?, View, Cursor, Query> types, TableBase table) {
		super(types, table);
		this.table = table;
		if (table != null && table.getTableSpec().getColumnCount() <= 0) {
			// Build table schema
			final TableSpec spec = new TableSpec();
			specifyTableStructure((Class<? extends AbstractTable<?, ?, ?>>) types.getTableClass(), spec);
			table.updateFromSpec(spec);
		}
	}

	public boolean isValid() {
		return table.isValid();
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	/**
	 * Returns the number of columns in the table.
	 * 
	 * @return the number of columns.
	 */
	public long getColumnCount() {
		return table.getColumnCount();
	}

	/**
	 * Returns the name of a column identified by columnIndex. Notice that the
	 * index is zero based.
	 * 
	 * @param columnIndex
	 *            the column index
	 * @return the name of the column
	 */
	public String getColumnName(long columnIndex) {
		return table.getColumnName(columnIndex);
	}

	/**
	 * Returns the 0-based index of a column based on the name.
	 * 
	 * @param columnName
	 *            the column name
	 * @return the index, -1 if not found
	 */
	public long getColumnIndex(String columnName) {
		return table.getColumnIndex(columnName);
	}

	/**
	 * Get the type of a column identified by the columnIdex.
	 * 
	 * @param columnIndex
	 *            index of the column.
	 * @return Type of the particular column.
	 */
	public ColumnType getColumnType(long columnIndex) {
		return table.getColumnType(columnIndex);
	}

	protected static void addLongColumn(TableSpec spec, String name) {
		spec.addColumn(ColumnType.ColumnTypeInt, name);
	}

	protected static void addStringColumn(TableSpec spec, String name) {
		spec.addColumn(ColumnType.ColumnTypeString, name);
	}

	protected static void addBooleanColumn(TableSpec spec, String name) {
		spec.addColumn(ColumnType.ColumnTypeBool, name);
	}

	protected static void addBinaryColumn(TableSpec spec, String name) {
		spec.addColumn(ColumnType.ColumnTypeBinary, name);
	}

	protected static void addDateColumn(TableSpec spec, String name) {
		spec.addColumn(ColumnType.ColumnTypeDate, name);
	}

	protected static void addMixedColumn(TableSpec spec, String name) {
		spec.addColumn(ColumnType.ColumnTypeMixed, name);
	}

	@SuppressWarnings("unchecked")
	protected static void addTableColumn(TableSpec spec, String name, AbstractTable<?, ?, ?> subtable) {
		TableSpec subspec = spec.addSubtableColumn(name);
		specifyTableStructure((Class<? extends AbstractTable<?, ?, ?>>) subtable.getClass(), subspec);
	}

	protected static void specifyTableStructure(Class<? extends AbstractTable<?, ?, ?>> tableClass, TableSpec spec) {
		try {
			tableClass.getMethod("specifyStructure", TableSpec.class).invoke(tableClass, spec);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't specify the table structure!", e);
		}
	}

	protected void doInsert(long rowIndex, Object... values) {
		table.insert(rowIndex, values);
	}

	public void setIndex(long columnIndex) {
		table.setIndex(columnIndex);
	}

	public boolean hasIndex(long columnIndex) {
		return table.hasIndex(columnIndex);
	}

	public void optimize() {
		table.optimize();
	}

	public Query where() {
		return AbstractQuery.createQuery(types.getQueryClass(), table, table.where());
	}

}
