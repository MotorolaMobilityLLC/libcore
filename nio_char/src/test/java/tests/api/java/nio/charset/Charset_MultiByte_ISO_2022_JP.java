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

@TestTargetClass(targets.Charsets.ISO_2022_JP.class)

public class Charset_MultiByte_ISO_2022_JP extends Charset_AbstractTest {

    @Override
    protected void setUp() throws Exception {
        charsetName = "ISO-2022-JP";

        testChars = theseChars(new int[]{
32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 
48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 
64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 
80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 
96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 
112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 
162, 163, 165, 167, 168, 172, 176, 177, 180, 182, 215, 247, 913, 914, 924, 934, 
945, 955, 965, 1025, 1040, 1050, 1060, 1070, 1080, 1090, 1100, 8208, 8220, 8230, 8240, 8251, 
8451, 8491, 8592, 8658, 8704, 8715, 8730, 8743, 8756, 8786, 8800, 8810, 8834, 8869, 8978, 9472, 
9484, 9495, 9507, 9519, 9531, 9547, 9632, 9650, 9660, 9670, 9711, 9733, 9792, 9834, 12288, 12298, 
12308, 12353, 12363, 12373, 12383, 12393, 12403, 12413, 12423, 12433, 12443, 12453, 12463, 12473, 12483, 12493, 
12503, 12513, 12523, 12533, 19968, 19978, 19988, 19998, 20010, 20022, 20034, 20045, 20055, 20066, 20081, 20094, 
20104, 20114, 20124, 20134, 20144, 20154, 20164, 20174, 20184, 20195, 20205, 20215, 20225, 20237, 20250, 20271, 
20282, 20294, 20304, 20314, 20329, 20339, 20351, 20363, 20374, 20384, 20395, 20405, 20415, 20426, 20436, 20447, 
20462, 20472, 20485, 20495, 20505, 20515, 20525, 20537, 20547, 20559, 20570, 20581, 20594, 20605, 20621, 20632, 
20652, 20663, 20674, 20685, 20698, 20709, 20725, 20736, 20754, 20767, 20778, 20791, 20801, 20811, 20826, 20837, 
20849, 20860, 20870, 20880, 20896, 20906, 20916, 20932, 20950, 20960, 20970, 20981, 20992, 21002, 21012, 21028, 
21038, 21048, 21059, 21069, 21083, 21093, 21103, 21117, 21127, 21137, 21147, 21161, 21172, 21182, 21193, 21205, 
21215, 21234, 21246, 21256, 21269, 21280, 21290, 21304, 21315, 21325, 21335, 21350, 21360, 21371, 21398, 21408, 
21421, 21435, 21448, 21460, 21471, 21481, 21491, 21505, 21515, 21531, 21542, 21558, 21568, 21578, 21599, 21610, 
21621, 21632, 21643, 21666, 21676, 21688, 21698, 21720, 21730, 21741, 21754, 21764, 21775, 21806, 21816, 21828, 
21839, 21852, 21883, 21895, 21912, 21927, 21942, 21956, 21972, 21983, 22007, 22022, 22036, 22057, 22068, 22082, 
22092, 22107, 22120, 22132, 22144, 22154, 22164, 22176, 22190, 22204, 22216, 22227, 22238, 22254, 22265, 22275, 
22285, 22296, 22310, 22320, 22331, 22343, 22353, 22369, 22399, 22409, 22419, 22432, 22442, 22464, 22475, 22486, 
22496, 22516, 22528, 22538, 22549, 22561, 22575, 22586, 22602, 22612, 22622, 22633, 22645, 22659, 22675, 22687, 
22699, 22712, 22725, 22737, 22748, 22763, 22775, 22786, 22799, 22809, 22821, 22833, 22846, 22856, 22868, 22880, 
22890, 22900, 22913, 22925, 22937, 22947, 22962, 22974, 22985, 22995, 23013, 23030, 23041, 23057, 23068, 23081, 
23093, 23104, 23130, 23142, 23167, 23186, 23228, 23241, 23254, 23265, 23290, 23305, 23318, 23330, 23340, 23350, 
23360, 23376, 23386, 23396, 23408, 23418, 23429, 23439, 23449, 23459, 23470, 23480, 23490, 23500, 23515, 23525, 
23536, 23546, 23556, 23566, 23578, 23588, 23601, 23611, 23621, 23631, 23646, 23660, 23670, 23692, 23713, 23723, 
23734, 23749, 23769, 23784, 23798, 23815, 23825, 23835, 23849, 23883, 23900, 23913, 23923, 23938, 23948, 23965, 
23980, 23991, 24009, 24019, 24029, 24039, 24049, 24059, 24070, 24081, 24091, 24101, 24111, 24125, 24135, 24148, 
24159, 24178, 24188, 24199, 24213, 24224, 24235, 24245, 24257, 24271, 24282, 24296, 24307, 24318, 24329, 24339, 
24351, 24361, 24373, 24385, 24396, 24406, 24417, 24427, 24439, 24449, 24459, 24471, 24481, 24493, 24505, 24515, 
24525, 24535, 24548, 24560, 24571, 24590, 24601, 24613, 24623, 24634, 24646, 24656, 24666, 24676, 24687, 24705, 
24715, 24726, 24736, 24746, 24756, 24773, 24785, 24796, 24807, 24817, 24827, 24838, 24853, 24863, 24876, 24892, 
24903, 24915, 24925, 24935, 24945, 24958, 24970, 24980, 24996, 25006, 25018, 25030, 25040, 25059, 25074, 25084, 
25096, 25106, 25117, 25130, 25140, 25151, 25161, 25171, 25182, 25192, 25206, 25216, 25226, 25236, 25246, 25259, 
25269, 25282, 25292, 25303, 25313, 25324, 25334, 25345, 25356, 25369, 25383, 25402, 25417, 25429, 25447, 25458, 
25472, 25484, 25494, 25504, 25514, 25524, 25534, 25545, 25558, 25569, 25582, 25594, 25606, 25619, 25638, 25652, 
25662, 25678, 25688, 25703, 25718, 25731, 25746, 25758, 25769, 25785, 25797, 25810, 25824, 25836, 25846, 25856, 
25880, 25891, 25903, 25913, 25925, 25935, 25945, 25955, 25968, 25986, 25996, 26007, 26017, 26027, 26039, 26049, 
26059, 26071, 26081, 26092, 26106, 26118, 26131, 26143, 26157, 26172, 26185, 26205, 26215, 26228, 26241, 26254, 
26264, 26274, 26286, 26296, 26308, 26326, 26336, 26352, 26364, 26376, 26388, 26398, 26408, 26420, 26431, 26441, 
26451, 26462, 26474, 26485, 26495, 26505, 26517, 26528, 26543, 26553, 26564, 26574, 26584, 26594, 26604, 26619, 
26643, 26654, 26665, 26676, 26688, 26701, 26713, 26723, 26740, 26750, 26765, 26775, 26786, 26797, 26809, 26820, 
26834, 26847, 26862, 26873, 26884, 26894, 26905, 26915, 26928, 26941, 26954, 26964, 26974, 26986, 26996, 27006, 
27018, 27028, 27040, 27054, 27067, 27079, 27091, 27101, 27111, 27122, 27133, 27146, 27156, 27166, 27177, 27189, 
27204, 27224, 27234, 27250, 27263, 27277, 27287, 27298, 27308, 27320, 27330, 27345, 27355, 27368, 27386, 27396, 
27410, 27421, 27431, 27442, 27453, 27463, 27475, 27487, 27497, 27507, 27519, 27529, 27541, 27556, 27567, 27578, 
27589, 27602, 27615, 27627, 27656, 27667, 27683, 27700, 27710, 27726, 27738, 27752, 27762, 27773, 27784, 27794, 
27809, 27819, 27832, 27844, 27859, 27869, 27880, 27891, 27915, 27927, 27941, 27954, 27965, 27993, 28003, 28014, 
28024, 28037, 28051, 28079, 28092, 28102, 28113, 28126, 28136, 28147, 28165, 28179, 28189, 28201, 28216, 28227, 
28237, 28248, 28263, 28274, 28286, 28300, 28310, 28322, 28335, 28346, 28356, 28369, 28381, 28396, 28407, 28417, 
28431, 28448, 28459, 28472, 28485, 28500, 28511, 28525, 28536, 28546, 28558, 28577, 28593, 28608, 28628, 28639, 
28651, 28662, 28673, 28683, 28693, 28703, 28716, 28734, 28748, 28760, 28771, 28783, 28796, 28809, 28825, 28844, 
28856, 28872, 28889, 28913, 28925, 28937, 28948, 28961, 28982, 29001, 29013, 29026, 29036, 29053, 29064, 29076, 
29087, 29100, 29113, 29123, 29134, 29151, 29164, 29177, 29190, 29200, 29211, 29224, 29234, 29244, 29254, 29266, 
29277, 29287, 29298, 29309, 29319, 29330, 29344, 29356, 29366, 29378, 29390, 29401, 29417, 29431, 29450, 29462, 
29477, 29487, 29502, 29518, 29539, 29552, 29562, 29572, 29590, 29609, 29619, 29632, 29642, 29662, 29674, 29688, 
29699, 29730, 29746, 29759, 29781, 29791, 29801, 29811, 29822, 29835, 29854, 29872, 29885, 29898, 29908, 29920, 
29934, 29944, 29955, 29965, 29976, 29987, 30000, 30010, 30020, 30031, 30041, 30052, 30064, 30079, 30089, 30100, 
30115, 30129, 30140, 30151, 30162, 30174, 30185, 30195, 30206, 30217, 30239, 30256, 30267, 30278, 30290, 30300, 
30311, 30322, 30332, 30342, 30352, 30362, 30382, 30392, 30402, 30413, 30423, 30433, 30446, 30456, 30468, 30491, 
30501, 30519, 30535, 30554, 30565, 30585, 30603, 30622, 30636, 30646, 30663, 30679, 30690, 30701, 30716, 30732, 
30752, 30770, 30783, 30813, 30827, 30844, 30854, 30865, 30883, 30895, 30906, 30917, 30928, 30938, 30951, 30964, 
30977, 30990, 31001, 31014, 31034, 31047, 31059, 31069, 31080, 31095, 31105, 31117, 31133, 31143, 31155, 31165, 
31177, 31189, 31199, 31209, 31227, 31240, 31252, 31263, 31278, 31291, 31302, 31312, 31329, 31339, 31350, 31361, 
31378, 31391, 31401, 31414, 31427, 31437, 31449, 31459, 31469, 31480, 31490, 31503, 31513, 31525, 31539, 31557, 
31567, 31581, 31591, 31601, 31622, 31634, 31644, 31658, 31668, 31680, 31691, 31709, 31721, 31731, 31744, 31757, 
31767, 31777, 31787, 31799, 31811, 31821, 31832, 31844, 31859, 31870, 31881, 31893, 31903, 31915, 31929, 31941, 
31954, 31964, 31975, 31986, 31998, 32010, 32020, 32032, 32043, 32053, 32063, 32075, 32086, 32097, 32110, 32121, 
32137, 32147, 32159, 32171, 32181, 32191, 32202, 32213, 32224, 32236, 32251, 32261, 32274, 32286, 32299, 32309, 
32321, 32331, 32341, 32358, 32368, 32379, 32392, 32402, 32412, 32566, 32581, 32592, 32607, 32617, 32629, 32642, 
32652, 32666, 32676, 32686, 32696, 32709, 32722, 32736, 32747, 32761, 32771, 32784, 32796, 32808, 32819, 32829, 
32842, 32854, 32865, 32879, 32889, 32900, 32915, 32925, 32937, 32948, 32963, 32974, 32985, 32996, 33007, 33020, 
33030, 33050, 33065, 33075, 33086, 33099, 33109, 33119, 33131, 33144, 33154, 33167, 33178, 33188, 33200, 33210, 
33222, 33233, 33247, 33258, 33268, 33278, 33288, 33298, 33308, 33321, 33331, 33344, 33368, 33378, 33390, 33400, 
33419, 33433, 33445, 33455, 33465, 33477, 33489, 33499, 33509, 33521, 33531, 33541, 33558, 33571, 33583, 33593, 
33605, 33615, 33651, 33669, 33683, 33694, 33704, 33717, 33729, 33740, 33750, 33760, 33771, 33783, 33795, 33805, 
33824, 33834, 33845, 33862, 33879, 33889, 33899, 33909, 33922, 33936, 33948, 33965, 33976, 33988, 34000, 34010, 
34028, 34044, 34054, 34065, 34079, 34092, 34109, 34120, 34133, 34147, 34157, 34167, 34180, 34192, 34203, 34214, 
34233, 34249, 34261, 34276, 34295, 34306, 34323, 34338, 34349, 34367, 34381, 34394, 34407, 34417, 34427, 34442, 
34453, 34467, 34479, 34500, 34510, 34521, 34532, 34542, 34552, 34562, 34573, 34584, 34597, 34612, 34623, 34633, 
34643, 34655, 34666, 34676, 34687, 34701, 34719, 34731, 34746, 34756, 34768, 34784, 34799, 34809, 34821, 34831, 
34849, 34865, 34875, 34886, 34898, 34909, 34920, 34930, 34941, 34952, 34962, 34974, 34987, 34997, 35007, 35023, 
35033, 35048, 35058, 35068, 35079, 35090, 35101, 35114, 35126, 35137, 35148, 35158, 35168, 35178, 35188, 35198, 
35208, 35219, 35233, 35244, 35258, 35282, 35292, 35302, 35316, 35328, 35338, 35350, 35363, 35373, 35386, 35398, 
35408, 35419, 35430, 35440, 35452, 35463, 35473, 35486, 35496, 35506, 35516, 35527, 35538, 35548, 35558, 35569, 
35582, 35596, 35606, 35616, 35627, 35641, 35657, 35670, 35686, 35696, 35709, 35722, 35734, 35895, 35905, 35916, 
35930, 35946, 35960, 35970, 35980, 35992, 36002, 36012, 36022, 36032, 36042, 36058, 36068, 36090, 36100, 36111, 
36196, 36208, 36225, 36249, 36259, 36275, 36286, 36299, 36310, 36321, 36331, 36341, 36351, 36361, 36381, 36394, 
36404, 36418, 36428, 36441, 36451, 36466, 36476, 36487, 36497, 36513, 36523, 36542, 36552, 36562, 36575, 36587, 
36600, 36611, 36626, 36636, 36646, 36659, 36670, 36681, 36695, 36705, 36763, 36775, 36785, 36795, 36805, 36817, 
36834, 36845, 36855, 36865, 36875, 36885, 36895, 36910, 36920, 36930, 36941, 36952, 36963, 36973, 36983, 36993, 
37007, 37027, 37039, 37057, 37070, 37083, 37096, 37109, 37122, 37138, 37165, 37193, 37204, 37218, 37228, 37239, 
37250, 37261, 37271, 37282, 37295, 37306, 37318, 37328, 37339, 37350, 37365, 37375, 37389, 37406, 37417, 37428, 
37439, 37449, 37463, 37474, 37489, 37502, 37521, 37531, 37549, 37559, 37583, 37604, 37618, 37628, 37638, 37648, 
37658, 37670, 37682, 37700, 37716, 37728, 37740, 37756, 37772, 37782, 37799, 37817, 37827, 37840, 37853, 37864, 
37891, 37904, 37914, 37931, 37941, 37953, 37969, 37979, 37994, 38005, 38015, 38263, 38274, 38287, 38297, 38307, 
38317, 38329, 38339, 38349, 38360, 38370, 38428, 38440, 38450, 38463, 38475, 38491, 38501, 38512, 38522, 38533, 
38543, 38553, 38563, 38576, 38587, 38597, 38609, 38619, 38632, 38642, 38656, 38666, 38678, 38692, 38704, 38717, 
38728, 38738, 38748, 38758, 38769, 38780, 38790, 38800, 38812, 38822, 38835, 38851, 38867, 38893, 38907, 38917, 
38927, 38938, 38948, 38964, 38982, 38996, 39006, 39019, 39080, 39094, 39107, 39131, 39145, 39156, 39166, 39177, 
39187, 39197, 39208, 39229, 39241, 39253, 39318, 39333, 39347, 39361, 39376, 39387, 39405, 39416, 39429, 39439, 
39449, 39464, 39479, 39490, 39501, 39511, 39522, 39592, 39608, 39620, 39631, 39646, 39658, 39668, 39686, 39704, 
39714, 39726, 39739, 39749, 39759, 39770, 39791, 39811, 39822, 39839, 39850, 39860, 39872, 39882, 39892, 39905, 
39920, 39940, 39952, 39963, 39973, 39983, 39993, 40006, 40018, 40032, 40054, 40165, 40176, 40195, 40206, 40219, 
40230, 40251, 40262, 40272, 40284, 40300, 40314, 40327, 40346, 40356, 40367, 40378, 40388, 40399, 40409, 40422, 
40434, 40445, 40474, 40565, 40575, 40587, 40597, 40607, 40617, 40632, 40644, 40654, 40664, 40677, 40687, 40697, 
40711, 40723, 40736, 40748, 40763, 40778, 40788, 40799, 40810, 40822, 40845, 40860, 65281, 65291, 65301, 65311, 
65321, 65331, 65341, 65351, 65361, 65371, 65381, 65391, 65401, 65411, 65421, 65431, 65507
            });

        testBytes = theseBytes(new int[]{
32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 
48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 
64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 
80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 
96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 
112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 
27, 36, 66, 33, 113, 33, 114, 27, 40, 74, 92, 27, 36, 66, 33, 120, 
33, 47, 34, 76, 33, 107, 33, 94, 33, 45, 34, 121, 33, 95, 33, 96, 
38, 33, 38, 34, 38, 44, 38, 53, 38, 65, 38, 75, 38, 84, 39, 39, 
39, 33, 39, 44, 39, 54, 39, 64, 39, 90, 39, 100, 39, 110, 33, 62, 
33, 72, 33, 68, 34, 115, 34, 40, 33, 110, 34, 114, 34, 43, 34, 77, 
34, 79, 34, 59, 34, 101, 34, 74, 33, 104, 34, 98, 33, 98, 34, 99, 
34, 62, 34, 93, 34, 94, 40, 33, 40, 35, 40, 49, 40, 50, 40, 56, 
40, 53, 40, 54, 34, 35, 34, 37, 34, 39, 34, 33, 34, 126, 33, 122, 
33, 106, 34, 118, 33, 33, 33, 84, 33, 76, 36, 33, 36, 43, 36, 53, 
36, 63, 36, 73, 36, 83, 36, 93, 36, 103, 36, 113, 33, 43, 37, 37, 
37, 47, 37, 57, 37, 67, 37, 77, 37, 87, 37, 97, 37, 107, 37, 117, 
48, 108, 62, 101, 51, 110, 62, 103, 80, 36, 80, 38, 80, 41, 70, 99, 
62, 104, 86, 38, 77, 112, 52, 37, 77, 61, 56, 95, 48, 33, 75, 114, 
80, 55, 63, 77, 80, 60, 80, 58, 73, 85, 66, 101, 80, 65, 80, 67, 
52, 107, 56, 96, 50, 113, 71, 108, 59, 71, 67, 34, 58, 52, 80, 69, 
80, 80, 50, 66, 59, 72, 78, 99, 80, 85, 54, 34, 85, 37, 63, 47, 
74, 88, 61, 83, 80, 87, 80, 88, 61, 36, 74, 112, 80, 102, 96, 71, 
56, 117, 74, 111, 79, 65, 55, 112, 80, 112, 74, 80, 80, 118, 55, 114, 
60, 69, 53, 54, 75, 53, 59, 49, 58, 69, 61, 125, 81, 36, 70, 47, 
78, 61, 81, 39, 81, 44, 53, 55, 60, 116, 61, 126, 77, 37, 81, 54, 
48, 116, 57, 110, 69, 94, 70, 126, 54, 38, 55, 115, 49, 95, 102, 110, 
52, 39, 81, 81, 58, 99, 64, 40, 82, 69, 81, 92, 70, 100, 81, 97, 
69, 97, 52, 41, 81, 101, 72, 61, 81, 105, 55, 116, 68, 102, 65, 48, 
57, 100, 71, 109, 73, 123, 81, 119, 55, 96, 81, 125, 78, 79, 61, 117, 
79, 43, 51, 47, 74, 89, 70, 48, 74, 103, 55, 46, 56, 123, 82, 51, 
82, 56, 62, 34, 72, 91, 82, 62, 64, 105, 82, 68, 70, 110, 55, 53, 
48, 117, 82, 74, 82, 77, 82, 78, 49, 94, 53, 110, 75, 116, 61, 71, 
82, 87, 67, 33, 49, 38, 82, 94, 49, 37, 55, 47, 72, 93, 82, 101, 
82, 96, 57, 112, 82, 108, 60, 118, 82, 106, 82, 114, 58, 112, 82, 120, 
82, 123, 82, 121, 83, 34, 83, 38, 83, 47, 83, 45, 83, 46, 49, 52, 
83, 41, 83, 52, 83, 57, 66, 111, 76, 100, 83, 53, 83, 65, 83, 73, 
83, 67, 55, 118, 49, 68, 83, 78, 83, 81, 83, 77, 83, 83, 67, 50, 
83, 82, 50, 94, 83, 92, 49, 61, 65, 57, 51, 122, 52, 111, 74, 46, 
83, 102, 83, 103, 83, 105, 83, 104, 83, 108, 83, 112, 83, 116, 83, 119, 
50, 115, 83, 121, 83, 122, 74, 96, 84, 35, 84, 37, 84, 41, 67, 79, 
84, 45, 54, 81, 57, 35, 84, 51, 84, 52, 84, 53, 84, 54, 84, 55, 
84, 58, 84, 49, 75, 100, 84, 66, 73, 86, 62, 125, 75, 89, 84, 65, 
66, 68, 84, 72, 116, 33, 58, 102, 50, 116, 69, 99, 58, 73, 49, 118, 
63, 80, 54, 45, 74, 104, 84, 82, 84, 83, 84, 87, 84, 89, 84, 91, 
84, 96, 59, 78, 68, 91, 84, 105, 50, 70, 61, 72, 84, 110, 60, 58, 
84, 115, 70, 96, 75, 91, 84, 123, 67, 37, 69, 91, 85, 34, 85, 43, 
76, 47, 85, 38, 85, 39, 62, 42, 59, 80, 64, 43, 49, 56, 48, 40, 
48, 82, 85, 48, 85, 49, 74, 90, 85, 55, 85, 52, 58, 39, 73, 88, 
76, 59, 71, 94, 85, 60, 60, 59, 85, 69, 67, 100, 85, 70, 52, 114, 
85, 74, 62, 110, 68, 92, 85, 78, 85, 82, 59, 82, 85, 85, 56, 73, 
85, 89, 85, 94, 66, 112, 57, 40, 67, 104, 64, 107, 53, 92, 85, 98, 
60, 100, 73, 89, 52, 50, 85, 108, 85, 110, 59, 123, 60, 77, 70, 51, 
62, 48, 76, 96, 61, 34, 63, 44, 53, 111, 86, 34, 66, 48, 86, 36, 
86, 39, 86, 41, 50, 44, 86, 45, 86, 47, 86, 50, 86, 53, 50, 101, 
69, 103, 63, 114, 86, 66, 86, 65, 74, 120, 86, 75, 86, 63, 63, 115, 
86, 77, 86, 81, 86, 58, 86, 83, 86, 84, 86, 88, 86, 90, 86, 92, 
64, 110, 57, 42, 56, 74, 52, 44, 72, 65, 86, 102, 86, 103, 63, 99, 
66, 83, 75, 57, 86, 115, 86, 111, 86, 112, 52, 51, 77, 68, 72, 95, 
68, 108, 86, 121, 56, 75, 48, 67, 86, 122, 86, 126, 87, 36, 87, 40, 
87, 44, 87, 48, 87, 50, 53, 93, 68, 111, 87, 56, 68, 37, 87, 59, 
87, 61, 87, 65, 87, 68, 68, 38, 87, 71, 64, 44, 78, 39, 70, 64, 
56, 102, 87, 84, 69, 48, 63, 52, 71, 38, 59, 86, 87, 88, 88, 45, 
87, 87, 87, 99, 87, 96, 53, 94, 54, 49, 87, 106, 87, 113, 54, 50, 
87, 107, 87, 117, 66, 41, 87, 122, 88, 39, 88, 35, 77, 42, 48, 45, 
88, 44, 62, 112, 79, 71, 64, 75, 87, 124, 88, 61, 88, 60, 88, 52, 
88, 51, 52, 54, 88, 71, 88, 70, 88, 64, 88, 88, 88, 87, 55, 69, 
88, 85, 77, 93, 88, 96, 88, 94, 74, 48, 88, 99, 88, 107, 88, 106, 
50, 123, 88, 112, 68, 40, 88, 118, 88, 121, 50, 124, 108, 67, 89, 35, 
66, 87, 75, 60, 72, 98, 66, 71, 89, 42, 89, 46, 73, 94, 53, 59, 
71, 68, 89, 56, 66, 114, 72, 100, 68, 113, 89, 68, 89, 66, 89, 57, 
51, 72, 51, 103, 59, 34, 59, 125, 89, 72, 53, 115, 89, 75, 68, 114, 
66, 42, 74, 97, 74, 123, 55, 126, 89, 87, 62, 56, 89, 85, 78, 43, 
65, 60, 68, 79, 89, 97, 89, 102, 77, 44, 48, 46, 52, 120, 77, 73, 
89, 108, 89, 109, 89, 110, 89, 107, 58, 113, 89, 119, 69, 38, 89, 116, 
89, 118, 55, 98, 59, 53, 70, 53, 89, 124, 51, 73, 90, 35, 90, 34, 
90, 43, 53, 60, 90, 49, 90, 53, 90, 55, 90, 59, 59, 89, 50, 126, 
56, 78, 73, 82, 54, 53, 59, 54, 63, 116, 90, 76, 73, 76, 69, 77, 
48, 54, 90, 81, 90, 82, 90, 85, 64, 123, 52, 122, 90, 92, 61, 92, 
50, 34, 58, 43, 48, 87, 64, 49, 62, 60, 67, 107, 90, 105, 90, 108, 
90, 110, 64, 50, 54, 71, 90, 117, 90, 120, 68, 42, 74, 107, 91, 34, 
91, 33, 91, 35, 91, 37, 91, 41, 82, 88, 55, 110, 58, 115, 91, 46, 
76, 90, 75, 81, 91, 51, 63, 121, 60, 93, 91, 57, 91, 66, 53, 79, 
72, 68, 91, 62, 75, 109, 79, 72, 56, 79, 91, 84, 74, 65, 91, 85, 
68, 83, 91, 82, 91, 75, 58, 58, 64, 114, 91, 89, 91, 91, 64, 115, 
91, 92, 91, 93, 91, 98, 91, 99, 91, 111, 91, 117, 91, 116, 57, 60, 
62, 63, 91, 104, 91, 110, 52, 126, 75, 64, 69, 111, 63, 57, 92, 42, 
92, 38, 68, 71, 63, 122, 92, 48, 92, 63, 92, 61, 77, 76, 92, 56, 
92, 65, 92, 68, 92, 60, 92, 57, 58, 103, 79, 49, 92, 85, 92, 70, 
92, 80, 116, 34, 92, 74, 92, 79, 92, 97, 92, 96, 65, 101, 92, 104, 
92, 102, 92, 100, 56, 34, 62, 65, 92, 109, 53, 76, 92, 115, 92, 111, 
51, 96, 92, 122, 93, 33, 92, 125, 93, 40, 93, 38, 93, 37, 93, 42, 
54, 123, 72, 39, 93, 50, 77, 115, 93, 51, 49, 53, 50, 36, 77, 95, 
54, 86, 93, 60, 52, 63, 93, 67, 74, 98, 58, 80, 93, 71, 61, 94, 
93, 77, 93, 79, 93, 82, 59, 38, 53, 35, 70, 71, 93, 91, 93, 93, 
93, 97, 93, 98, 93, 102, 63, 101, 72, 69, 72, 70, 49, 120, 93, 113, 
53, 98, 53, 37, 68, 64, 93, 115, 75, 87, 75, 119, 74, 40, 93, 117, 
93, 119, 93, 123, 67, 109, 49, 75, 77, 78, 64, 118, 68, 69, 61, 39, 
94, 42, 94, 54, 94, 49, 73, 98, 63, 59, 94, 58, 94, 50, 51, 54, 
78, 67, 94, 66, 61, 74, 94, 68, 94, 70, 61, 95, 64, 54, 55, 76, 
94, 97, 50, 57, 94, 98, 94, 92, 94, 90, 62, 69, 77, 47, 94, 83, 
79, 81, 72, 46, 61, 96, 48, 110, 94, 106, 69, 46, 94, 107, 51, 106, 
66, 108, 94, 118, 95, 35, 53, 121, 79, 51, 71, 121, 76, 33, 65, 50, 
52, 67, 55, 105, 51, 99, 95, 44, 95, 38, 95, 45, 95, 48, 95, 54, 
69, 67, 55, 99, 95, 62, 95, 59, 77, 116, 111, 105, 95, 71, 95, 68, 
73, 78, 95, 79, 64, 37, 95, 81, 94, 117, 70, 103, 95, 84, 69, 116, 
60, 94, 79, 39, 95, 85, 95, 89, 95, 90, 78, 117, 95, 96, 95, 88, 
95, 98, 95, 97, 49, 107, 76, 53, 65, 51, 78, 123, 95, 102, 95, 105, 
95, 108, 64, 122, 95, 103, 77, 80, 61, 79, 95, 114, 95, 116, 71, 51, 
95, 121, 83, 91, 95, 123, 96, 33, 71, 122, 96, 36, 96, 37, 96, 38, 
96, 42, 96, 44, 96, 47, 68, 45, 76, 70, 75, 82, 64, 55, 56, 35, 
96, 54, 96, 55, 53, 62, 56, 36, 62, 117, 54, 56, 96, 64, 65, 64, 
60, 109, 96, 70, 96, 73, 96, 75, 96, 80, 96, 78, 96, 81, 96, 82, 
61, 67, 96, 88, 96, 92, 56, 60, 54, 106, 52, 97, 96, 94, 96, 97, 
59, 57, 96, 100, 55, 62, 96, 105, 53, 101, 66, 118, 96, 106, 96, 109, 
96, 108, 96, 110, 96, 114, 58, 60, 96, 119, 96, 120, 96, 121, 52, 68, 
60, 37, 96, 123, 96, 125, 97, 33, 97, 37, 97, 41, 97, 44, 97, 48, 
52, 69, 59, 58, 69, 68, 68, 46, 97, 55, 48, 90, 78, 49, 97, 63, 
48, 91, 53, 38, 97, 73, 97, 75, 97, 79, 97, 87, 97, 86, 62, 73, 
97, 90, 97, 93, 65, 105, 97, 101, 97, 94, 97, 104, 97, 105, 97, 112, 
97, 113, 97, 116, 76, 126, 97, 117, 97, 124, 98, 33, 98, 36, 51, 39, 
59, 41, 98, 45, 72, 105, 98, 48, 98, 51, 98, 52, 69, 112, 98, 56, 
76, 92, 65, 106, 98, 64, 98, 70, 98, 68, 98, 71, 98, 73, 98, 74, 
98, 78, 98, 81, 98, 83, 98, 86, 61, 86, 98, 88, 98, 94, 98, 96, 
76, 112, 67, 59, 98, 101, 98, 102, 98, 105, 75, 36, 57, 92, 62, 75, 
78, 50, 98, 111, 98, 110, 72, 106, 98, 114, 98, 117, 51, 78, 98, 124, 
72, 88, 99, 34, 99, 33, 99, 36, 62, 76, 65, 67, 99, 40, 98, 104, 
60, 40, 53, 64, 99, 49, 61, 75, 73, 60, 69, 120, 54, 88, 50, 82, 
99, 53, 99, 54, 99, 58, 54, 89, 99, 61, 99, 62, 65, 69, 67, 97, 
48, 92, 99, 67, 76, 45, 99, 72, 60, 111, 99, 75, 75, 84, 48, 44, 
99, 81, 51, 79, 53, 102, 70, 77, 67, 98, 55, 34, 99, 93, 99, 96, 
51, 118, 99, 103, 99, 107, 61, 87, 99, 113, 81, 63, 99, 115, 99, 116, 
99, 121, 63, 90, 99, 119, 100, 38, 72, 53, 100, 56, 100, 42, 100, 44, 
74, 79, 100, 54, 100, 51, 67, 61, 100, 62, 64, 97, 100, 58, 100, 64, 
100, 65, 100, 74, 100, 71, 100, 77, 100, 76, 52, 74, 100, 84, 100, 83, 
100, 87, 64, 82, 100, 92, 100, 94, 74, 70, 76, 98, 74, 52, 71, 116, 
48, 64, 100, 104, 100, 111, 100, 114, 100, 117, 100, 118, 78, 72, 100, 122, 
53, 106, 101, 36, 61, 99, 65, 71, 59, 103, 101, 42, 101, 41, 101, 38, 
101, 45, 77, 109, 101, 49, 56, 40, 101, 53, 101, 52, 101, 70, 101, 64, 
101, 61, 76, 74, 54, 91, 101, 110, 68, 121, 76, 75, 101, 76, 101, 79, 
101, 83, 60, 74, 75, 37, 101, 92, 72, 75, 55, 82, 65, 54, 101, 90, 
55, 43, 101, 108, 101, 114, 101, 117, 101, 124, 52, 76, 102, 33, 102, 37, 
102, 42, 102, 45, 71, 77, 102, 48, 102, 53, 102, 57, 55, 50, 102, 62, 
102, 64, 102, 66, 61, 44, 63, 105, 52, 101, 102, 75, 57, 77, 66, 81, 
102, 83, 102, 85, 60, 42, 67, 63, 102, 88, 64, 59, 65, 111, 78, 126, 
102, 100, 102, 103, 102, 107, 52, 78, 56, 42, 58, 104, 48, 95, 66, 91, 
102, 114, 48, 125, 102, 120, 103, 38, 64, 72, 53, 83, 102, 123, 71, 62, 
103, 35, 102, 126, 79, 83, 103, 53, 60, 112, 68, 50, 103, 48, 57, 81, 
73, 102, 103, 51, 103, 56, 103, 63, 50, 50, 103, 66, 103, 71, 60, 43, 
67, 87, 103, 76, 54, 61, 80, 48, 61, 88, 103, 85, 103, 88, 103, 89, 
103, 91, 103, 96, 58, 49, 103, 103, 48, 114, 73, 103, 51, 41, 63, 68, 
54, 92, 52, 35, 49, 113, 50, 87, 60, 99, 49, 81, 103, 120, 51, 125, 
104, 35, 104, 52, 104, 42, 104, 39, 104, 41, 49, 65, 104, 58, 104, 46, 
103, 118, 52, 80, 104, 62, 104, 57, 104, 71, 54, 93, 62, 84, 104, 74, 
104, 65, 104, 72, 104, 68, 104, 76, 104, 78, 104, 84, 104, 98, 104, 94, 
67, 120, 73, 114, 104, 89, 48, 42, 104, 92, 61, 47, 73, 71, 104, 90, 
62, 120, 67, 95, 104, 108, 104, 107, 75, 41, 104, 122, 104, 81, 74, 78, 
104, 119, 104, 118, 74, 67, 104, 126, 105, 36, 104, 124, 105, 43, 105, 49, 
71, 118, 105, 51, 105, 45, 65, 38, 105, 55, 105, 52, 105, 57, 70, 35, 
61, 115, 105, 34, 105, 63, 105, 68, 77, 118, 105, 71, 105, 72, 53, 117, 
105, 76, 105, 77, 48, 58, 50, 99, 59, 61, 105, 79, 105, 85, 105, 86, 
51, 66, 51, 63, 72, 58, 72, 90, 66, 125, 75, 42, 105, 107, 67, 88, 
105, 114, 105, 110, 105, 111, 64, 102, 106, 33, 105, 118, 106, 35, 105, 122, 
71, 104, 77, 59, 106, 38, 106, 46, 77, 102, 106, 47, 106, 44, 106, 54, 
106, 52, 51, 42, 106, 36, 106, 55, 106, 66, 106, 67, 106, 71, 61, 48, 
106, 74, 62, 87, 73, 61, 106, 85, 106, 83, 55, 54, 106, 88, 106, 81, 
72, 111, 106, 84, 106, 95, 78, 34, 106, 101, 106, 107, 106, 108, 106, 105, 
74, 35, 75, 43, 106, 124, 106, 116, 106, 121, 106, 118, 106, 123, 50, 40, 
106, 125, 107, 36, 107, 38, 107, 40, 107, 42, 107, 44, 107, 46, 107, 49, 
107, 52, 51, 81, 107, 57, 63, 40, 107, 60, 56, 64, 63, 86, 107, 63, 
55, 109, 64, 95, 63, 71, 107, 73, 58, 62, 107, 71, 59, 110, 53, 77, 
107, 75, 56, 88, 107, 84, 56, 108, 107, 82, 50, 93, 53, 67, 67, 76, 
78, 74, 68, 53, 68, 124, 107, 97, 66, 122, 107, 104, 107, 108, 107, 111, 
107, 115, 54, 96, 107, 122, 107, 123, 55, 89, 53, 68, 108, 37, 90, 78, 
108, 43, 67, 43, 108, 47, 108, 50, 70, 90, 57, 107, 108, 56, 108, 58, 
75, 70, 108, 66, 57, 87, 64, 85, 108, 74, 50, 108, 66, 49, 108, 79, 
108, 77, 108, 81, 108, 83, 108, 87, 64, 86, 65, 118, 108, 99, 108, 100, 
60, 113, 66, 45, 108, 102, 108, 109, 108, 106, 64, 87, 108, 111, 65, 41, 
108, 117, 108, 116, 108, 118, 109, 41, 108, 125, 108, 122, 109, 35, 109, 38, 
109, 42, 109, 45, 109, 46, 109, 48, 109, 54, 109, 58, 63, 72, 109, 63, 
109, 65, 56, 46, 70, 112, 109, 71, 60, 52, 51, 83, 109, 76, 109, 79, 
109, 83, 109, 84, 109, 89, 109, 91, 109, 94, 55, 37, 63, 73, 82, 33, 
63, 43, 68, 84, 63, 87, 54, 97, 109, 107, 69, 51, 76, 66, 65, 119, 
109, 121, 109, 116, 66, 46, 66, 97, 48, 111, 63, 107, 74, 87, 110, 41, 
56, 47, 65, 120, 65, 43, 110, 50, 110, 54, 70, 97, 110, 56, 48, 106, 
79, 58, 110, 62, 73, 116, 77, 57, 110, 63, 110, 64, 69, 34, 70, 83, 
63, 108, 63, 93, 61, 55, 57, 115, 110, 78, 66, 105, 72, 48, 110, 79, 
110, 81, 110, 85, 72, 80, 110, 90, 110, 94, 75, 85, 110, 96, 110, 99, 
70, 95, 110, 98, 111, 79, 78, 107, 110, 111, 110, 107, 110, 105, 72, 45, 
57, 91, 75, 72, 65, 45, 110, 117, 65, 44, 110, 121, 110, 119, 61, 123, 
73, 70, 57, 93, 59, 44, 63, 109, 111, 35, 54, 83, 79, 63, 110, 125, 
68, 87, 111, 41, 55, 45, 111, 42, 51, 121, 58, 63, 51, 59, 111, 49, 
111, 55, 111, 57, 111, 56, 111, 52, 111, 63, 111, 65, 111, 60, 111, 67, 
111, 68, 111, 71, 52, 85, 111, 74, 111, 78, 111, 81, 111, 88, 68, 57, 
111, 89, 49, 60, 111, 95, 51, 85, 111, 99, 111, 102, 111, 106, 111, 107, 
70, 46, 111, 115, 73, 108, 111, 117, 75, 73, 48, 36, 111, 123, 74, 69, 
52, 89, 49, 34, 56, 49, 54, 121, 63, 111, 55, 100, 78, 89, 112, 46, 
64, 73, 50, 109, 59, 40, 63, 119, 49, 43, 49, 64, 60, 123, 78, 110, 
112, 67, 112, 69, 112, 71, 112, 73, 112, 77, 64, 68, 112, 80, 112, 83, 
112, 87, 112, 90, 112, 93, 112, 96, 112, 97, 112, 99, 112, 101, 112, 103, 
52, 90, 112, 108, 49, 36, 57, 96, 112, 114, 55, 91, 112, 117, 49, 80, 
112, 121, 52, 105, 78, 96, 112, 124, 73, 119, 113, 39, 113, 41, 72, 116, 
113, 43, 48, 59, 62, 126, 113, 46, 50, 110, 113, 51, 52, 91, 113, 57, 
113, 60, 113, 67, 60, 115, 113, 70, 67, 90, 71, 125, 67, 115, 113, 75, 
113, 79, 113, 84, 113, 87, 113, 86, 113, 89, 66, 77, 113, 91, 113, 93, 
113, 98, 113, 100, 113, 102, 57, 124, 51, 60, 63, 113, 113, 112, 113, 116, 
72, 49, 113, 124, 114, 34, 114, 35, 114, 38, 114, 45, 100, 120, 76, 37, 
114, 50, 53, 123, 79, 37, 114, 57, 48, 62, 114, 61, 75, 110, 114, 64, 
114, 65, 114, 66, 114, 75, 114, 76, 114, 80, 114, 90, 79, 76, 114, 92, 
114, 93, 48, 115, 51, 111, 114, 99, 75, 112, 114, 102, 114, 104, 68, 59, 
114, 108, 114, 112, 50, 42, 49, 117, 114, 115, 57, 99, 114, 125, 115, 37, 
49, 45, 75, 50, 115, 44, 115, 41, 115, 45, 115, 46, 114, 116, 115, 53, 
115, 49, 115, 55, 115, 57, 115, 60, 79, 73, 115, 63, 115, 64, 115, 67, 
60, 47, 115, 72, 115, 75, 78, 91, 115, 79, 115, 80, 50, 43, 115, 85, 
96, 84, 115, 93, 115, 95, 115, 99, 115, 103, 56, 93, 65, 77, 115, 108, 
73, 33, 115, 110, 115, 112, 115, 114, 115, 120, 115, 123, 78, 54, 115, 125, 
33, 42, 33, 92, 35, 53, 33, 41, 35, 73, 35, 83, 33, 79, 35, 103, 
35, 113, 33, 80, 27, 40, 73, 37, 47, 57, 67, 77, 87, 27, 36, 66, 
33, 49
            });

        super.setUp();
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "functionalCoDec_REPR",
        args = {}
    )
    @KnownFailure("This Characterset is not properly supported in Android!")
    @Override
    public void test_CodecDynamic() throws CharacterCodingException {
        super.test_CodecDynamic();
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "functionalCoDec_REPR",
        args = {}
    )
    @KnownFailure("This Characterset is not properly supported in Android!")
    @Override
    public void test_Decode() throws CharacterCodingException {
        super.test_Decode();
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "functionalCoDec_REPR",
        args = {}
    )
    @KnownFailure("This Characterset is not properly supported in Android!")
    @Override
    public void test_Encode() throws CharacterCodingException {
        super.test_Encode();
    }

}
