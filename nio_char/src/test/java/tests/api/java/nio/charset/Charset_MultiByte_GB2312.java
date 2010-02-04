/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.api.java.nio.charset;

import dalvik.annotation.KnownFailure;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;

import java.nio.charset.CharacterCodingException;

@TestTargetClass(targets.Charsets.GB2312.class)

public class Charset_MultiByte_GB2312 extends Charset_AbstractTest {

    @Override
    protected void setUp() throws Exception {
        charsetName = "GB2312";

        testChars = theseChars(new int[]{
0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 
16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 
32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 
48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 
64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 
80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 
96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 
112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 
164, 167, 168, 176, 177, 215, 224, 225, 232, 233, 234, 236, 237, 242, 243, 247, 
249, 250, 252, 257, 275, 333, 363, 462, 711, 913, 945, 1025, 1055, 1085, 8213, 8243, 
8451, 8544, 8592, 8712, 8743, 8776, 8814, 8857, 8978, 9312, 9342, 9472, 9502, 9532, 9632, 9670, 
9733, 9792, 12288, 12353, 12383, 12413, 12449, 12479, 12509, 12539, 12569, 12832, 19968, 19998, 20029, 20060, 
20094, 20127, 20159, 20189, 20219, 20249, 20280, 20311, 20342, 20372, 20405, 20439, 20472, 20504, 20538, 20570, 
20603, 20643, 20687, 20717, 20747, 20799, 20834, 20864, 20896, 20928, 20960, 20991, 21021, 21051, 21084, 21117, 
21147, 21182, 21215, 21246, 21277, 21307, 21338, 21368, 21400, 21430, 21460, 21490, 21520, 21550, 21584, 21617, 
21647, 21677, 21708, 21738, 21769, 21799, 21830, 21860, 21890, 21927, 21957, 21987, 22017, 22047, 22079, 22114, 
22149, 22179, 22218, 22251, 22281, 22312, 22343, 22374, 22404, 22434, 22466, 22496, 22528, 22558, 22596, 22629, 
22659, 22696, 22737, 22768, 22799, 22829, 22859, 22899, 22930, 22962, 22992, 23033, 23064, 23094, 23125, 23156, 
23186, 23218, 23250, 23281, 23318, 23348, 23379, 23409, 23439, 23472, 23504, 23534, 23567, 23601, 23631, 23662, 
23692, 23723, 23755, 23786, 23822, 23853, 23883, 23913, 23961, 23991, 24027, 24061, 24091, 24123, 24155, 24186, 
24217, 24247, 24278, 24308, 24339, 24369, 24400, 24432, 24464, 24494, 24524, 24554, 24586, 24616, 24651, 24681, 
24713, 24744, 24774, 24806, 24838, 24868, 24904, 24935, 24971, 25001, 25032, 25062, 25094, 25124, 25155, 25187, 
25220, 25250, 25282, 25314, 25345, 25375, 25405, 25438, 25472, 25504, 25534, 25566, 25597, 25627, 25658, 25688, 
25720, 25750, 25781, 25815, 25856, 25893, 25925, 25955, 25991, 26021, 26051, 26082, 26112, 26143, 26174, 26207, 
26238, 26269, 26302, 26332, 26364, 26395, 26426, 26460, 26492, 26522, 26552, 26584, 26621, 26653, 26684, 26720, 
26753, 26786, 26816, 26848, 26881, 26911, 26941, 26973, 27004, 27035, 27067, 27099, 27133, 27167, 27197, 27227, 
27257, 27287, 27424, 27454, 27490, 27521, 27553, 27583, 27617, 27653, 27684, 27714, 27744, 27774, 27807, 27837, 
27867, 27898, 27929, 27961, 27993, 28023, 28053, 28085, 28118, 28151, 28182, 28212, 28243, 28286, 28316, 28346, 
28378, 28409, 28448, 28478, 28508, 28538, 28572, 28608, 28638, 28689, 28725, 28766, 28796, 28828, 28859, 28889, 
28919, 28949, 28982, 29020, 29050, 29080, 29113, 29152, 29190, 29224, 29255, 29286, 29316, 29356, 29389, 29420, 
29450, 29481, 29517, 29548, 29579, 29609, 29640, 29671, 29701, 29733, 29781, 29814, 29852, 29882, 29916, 29951, 
29983, 30014, 30044, 30079, 30109, 30140, 30171, 30201, 30231, 30261, 30292, 30328, 30358, 30388, 30418, 30449, 
30489, 30519, 30554, 30585, 30623, 30653, 30683, 30717, 30748, 30778, 30813, 30844, 30874, 30905, 30937, 30967, 
31006, 31036, 31066, 31096, 31130, 31161, 31192, 31224, 31255, 31287, 31319, 31350, 31381, 31411, 31446, 31481, 
31513, 31544, 31574, 31605, 31636, 31668, 31699, 31729, 31759, 31800, 31859, 31889, 31921, 31957, 31992, 32032, 
32110, 32166, 32315, 32386, 32416, 32446, 32476, 32506, 32536, 32566, 32596, 32626, 32660, 32690, 32724, 32755, 
32786, 32817, 32850, 32881, 32915, 32945, 32982, 33012, 33042, 33073, 33104, 33134, 33167, 33203, 33251, 33281, 
33311, 33342, 33375, 33405, 33436, 33469, 33499, 33529, 33559, 33589, 33620, 33655, 33688, 33718, 33748, 33778, 
33809, 33841, 33873, 33905, 33943, 33976, 34006, 34044, 34074, 34104, 34134, 34164, 34203, 34233, 34268, 34299, 
34343, 34381, 34411, 34442, 34472, 34502, 34532, 34562, 34593, 34623, 34656, 34686, 34719, 34749, 34779, 34809, 
34843, 34873, 34903, 34935, 34966, 34999, 35029, 35059, 35090, 35120, 35166, 35199, 35265, 35299, 35335, 35390, 
35449, 35591, 35622, 35686, 35744, 35774, 35804, 35834, 35864, 35894, 35925, 35955, 35988, 36125, 36155, 36185, 
36215, 36255, 36286, 36317, 36347, 36381, 36413, 36454, 36485, 36523, 36558, 36710, 36740, 36771, 36801, 36831, 
36861, 36891, 36923, 36955, 36989, 37019, 37049, 37079, 37112, 37145, 37177, 37207, 37237, 37274, 37306, 37340, 
37492, 37550, 37694, 37738, 37775, 37834, 37950, 37995, 38025, 38055, 38085, 38115, 38145, 38175, 38206, 38236, 
38271, 38376, 38406, 38442, 38472, 38503, 38533, 38567, 38597, 38632, 38662, 38698, 38738, 38771, 38801, 38831, 
38886, 39029, 39059, 39118, 39181, 39214, 39252, 39282, 39312, 39532, 39562, 39592, 39627, 39659, 39695, 39727, 
39757, 40060, 40090, 40120, 40150, 40479, 40509, 40539, 40574, 40605, 40635, 40667, 40697, 40727, 40759, 40831, 
40863, 65281, 65311, 65341, 65371, 65504
            });

        testBytes = theseBytes(new int[]{
0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 
16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 
32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 
48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 
64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 
80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 
96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 
112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 
161, 232, 161, 236, 161, 167, 161, 227, 161, 192, 161, 193, 168, 164, 168, 162, 
168, 168, 168, 166, 168, 186, 168, 172, 168, 170, 168, 176, 168, 174, 161, 194, 
168, 180, 168, 178, 168, 185, 168, 161, 168, 165, 168, 173, 168, 177, 168, 163, 
161, 166, 166, 161, 166, 193, 167, 167, 167, 177, 167, 223, 161, 170, 161, 229, 
161, 230, 162, 241, 161, 251, 161, 202, 161, 196, 161, 214, 161, 218, 161, 209, 
161, 208, 162, 217, 162, 207, 169, 164, 169, 194, 169, 224, 161, 246, 161, 244, 
161, 239, 161, 226, 161, 161, 164, 161, 164, 191, 164, 221, 165, 161, 165, 191, 
165, 221, 161, 164, 168, 217, 162, 229, 210, 187, 216, 169, 192, 246, 216, 191, 
199, 172, 216, 189, 210, 218, 217, 218, 200, 206, 187, 239, 201, 236, 217, 162, 
217, 165, 217, 176, 199, 214, 203, 215, 217, 186, 204, 200, 213, 174, 215, 246, 
217, 205, 180, 246, 207, 241, 217, 212, 217, 217, 182, 249, 190, 164, 188, 189, 
185, 218, 190, 187, 188, 184, 212, 228, 179, 245, 191, 204, 216, 224, 216, 226, 
193, 166, 219, 192, 196, 188, 185, 180, 212, 209, 210, 189, 178, 169, 208, 182, 
192, 229, 219, 204, 202, 229, 202, 183, 205, 194, 203, 177, 196, 197, 223, 201, 
211, 189, 223, 210, 223, 223, 196, 196, 176, 166, 223, 243, 201, 204, 198, 161, 
206, 185, 208, 250, 208, 225, 203, 195, 224, 210, 224, 189, 186, 217, 224, 222, 
224, 233, 207, 249, 196, 210, 224, 241, 224, 246, 212, 218, 190, 249, 204, 185, 
194, 162, 185, 184, 185, 161, 178, 186, 220, 165, 220, 166, 220, 168, 220, 170, 
190, 179, 196, 171, 219, 214, 201, 249, 207, 196, 216, 178, 183, 220, 197, 174, 
182, 202, 230, 167, 189, 227, 230, 177, 196, 239, 200, 162, 230, 188, 211, 164, 
195, 189, 230, 199, 230, 200, 230, 205, 230, 212, 217, 248, 230, 222, 229, 238, 
186, 234, 212, 215, 195, 194, 229, 188, 208, 161, 190, 205, 198, 193, 229, 248, 
225, 167, 225, 182, 225, 190, 211, 248, 198, 233, 213, 184, 225, 210, 225, 212, 
225, 215, 225, 218, 231, 221, 217, 227, 178, 175, 224, 253, 225, 164, 231, 219, 
195, 237, 191, 181, 193, 206, 219, 200, 185, 173, 200, 245, 229, 230, 213, 195, 
208, 236, 206, 162, 188, 201, 226, 236, 226, 247, 212, 185, 193, 181, 182, 247, 
207, 164, 196, 250, 227, 176, 181, 235, 237, 169, 183, 223, 180, 200, 187, 219, 
177, 239, 237, 172, 208, 184, 197, 179, 237, 176, 234, 174, 236, 231, 191, 219, 
179, 173, 199, 192, 183, 247, 194, 163, 179, 214, 208, 174, 205, 236, 192, 204, 
207, 198, 194, 211, 222, 242, 222, 238, 192, 191, 222, 246, 208, 175, 213, 170, 
195, 254, 222, 254, 196, 236, 223, 168, 197, 202, 223, 172, 185, 202, 201, 162, 
206, 196, 179, 226, 236, 185, 188, 200, 234, 192, 208, 199, 207, 212, 234, 201, 
193, 192, 234, 212, 234, 213, 234, 215, 194, 252, 205, 251, 187, 250, 182, 197, 
232, 204, 195, 182, 232, 219, 232, 207, 232, 223, 232, 233, 184, 241, 232, 226, 
193, 186, 201, 210, 188, 236, 204, 196, 233, 164, 232, 252, 180, 170, 233, 172, 
194, 165, 233, 187, 233, 189, 188, 247, 178, 219, 213, 193, 233, 215, 233, 211, 
233, 214, 233, 222, 199, 183, 191, 238, 214, 185, 233, 226, 233, 235, 181, 238, 
213, 177, 235, 169, 235, 179, 199, 243, 179, 216, 183, 218, 185, 181, 185, 193, 
183, 186, 227, 248, 228, 168, 228, 161, 213, 227, 186, 163, 204, 233, 186, 173, 
196, 215, 187, 236, 228, 201, 191, 202, 228, 212, 205, 229, 193, 239, 196, 231, 
185, 246, 228, 239, 196, 174, 209, 250, 199, 177, 228, 253, 192, 189, 188, 164, 
229, 168, 198, 217, 229, 175, 229, 177, 215, 198, 236, 191, 236, 194, 192, 211, 
205, 233, 187, 192, 200, 187, 236, 207, 236, 213, 236, 214, 236, 228, 236, 219, 
177, 172, 236, 224, 198, 172, 234, 243, 234, 247, 200, 174, 225, 243, 182, 192, 
226, 165, 208, 201, 226, 176, 226, 179, 205, 245, 205, 230, 231, 236, 231, 242, 
192, 197, 231, 250, 232, 166, 209, 254, 232, 171, 232, 183, 185, 207, 234, 179, 
201, 250, 231, 222, 208, 243, 231, 220, 240, 222, 204, 219, 205, 180, 177, 212, 
240, 249, 241, 169, 241, 175, 185, 239, 205, 238, 241, 229, 186, 208, 237, 236, 
237, 244, 190, 236, 237, 253, 182, 195, 238, 169, 238, 173, 195, 172, 206, 249, 
237, 191, 237, 194, 207, 245, 197, 240, 237, 213, 237, 219, 237, 222, 193, 215, 
237, 230, 192, 241, 236, 241, 187, 246, 236, 250, 211, 237, 195, 216, 189, 213, 
176, 222, 240, 162, 203, 235, 190, 191, 241, 187, 241, 193, 202, 250, 214, 241, 
243, 207, 243, 205, 178, 223, 243, 219, 178, 173, 243, 240, 194, 168, 192, 233, 
243, 252, 244, 164, 195, 215, 244, 206, 193, 187, 184, 226, 244, 233, 203, 216, 
208, 245, 244, 235, 247, 227, 215, 235, 190, 192, 231, 163, 194, 231, 231, 184, 
212, 181, 243, 190, 216, 232, 202, 240, 184, 225, 244, 203, 207, 232, 244, 232, 
241, 231, 241, 242, 241, 248, 241, 250, 235, 193, 235, 197, 197, 214, 235, 216, 
235, 223, 205, 209, 184, 175, 200, 249, 184, 224, 201, 197, 179, 188, 244, 168, 
214, 219, 244, 184, 244, 190, 220, 180, 206, 223, 209, 191, 191, 193, 198, 187, 
220, 248, 210, 240, 192, 243, 186, 201, 221, 183, 221, 178, 221, 202, 183, 198, 
221, 200, 221, 230, 221, 215, 180, 208, 221, 245, 213, 244, 177, 205, 222, 164, 
206, 181, 221, 250, 222, 161, 212, 204, 209, 166, 222, 183, 222, 188, 212, 229, 
222, 190, 242, 174, 179, 230, 206, 195, 242, 182, 199, 249, 184, 242, 183, 228, 
192, 175, 242, 234, 242, 240, 242, 238, 195, 248, 243, 174, 243, 178, 208, 183, 
243, 186, 243, 188, 189, 214, 214, 212, 208, 228, 241, 202, 212, 163, 201, 209, 
176, 253, 229, 189, 244, 197, 206, 247, 188, 251, 189, 226, 217, 234, 246, 164, 
213, 178, 229, 192, 246, 165, 190, 175, 218, 165, 201, 232, 218, 183, 197, 181, 
218, 209, 218, 223, 245, 185, 225, 217, 245, 249, 177, 180, 234, 221, 234, 231, 
198, 240, 204, 203, 214, 186, 190, 224, 245, 210, 245, 215, 245, 225, 177, 196, 
245, 238, 201, 237, 234, 166, 179, 181, 233, 252, 192, 177, 199, 168, 179, 217, 
215, 183, 185, 228, 194, 223, 229, 222, 229, 225, 218, 246, 215, 222, 219, 173, 
181, 166, 177, 201, 219, 184, 208, 239, 189, 205, 195, 209, 245, 184, 184, 170, 
188, 248, 246, 199, 246, 201, 246, 202, 246, 204, 246, 203, 246, 205, 246, 206, 
182, 164, 190, 251, 199, 166, 207, 179, 203, 248, 239, 191, 239, 204, 190, 181, 
179, 164, 195, 197, 227, 207, 218, 230, 179, 194, 218, 237, 211, 231, 203, 237, 
209, 197, 211, 234, 246, 170, 246, 175, 199, 224, 189, 249, 247, 178, 247, 181, 
206, 164, 210, 179, 205, 199, 183, 231, 247, 208, 247, 209, 247, 211, 203, 199, 
226, 202, 194, 237, 230, 234, 185, 199, 247, 197, 247, 216, 247, 221, 219, 203, 
247, 205, 211, 227, 246, 221, 190, 168, 177, 238, 196, 241, 184, 235, 240, 204, 
245, 186, 247, 234, 194, 233, 247, 236, 237, 233, 216, 187, 247, 251, 179, 221, 
185, 234, 163, 161, 163, 191, 163, 221, 163, 251, 161, 233
            });

        super.setUp();
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "functionalCoDec_REPR",
        args = {}
    )
    @Override
    public void test_CodecDynamic() throws CharacterCodingException {
        super.test_CodecDynamic();
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "functionalCoDec_REPR",
        args = {}
    )
    @Override
    public void test_Decode() throws CharacterCodingException {
        super.test_Decode();
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "functionalCoDec_REPR",
        args = {}
    )
    @Override
    public void test_Encode() throws CharacterCodingException {
        super.test_Encode();
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "functionalCoDec_REPR",
        args = {}
    )
    @Override
    public void test_nameMatch() {
        super.test_nameMatch();
    }

}
