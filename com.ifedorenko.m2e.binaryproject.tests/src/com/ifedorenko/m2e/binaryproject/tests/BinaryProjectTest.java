/*******************************************************************************
 * Copyright (c) 2012 Igor Fedorenko
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Igor Fedorenko - initial API and implementation
 *******************************************************************************/
package com.ifedorenko.m2e.binaryproject.tests;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.jdt.internal.BuildPathManager;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.ClasspathHelpers;

import com.ifedorenko.m2e.binaryproject.BinaryProjectPlugin;

@SuppressWarnings( { "restriction" } )
public class BinaryProjectTest
    extends AbstractMavenProjectTestCase
{
    IProgressMonitor monitor = new NullProgressMonitor();

    public void testBasic()
        throws Exception
    {
        IProject project =
            BinaryProjectPlugin.getInstance().create( "org.apache.maven", "maven-core", "3.0.4", null, monitor );

        assertTrue( project.hasNature( IMavenConstants.NATURE_ID ) );
        assertTrue( project.hasNature( JavaCore.NATURE_ID ) );
        assertNotNull( BuildPathManager.getMaven2ClasspathContainer( JavaCore.create( project ) ) );
        assertFalse( hasBuilder( project, JavaCore.BUILDER_ID ) );

        IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create( project, monitor );

        assertNotNull( facade );
        assertEquals( BinaryProjectPlugin.LIFECYCLE_MAPPING_ID, facade.getLifecycleMappingId() );

        IJavaProject javaProject = JavaCore.create( project );

        ClasspathHelpers.assertClasspath( new String[] { "org.eclipse.jdt.launching.JRE_CONTAINER/.*",
            "org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER", ".*/maven-core-3.0.4.jar" }, javaProject.getRawClasspath() );

        IClasspathEntry[] mavenClasspath =
            BuildPathManager.getMaven2ClasspathContainer( javaProject ).getClasspathEntries();

        assertTrue( mavenClasspath.length > 0 );
    }

    private boolean hasBuilder( IProject project, String builderId )
        throws CoreException
    {
        for ( ICommand command : project.getDescription().getBuildSpec() )
        {
            if ( builderId.equals( command.getBuilderName() ) )
            {
                return true;
            }
        }
        return false;
    }

}
