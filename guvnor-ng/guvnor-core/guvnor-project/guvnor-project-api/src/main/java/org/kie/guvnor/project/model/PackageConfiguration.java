package org.kie.guvnor.project.model;

import org.drools.guvnor.models.commons.shared.imports.Imports;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PackageConfiguration {

    private Imports imports = new Imports();

    public Imports getImports() {
        return imports;
    }
}
