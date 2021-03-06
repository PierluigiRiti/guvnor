/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.datamodel.backend.server.testclasses;

/**
 * Test class to check Java enums are extracted correctly by ProjectDataModelOracleBuilder
 */
public class TestJavaEnum1 {

    public enum TestEnum {
        ZERO,
        ONE,
        TWO
    }

    private TestEnum field1;

    public TestEnum getField1() {
        return field1;
    }

    public void setField1( TestEnum field1 ) {
        this.field1 = field1;
    }

}
