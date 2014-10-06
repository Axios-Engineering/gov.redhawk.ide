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
package gov.redhawk.ide.ui.tests.runtime;

import gov.redhawk.ide.swtbot.StandardTestActions;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class DomainInteractionTest extends AbstractDomainRuntimeTest {

	private SWTBot viewBot;
	private SWTBotView explorerView;

	@Before
	public void before() throws Exception {
		super.before();

		explorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		explorerView.show();
		explorerView.setFocus();
		viewBot = explorerView.bot();

		launchDomainManager("REDHAWK_DEV");

	}

	@Test
	public void testConnect() {
		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();

		SWTBotShell shell = bot.activeShell();
		Assert.assertEquals("New Domain Manager", shell.getText());
		SWTBot wizardBot = shell.bot();

		wizardBot.textWithLabel("Display Name:").setText("REDHAWK");
		Assert.assertEquals("REDHAWK", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN");
		Assert.assertEquals("REDHAWK_DOMAIN", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST01");
		Assert.assertEquals("REDHAWK_DOMAIN_TEST01", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());
		
		wizardBot.textWithLabel("Display Name:").setText("REDHAWK_DEV");
		Assert.assertEquals("REDHAWK_DEV", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		bot.button("Finish").click();

		viewBot.tree().getTreeItem("REDHAWK_DEV CONNECTED").expand();
		viewBot.tree().getTreeItem("REDHAWK_DEV CONNECTED").select();
	}
}