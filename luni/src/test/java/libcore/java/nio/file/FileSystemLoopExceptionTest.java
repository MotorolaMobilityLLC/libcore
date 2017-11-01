/*
 * Copyright (C) 2016 The Android Open Source Project
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
 * limitations under the License
 */

package libcore.java.nio.file;

import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystemLoopException;
import libcore.libcore.util.SerializationTester;

public class FileSystemLoopExceptionTest extends TestCase {

    public void test_constructor$String() {
        FileSystemLoopException exception = new FileSystemLoopException("file");
        assertEquals("file", exception.getFile());
        assertTrue(exception instanceof FileSystemException);
    }

    public void test_serialization () throws IOException, ClassNotFoundException {
        String hex = "ACED0005737200256A6176612E6E696F2E66696C652E46696C6553797374656D4C6F6F7045786"
                + "3657074696F6E4335EED96F492F51020000787200216A6176612E6E696F2E66696C652E46696C655"
                + "3797374656D457863657074696F6ED598F27876D360FC0200024C000466696C657400124C6A61766"
                + "12F6C616E672F537472696E673B4C00056F7468657271007E0002787200136A6176612E696F2E494"
                + "F457863657074696F6E6C8073646525F0AB020000787200136A6176612E6C616E672E45786365707"
                + "4696F6ED0FD1F3E1A3B1CC4020000787200136A6176612E6C616E672E5468726F7761626C65D5C63"
                + "5273977B8CB0300044C000563617573657400154C6A6176612F6C616E672F5468726F7761626C653"
                + "B4C000D64657461696C4D65737361676571007E00025B000A737461636B547261636574001E5B4C6"
                + "A6176612F6C616E672F537461636B5472616365456C656D656E743B4C00147375707072657373656"
                + "4457863657074696F6E737400104C6A6176612F7574696C2F4C6973743B787071007E00097075720"
                + "01E5B4C6A6176612E6C616E672E537461636B5472616365456C656D656E743B02462A3C3CFD22390"
                + "200007870000000097372001B6A6176612E6C616E672E537461636B5472616365456C656D656E746"
                + "109C59A2636DD8502000449000A6C696E654E756D6265724C000E6465636C6172696E67436C61737"
                + "371007E00024C000866696C654E616D6571007E00024C000A6D6574686F644E616D6571007E00027"
                + "870000000237400316C6962636F72652E6A6176612E6E696F2E66696C652E46696C6553797374656"
                + "D4C6F6F70457863657074696F6E5465737474002046696C6553797374656D4C6F6F7045786365707"
                + "4696F6E546573742E6A617661740012746573745F73657269616C697A6174696F6E7371007E000CF"
                + "FFFFFFE7400186A6176612E6C616E672E7265666C6563742E4D6574686F6474000B4D6574686F642"
                + "E6A617661740006696E766F6B657371007E000C000000F9740028766F6761722E7461726765742E6"
                + "A756E69742E4A756E69743324566F6761724A556E69745465737474000B4A756E6974332E6A61766"
                + "174000372756E7371007E000C00000063740020766F6761722E7461726765742E6A756E69742E4A5"
                + "56E697452756E6E657224317400104A556E697452756E6E65722E6A61766174000463616C6C73710"
                + "07E000C0000005C740020766F6761722E7461726765742E6A756E69742E4A556E697452756E6E657"
                + "224317400104A556E697452756E6E65722E6A61766174000463616C6C7371007E000C000000ED740"
                + "01F6A6176612E7574696C2E636F6E63757272656E742E4675747572655461736B74000F467574757"
                + "2655461736B2E6A61766174000372756E7371007E000C0000046D7400276A6176612E7574696C2E6"
                + "36F6E63757272656E742E546872656164506F6F6C4578656375746F72740017546872656164506F6"
                + "F6C4578656375746F722E6A61766174000972756E576F726B65727371007E000C0000025F74002E6"
                + "A6176612E7574696C2E636F6E63757272656E742E546872656164506F6F6C4578656375746F72245"
                + "76F726B6572740017546872656164506F6F6C4578656375746F722E6A61766174000372756E73710"
                + "07E000C000002F97400106A6176612E6C616E672E54687265616474000B5468726561642E6A61766"
                + "174000372756E7372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C697"
                + "3747AB817B43CA79EDE02000078707874000466696C6570";
        FileSystemLoopException exception = (FileSystemLoopException) SerializationTester
                .deserializeHex(hex);

        String hex1 = SerializationTester.serializeHex(exception).toString();
        assertEquals(hex, hex1);
        assertEquals("file", exception.getFile());
    }
}
