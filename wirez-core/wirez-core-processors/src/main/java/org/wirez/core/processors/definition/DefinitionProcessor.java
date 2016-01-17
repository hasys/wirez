/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wirez.core.processors.definition;

import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;
import org.uberfire.annotations.processors.exceptions.GenerationException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({DefinitionProcessor.ANNOTATION_DEFINITION})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class DefinitionProcessor extends AbstractErrorAbsorbingProcessor {

    public static final String ANNOTATION_DEFINITION = "org.wirez.core.api.annotation.definition.Definition";
    public static final String ANNOTATION_DEFINITION_PROPERTY = "org.wirez.core.api.annotation.definition.Property";

    private final DefinitionGenerator definitionGenerator;

    public DefinitionProcessor() {
        DefinitionGenerator pg = null;
        try {
            pg = new DefinitionGenerator();
        } catch (Throwable t) {
            rememberInitializationError(t);
        }
        definitionGenerator = pg;
    }

    @Override
    protected boolean processWithExceptions(Set<? extends TypeElement> set, RoundEnvironment roundEnv) throws Exception {

        //We don't have any post-processing
        if ( roundEnv.processingOver() ) {
            return false;
        }

        //If prior processing threw an error exit
        if ( roundEnv.errorRaised() ) {
            return false;
        }

        final Messager messager = processingEnv.getMessager();
        final Elements elementUtils = processingEnv.getElementUtils();

        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement(ANNOTATION_DEFINITION) ) ) {
            if (e.getKind() == ElementKind.INTERFACE) {

                TypeElement classElement = (TypeElement) e;
                PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

                messager.printMessage(Diagnostic.Kind.NOTE, "Discovered definition class [" + classElement.getSimpleName() + "]");

                final String packageName = packageElement.getQualifiedName().toString();
                final String classNameActivity = classElement.getSimpleName() + "Definition";

                try {
                    //Try generating code for each required class
                    messager.printMessage( Diagnostic.Kind.NOTE, "Generating code for [" + classNameActivity + "]" );

                    final StringBuffer definitionClassCode = definitionGenerator.generate( packageName,
                            packageElement,
                            classNameActivity,
                            classElement,
                            processingEnv );

                    writeCode( packageName,
                            classNameActivity,
                            definitionClassCode );

                } catch ( GenerationException ge ) {
                    final String msg = ge.getMessage();
                    processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg, classElement );
                }

            }
        }

        return true;
    }


}
