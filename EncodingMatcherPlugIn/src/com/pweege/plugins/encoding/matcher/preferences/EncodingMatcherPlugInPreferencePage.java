package com.pweege.plugins.encoding.matcher.preferences;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.pweege.plugins.encoding.matcher.ativator.Activator;

public class EncodingMatcherPlugInPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private FilePatternListEditor includesFileListEditor;
	private FilePatternListEditor excludesFileListEditor;

	public EncodingMatcherPlugInPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("File Pattern for Encoding-Matcher-Plugin");
	}

	@Override
	protected void createFieldEditors() {

		this.includesFileListEditor = new FilePatternListEditor("includesFileListEditor", "Includes File Pattern",
				getFieldEditorParent());
		addField(this.includesFileListEditor);

		this.excludesFileListEditor = new FilePatternListEditor("excludesFileListEditor", "Excludes File Pattern",
				getFieldEditorParent());
		addField(this.excludesFileListEditor);

	}

	private class FilePatternListEditor extends org.eclipse.jface.preference.ListEditor {

		private FilePatternListEditor(String name, String labelText, Composite parent) {
			super(name, labelText, parent);
		}

		@Override
		protected String createList(String[] theArray) {
			if (theArray == null)
				return "";
			return StringUtils.join(theArray, ";");
		}

		@Override
		protected String getNewInputObject() {
			InputDialog dlg = new InputDialog(null, "Enter a File Pattern", "File Pattern", null, null);
			if (dlg.open() == org.eclipse.jface.window.Window.OK) {
				String newPattern = dlg.getValue();
				newPattern = StringUtils.trimToNull(newPattern);
				return StringUtils.defaultIfBlank(newPattern, null);
			}
			return null;
		}

		@Override
		protected String[] parseString(String strList) {
			if (strList == null)
				return ArrayUtils.EMPTY_STRING_ARRAY;
			return StringUtils.split(strList, ";");
		}

	}

}
