/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.sad.graphiti.ui.tests;

import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.tests.utils.WaveformUtils;
import gov.redhawk.ide.swtbot.tests.utils.EditorTestUtils;
import gov.redhawk.ide.swtbot.tests.utils.FindByUtils;
import gov.redhawk.ide.swtbot.tests.utils.MenuUtils;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class) 
public class WaveformFindByTests {

	private static SWTGefBot gefBot;
	private static SWTBotGefEditor editor;
	private static SWTWorkbenchBot wbBot;
	private String waveformName;
	private static final String[] FINDBYS = { FindByUtils.FIND_BY_CORBA_NAME, FindByUtils.FIND_BY_DOMAIN_MANAGER, 
		FindByUtils.FIND_BY_EVENT_CHANNEL, FindByUtils.FIND_BY_FILE_MANAGER, FindByUtils.FIND_BY_SERVICE };

	@BeforeClass
	public static void beforeClass() throws Exception {
		while (PlatformUI.getWorkbench().isStarting()) {
			Thread.sleep(1000);
		}
	}

	@Before
	public void beforeTest() throws Exception {
		gefBot = new SWTGefBot();
		wbBot = new SWTWorkbenchBot();
		SWTBotPerspective perspective = wbBot.perspectiveById("gov.redhawk.ide.ui.perspectives.sca");
		perspective.activate();
		wbBot.resetActivePerspective();
	}

	@After
	public void afterTest() {
		if (waveformName != null) {
			MenuUtils.closeAndDelete(gefBot, waveformName);
		}
		gefBot.closeAllEditors();
	}

	@AfterClass
	public static void afterClass() {
		gefBot.sleep(2000);
	}

	/**
	 * IDE-669
	 * Components are removed with the delete button (trashcan image) that appears when you select the component, 
	 * but the delete context menu does not remove the component from the diagram. In most cases, the delete and 
	 * remove context menu options are grayed out and not selectable.
	 */
	@Test
	public void checkFindByContextMenuDelete() {
		waveformName = "IDE-669-Test";

		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		for (String s : FINDBYS) {
			// Add component to diagram from palette
			EditorTestUtils.dragFromPaletteToDiagram(editor, s, 0, 0);
			FindByUtils.completeFindByWizard(gefBot, s);
		}

		for (String s : FINDBYS) {
			// Drill down to graphiti component shape
			SWTBotGefEditPart gefEditPart = editor.getEditPart(FindByUtils.getFindByDefaultName(s));
			EditorTestUtils.deleteFromDiagram(editor, gefEditPart);
			gefBot.button("Yes").click(); // are you sure you want to delete this element?
			Assert.assertNull(editor.getEditPart(s));
		}
	}

	/**
	 * IDE-653
	 * Users are able to directly edit the name of Component and FindBy shapes on the diagram by double clicking on it.  
	 * If you click on text other than the usage name and it will think you are editing the usage name.
	 * This likely involves telling Graphiti not to do anything when selecting certain Pictogram Elements.
	 */
//	@Test
//	public void checkFindByDirectEdit() {
//		waveformName = "IDE-653-Test";
//		WaveformUtils.createNewWaveform(gefBot, waveformName);
//		editor = gefBot.gefEditor(waveformName);
//
//		for (String findBy : FINDBYS) {
//			// Add component to diagram from palette
//			EditorTestUtils.dragFromPaletteToDiagram(editor, findBy, 0, 0);
//			FindByUtils.completeFindByWizard(gefBot, findBy);
//
//			// Drill down to graphiti component shape
//			SWTBotGefEditPart gefEditPart = editor.getEditPart(findBy);
//			ComponentShapeImpl componentShape = (ComponentShapeImpl) gefEditPart.part().getModel();
//
//			// Grab the associated business object and confirm it is a FindByStub
//			Object bo = DUtil.getBusinessObject(componentShape);
//			Assert.assertTrue("business object should be of type FindByStub", bo instanceof FindByStub);
//			SadComponentInstantiation ci = (SadComponentInstantiation) bo;
//
//			// TODO Edit via directEdit
//			String initName = ci.getUsageName();
//			editor.getEditPart(initName).activateDirectEdit();
//
//			editor.directEditType(initName + "_edit");
//			gefEditPart.click();
//
//			Assert.assertEquals(initName + "_edit", ci.getUsageName());
//
//			// Save, close, and reopen
//			MenuUtils.closeAll(gefBot, true);
//			EditorTestUtils.openSadDiagram(wbBot, waveformName);
//			Assert.assertEquals(initName + "_edit", ci.getUsageName());
//		}
//	}
//	
//	@Test
//	public void checkFindByEdit() {
//		
//	}


}