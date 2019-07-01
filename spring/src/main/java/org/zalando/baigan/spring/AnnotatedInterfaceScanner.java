package org.zalando.baigan.spring;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import java.lang.annotation.Annotation;

final class AnnotatedInterfaceScanner extends ClassPathScanningCandidateComponentProvider {

    AnnotatedInterfaceScanner(final Class<? extends Annotation> annotation) {
        super(false);
        addIncludeFilter(new AnnotationTypeFilter(annotation));
    }

    @Override
    protected boolean isCandidateComponent(final AnnotatedBeanDefinition definition) {
        return definition.getMetadata().isInterface();
    }
}
