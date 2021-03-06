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
// BEGIN GENERATED CODE
package gov.redhawk.ide.sdr.tests;

import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.SdrRoot;
import junit.textui.TestRunner;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.util.EList;
import org.junit.Assert;

/**
 * <!-- begin-user-doc --> A test case for the model object '
 * <em><b>Components Container</b></em>'. <!-- end-user-doc -->
 * @generated
 */
public class ComponentsContainerTest extends SoftPkgRegistryTest {
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(ComponentsContainerTest.class);
	}

	/**
	 * Constructs a new Components Container test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentsContainerTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Components Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected ComponentsContainer getFixture() {
		return (ComponentsContainer)fixture;
	}

	private SdrRoot sdrRoot;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated NOT
	 */
	@Override
	protected void setUp() throws Exception {
		this.sdrRoot = SdrTestsUtil.getSdrTestsSdrRoot();
		this.sdrRoot.load(null);
		setFixture(this.sdrRoot.getComponentsContainer());
		Assert.assertNotNull(fixture);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

	public void testComponentsContainer() {
		final EList<SoftPkg> components = getFixture().getComponents();
		Assert.assertEquals(3, components.size());
		for (final SoftPkg softPkg : components) {
			Assert.assertNotNull(softPkg);
		}
		Assert.assertEquals(this.sdrRoot, getFixture().getSdrRoot());
		
		Assert.assertNotNull(this.fixture);
	}

} // ComponentsContainerTest
