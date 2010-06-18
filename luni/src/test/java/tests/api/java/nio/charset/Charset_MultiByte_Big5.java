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

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

/** @hide
 * SEE correspondig_Android test class:
 */

public class Charset_MultiByte_Big5 extends Charset_AbstractTest {

    @Override
    protected void setUp() throws Exception {
        charsetName = "Big5";

        testChars = theseChars(new int[]{
0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63,
64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95,
96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111,
112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127,
162, 163, 165, 167, 176, 177, 183, 215, 247, 711, 713, 729, 913, 923, 933, 945,
955, 965, 1025, 1044, 1059, 1069, 1079, 1089, 1099, 8211, 8221, 8242, 8254, 8451, 8544, 8592,
8730, 8741, 8756, 8786, 8800, 8869, 8895, 9312, 9332, 9472, 9484, 9496, 9508, 9524, 9552, 9566,
9578, 9601, 9611, 9621, 9632, 9650, 9660, 9670, 9698, 9733, 9792, 12288, 12298, 12308, 12318, 12328,
12353, 12363, 12373, 12383, 12393, 12403, 12413, 12423, 12433, 12445, 12455, 12465, 12475, 12485, 12495, 12505,
12515, 12525, 12542, 12552, 12562, 12572, 12582, 12963, 13198, 13212, 13252, 13262, 19968, 19978, 19988, 19998,
20011, 20024, 20034, 20045, 20056, 20073, 20083, 20094, 20104, 20114, 20126, 20136, 20147, 20160, 20170, 20180,
20190, 20200, 20210, 20221, 20232, 20242, 20253, 20268, 20278, 20289, 20300, 20310, 20320, 20330, 20340, 20350,
20360, 20370, 20380, 20398, 20409, 20419, 20429, 20439, 20449, 20460, 20470, 20480, 20491, 20501, 20511, 20521,
20531, 20544, 20554, 20565, 20575, 20585, 20595, 20605, 20615, 20625, 20635, 20652, 20662, 20673, 20683, 20693,
20704, 20714, 20725, 20735, 20745, 20755, 20767, 20777, 20787, 20797, 20807, 20818, 20828, 20839, 20849, 20860,
20871, 20881, 20894, 20906, 20918, 20932, 20942, 20952, 20976, 20986, 20998, 21008, 21020, 21032, 21042, 21057,
21067, 21077, 21087, 21097, 21108, 21119, 21129, 21139, 21151, 21161, 21179, 21191, 21202, 21213, 21225, 21235,
21246, 21256, 21266, 21276, 21290, 21300, 21310, 21320, 21330, 21340, 21350, 21360, 21371, 21386, 21396, 21406,
21420, 21433, 21443, 21453, 21463, 21473, 21483, 21493, 21505, 21515, 21528, 21540, 21550, 21560, 21570, 21582,
21600, 21611, 21621, 21631, 21643, 21653, 21664, 21674, 21686, 21696, 21710, 21726, 21736, 21746, 21756, 21766,
21776, 21786, 21798, 21808, 21819, 21829, 21839, 21852, 21862, 21877, 21887, 21897, 21907, 21917, 21927, 21937,
21947, 21957, 21967, 21977, 21987, 21999, 22009, 22020, 22030, 22043, 22055, 22066, 22077, 22088, 22099, 22110,
22120, 22130, 22142, 22156, 22167, 22181, 22194, 22204, 22214, 22225, 22235, 22245, 22256, 22266, 22276, 22290,
22300, 22312, 22323, 22334, 22345, 22369, 22379, 22389, 22400, 22411, 22421, 22431, 22446, 22456, 22466, 22476,
22492, 22503, 22513, 22523, 22533, 22544, 22555, 22565, 22575, 22585, 22600, 22610, 22621, 22632, 22644, 22654,
22664, 22675, 22685, 22696, 22707, 22717, 22727, 22737, 22747, 22759, 22772, 22782, 22796, 22806, 22816, 22826,
22839, 22852, 22862, 22872, 22882, 22893, 22903, 22913, 22925, 22935, 22945, 22958, 22969, 22979, 22989, 23000,
23011, 23021, 23031, 23041, 23052, 23062, 23072, 23085, 23095, 23105, 23116, 23126, 23136, 23146, 23159, 23171,
23182, 23194, 23205, 23215, 23225, 23236, 23253, 23263, 23273, 23283, 23293, 23303, 23315, 23325, 23335, 23346,
23356, 23367, 23377, 23387, 23397, 23408, 23418, 23428, 23438, 23448, 23458, 23468, 23478, 23488, 23498, 23508,
23518, 23528, 23538, 23553, 23563, 23573, 23583, 23594, 23607, 23617, 23627, 23637, 23648, 23658, 23668, 23678,
23688, 23698, 23709, 23719, 23729, 23750, 23760, 23770, 23784, 23796, 23807, 23819, 23830, 23840, 23854, 23864,
23874, 23884, 23897, 23907, 23919, 23929, 23940, 23954, 23964, 23975, 23985, 23996, 24006, 24017, 24029, 24039,
24049, 24061, 24074, 24084, 24095, 24105, 24115, 24125, 24138, 24148, 24159, 24169, 24179, 24189, 24199, 24213,
24224, 24234, 24244, 24254, 24264, 24274, 24284, 24294, 24305, 24318, 24328, 24338, 24349, 24359, 24369, 24380,
24390, 24404, 24418, 24428, 24438, 24448, 24458, 24470, 24480, 24490, 24501, 24511, 24521, 24532, 24542, 24552,
24563, 24573, 24585, 24595, 24605, 24615, 24626, 24640, 24652, 24664, 24674, 24684, 24703, 24713, 24724, 24735,
24752, 24762, 24772, 24782, 24792, 24802, 24816, 24826, 24836, 24846, 24856, 24867, 24878, 24891, 24901, 24911,
24922, 24933, 24944, 24954, 24969, 24979, 24989, 24999, 25009, 25020, 25030, 25046, 25056, 25066, 25077, 25087,
25097, 25108, 25119, 25129, 25139, 25149, 25159, 25169, 25179, 25189, 25199, 25209, 25219, 25230, 25240, 25256,
25267, 25277, 25287, 25297, 25307, 25323, 25333, 25343, 25353, 25363, 25384, 25394, 25404, 25414, 25424, 25434,
25445, 25455, 25466, 25476, 25486, 25496, 25506, 25516, 25533, 25543, 25554, 25564, 25575, 25585, 25606, 25616,
25626, 25636, 25646, 25657, 25667, 25677, 25688, 25701, 25711, 25721, 25733, 25743, 25753, 25763, 25773, 25787,
25797, 25807, 25817, 25827, 25837, 25847, 25857, 25868, 25878, 25888, 25898, 25910, 25921, 25935, 25945, 25955,
25967, 25977, 25987, 26000, 26011, 26021, 26031, 26041, 26051, 26061, 26071, 26081, 26092, 26106, 26116, 26126,
26140, 26150, 26161, 26177, 26188, 26201, 26212, 26222, 26232, 26244, 26256, 26269, 26280, 26290, 26301, 26311,
26322, 26332, 26342, 26352, 26364, 26376, 26386, 26397, 26407, 26417, 26427, 26437, 26447, 26457, 26474, 26484,
26494, 26505, 26515, 26525, 26542, 26552, 26562, 26572, 26584, 26594, 26604, 26614, 26642, 26652, 26662, 26673,
26683, 26693, 26703, 26731, 26741, 26751, 26761, 26771, 26781, 26791, 26801, 26820, 26830, 26840, 26851, 26862,
26872, 26884, 26894, 26917, 26927, 26937, 26948, 26958, 26968, 26978, 26988, 26998, 27010, 27021, 27031, 27041,
27051, 27061, 27071, 27081, 27091, 27106, 27116, 27126, 27136, 27146, 27156, 27166, 27176, 27186, 27196, 27206,
27216, 27226, 27236, 27247, 27262, 27273, 27283, 27294, 27304, 27315, 27325, 27335, 27345, 27355, 27365, 27375,
27385, 27395, 27407, 27417, 27427, 27437, 27447, 27457, 27467, 27477, 27487, 27498, 27510, 27520, 27530, 27540,
27550, 27562, 27573, 27583, 27593, 27603, 27614, 27624, 27634, 27644, 27654, 27664, 27674, 27684, 27694, 27704,
27714, 27724, 27735, 27745, 27755, 27766, 27776, 27786, 27796, 27819, 27830, 27840, 27850, 27860, 27870, 27880,
27890, 27904, 27914, 27926, 27936, 27946, 27956, 27966, 27992, 28002, 28012, 28022, 28032, 28042, 28052, 28074,
28084, 28094, 28104, 28114, 28124, 28134, 28144, 28154, 28165, 28185, 28195, 28205, 28216, 28227, 28237, 28248,
28258, 28270, 28280, 28296, 28306, 28316, 28326, 28336, 28346, 28356, 28366, 28376, 28395, 28405, 28415, 28425,
28435, 28446, 28457, 28467, 28478, 28494, 28504, 28514, 28524, 28534, 28544, 28555, 28565, 28576, 28586, 28596,
28607, 28617, 28628, 28638, 28648, 28658, 28668, 28678, 28689, 28699, 28710, 28720, 28730, 28740, 28753, 28763,
28773, 28784, 28794, 28804, 28814, 28824, 28836, 28846, 28856, 28869, 28879, 28889, 28900, 28911, 28921, 28932,
28942, 28953, 28963, 28974, 28986, 28996, 29006, 29016, 29026, 29036, 29048, 29058, 29071, 29081, 29092, 29103,
29113, 29123, 29134, 29144, 29154, 29164, 29176, 29186, 29196, 29209, 29219, 29229, 29240, 29250, 29260, 29270,
29280, 29290, 29300, 29310, 29320, 29330, 29341, 29351, 29364, 29375, 29385, 29396, 29407, 29417, 29427, 29437,
29447, 29457, 29467, 29477, 29488, 29498, 29508, 29518, 29528, 29538, 29548, 29558, 29568, 29578, 29588, 29599,
29609, 29619, 29630, 29640, 29650, 29660, 29671, 29684, 29694, 29704, 29718, 29728, 29738, 29748, 29759, 29770,
29780, 29790, 29801, 29811, 29821, 29831, 29842, 29852, 29862, 29872, 29882, 29893, 29903, 29913, 29923, 29934,
29947, 29959, 29969, 29980, 29990, 30000, 30010, 30023, 30036, 30047, 30058, 30070, 30080, 30090, 30100, 30114,
30128, 30138, 30148, 30158, 30168, 30178, 30189, 30199, 30209, 30219, 30229, 30239, 30249, 30259, 30269, 30279,
30290, 30300, 30313, 30325, 30335, 30345, 30355, 30365, 30378, 30388, 30398, 30408, 30418, 30428, 30438, 30448,
30458, 30468, 30480, 30490, 30501, 30511, 30521, 30532, 30542, 30553, 30563, 30573, 30585, 30595, 30605, 30615,
30625, 30635, 30645, 30655, 30665, 30675, 30686, 30696, 30706, 30716, 30726, 30736, 30749, 30759, 30769, 30787,
30797, 30812, 30824, 30841, 30851, 30862, 30872, 30882, 30892, 30906, 30916, 30926, 30938, 30949, 30959, 30969,
30980, 30990, 31001, 31011, 31021, 31032, 31042, 31052, 31062, 31072, 31082, 31092, 31103, 31114, 31124, 31136,
31146, 31156, 31166, 31176, 31186, 31196, 31206, 31222, 31232, 31242, 31252, 31262, 31272, 31287, 31300, 31310,
31320, 31330, 31340, 31350, 31360, 31370, 31380, 31390, 31400, 31410, 31422, 31434, 31448, 31459, 31469, 31479,
31489, 31502, 31512, 31522, 31532, 31544, 31556, 31566, 31576, 31587, 31597, 31607, 31618, 31628, 31638, 31648,
31660, 31671, 31681, 31691, 31701, 31711, 31721, 31731, 31741, 31751, 31761, 31772, 31782, 31792, 31803, 31813,
31824, 31834, 31844, 31854, 31864, 31876, 31889, 31902, 31912, 31922, 31932, 31944, 31954, 31964, 31975, 31985,
31995, 32005, 32015, 32025, 32040, 32050, 32060, 32070, 32080, 32091, 32102, 32112, 32122, 32132, 32142, 32156,
32166, 32176, 32186, 32196, 32206, 32216, 32227, 32238, 32249, 32259, 32269, 32279, 32289, 32299, 32309, 32319,
32329, 32339, 32350, 32360, 32370, 32380, 32390, 32401, 32411, 32566, 32579, 32589, 32600, 32611, 32621, 32631,
32643, 32653, 32666, 32676, 32687, 32697, 32707, 32717, 32727, 32737, 32747, 32757, 32767, 32779, 32789, 32799,
32809, 32819, 32829, 32839, 32849, 32860, 32871, 32881, 32893, 32903, 32914, 32924, 32937, 32948, 32962, 32972,
32982, 32992, 33005, 33016, 33026, 33045, 33055, 33065, 33081, 33091, 33101, 33115, 33125, 33135, 33145, 33155,
33165, 33175, 33186, 33196, 33207, 33218, 33228, 33239, 33249, 33260, 33271, 33281, 33291, 33301, 33311, 33322,
33332, 33343, 33353, 33363, 33374, 33384, 33394, 33404, 33418, 33428, 33438, 33448, 33459, 33469, 33489, 33499,
33509, 33519, 33529, 33539, 33549, 33559, 33570, 33580, 33590, 33600, 33610, 33620, 33651, 33661, 33671, 33682,
33693, 33703, 33725, 33735, 33745, 33755, 33765, 33775, 33785, 33795, 33805, 33819, 33833, 33843, 33853, 33863,
33873, 33883, 33893, 33903, 33913, 33926, 33936, 33946, 33956, 33966, 33976, 33986, 33996, 34006, 34023, 34033,
34043, 34054, 34065, 34076, 34086, 34096, 34107, 34117, 34129, 34139, 34149, 34161, 34171, 34181, 34191, 34201,
34211, 34223, 34233, 34243, 34253, 34263, 34273, 34283, 34294, 34304, 34314, 34327, 34337, 34348, 34358, 34368,
34379, 34389, 34399, 34409, 34419, 34437, 34448, 34458, 34468, 34479, 34489, 34499, 34512, 34522, 34532, 34549,
34560, 34570, 34584, 34594, 34604, 34615, 34625, 34636, 34646, 34656, 34666, 34676, 34689, 34701, 34711, 34722,
34732, 34742, 34752, 34762, 34772, 34782, 34792, 34802, 34812, 34822, 34832, 34843, 34853, 34863, 34873, 34883,
34893, 34903, 34913, 34923, 34933, 34943, 34953, 34963, 34974, 34984, 34994, 35004, 35017, 35028, 35038, 35048,
35058, 35068, 35078, 35088, 35098, 35109, 35119, 35131, 35142, 35152, 35162, 35172, 35182, 35193, 35203, 35215,
35227, 35238, 35250, 35261, 35282, 35292, 35302, 35312, 35322, 35332, 35342, 35352, 35362, 35372, 35382, 35392,
35402, 35412, 35422, 35432, 35442, 35452, 35462, 35473, 35486, 35496, 35506, 35516, 35526, 35537, 35547, 35558,
35568, 35578, 35588, 35598, 35608, 35618, 35628, 35638, 35648, 35658, 35668, 35679, 35690, 35700, 35710, 35720,
35730, 35740, 35895, 35905, 35915, 35925, 35935, 35945, 35955, 35965, 35977, 35987, 35997, 36007, 36018, 36028,
36039, 36049, 36060, 36070, 36080, 36090, 36100, 36111, 36121, 36196, 36206, 36216, 36228, 36238, 36249, 36259,
36269, 36279, 36289, 36299, 36309, 36319, 36329, 36339, 36349, 36359, 36369, 36379, 36389, 36400, 36412, 36423,
36435, 36445, 36455, 36466, 36476, 36486, 36496, 36506, 36516, 36530, 36541, 36553, 36563, 36573, 36583, 36593,
36603, 36613, 36624, 36634, 36644, 36654, 36664, 36674, 36685, 36695, 36705, 36763, 36774, 36784, 36799, 36809,
36819, 36832, 36842, 36852, 36862, 36875, 36885, 36895, 36909, 36920, 36930, 36941, 36952, 36962, 36973, 36983,
36993, 37003, 37013, 37023, 37034, 37044, 37054, 37064, 37076, 37087, 37097, 37107, 37117, 37127, 37137, 37147,
37158, 37168, 37178, 37188, 37198, 37208, 37218, 37228, 37239, 37249, 37259, 37273, 37283, 37293, 37303, 37313,
37323, 37333, 37346, 37356, 37367, 37377, 37388, 37398, 37411, 37421, 37431, 37445, 37455, 37466, 37476, 37487,
37497, 37507, 37517, 37527, 37537, 37547, 37557, 37568, 37578, 37589, 37599, 37609, 37623, 37633, 37643, 37653,
37663, 37673, 37683, 37702, 37712, 37722, 37732, 37744, 37754, 37768, 37778, 37789, 37799, 37809, 37824, 37834,
37844, 37854, 37864, 37877, 37887, 37897, 37907, 37920, 37930, 37941, 37951, 37961, 37973, 37984, 37994, 38004,
38014, 38263, 38274, 38284, 38296, 38307, 38317, 38327, 38339, 38349, 38362, 38372, 38428, 38440, 38450, 38460,
38474, 38484, 38494, 38506, 38516, 38526, 38536, 38546, 38556, 38567, 38577, 38587, 38597, 38610, 38620, 38632,
38642, 38653, 38663, 38673, 38684, 38694, 38704, 38714, 38724, 38738, 38748, 38758, 38768, 38778, 38788, 38798,
38808, 38818, 38828, 38838, 38849, 38859, 38869, 38879, 38893, 38904, 38914, 38924, 38934, 38944, 38955, 38965,
38977, 38988, 38999, 39010, 39023, 39080, 39090, 39100, 39110, 39131, 39141, 39151, 39161, 39171, 39184, 39194,
39204, 39214, 39226, 39237, 39248, 39259, 39318, 39329, 39339, 39349, 39361, 39371, 39381, 39391, 39401, 39412,
39422, 39433, 39444, 39454, 39465, 39476, 39486, 39496, 39506, 39518, 39528, 39592, 39603, 39614, 39626, 39636,
39647, 39659, 39670, 39681, 39691, 39701, 39711, 39721, 39731, 39742, 39752, 39762, 39775, 39788, 39798, 39808,
39824, 39834, 39844, 39854, 39864, 39875, 39891, 39902, 39912, 39927, 39941, 39954, 39964, 39976, 39986, 39996,
40006, 40016, 40030, 40040, 40051, 40165, 40177, 40187, 40197, 40208, 40219, 40229, 40239, 40251, 40261, 40271,
40281, 40295, 40305, 40315, 40325, 40336, 40346, 40356, 40367, 40377, 40387, 40397, 40407, 40417, 40427, 40437,
40447, 40457, 40467, 40477, 40565, 40575, 40585, 40595, 40605, 40615, 40628, 40638, 40648, 40659, 40669, 40679,
40690, 40700, 40710, 40720, 40730, 40740, 40750, 40760, 40770, 40780, 40790, 40800, 40810, 40820, 40830, 40845,
40856, 40866, 64012, 65072, 65082, 65092, 65102, 65113, 65123, 65281, 65291, 65301, 65311, 65321, 65331, 65343,
65353, 65363, 65373, 65536
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
162, 70, 162, 71, 162, 68, 161, 177, 162, 88, 161, 211, 161, 80, 161, 209,
161, 210, 163, 190, 163, 188, 163, 187, 163, 68, 163, 78, 163, 87, 163, 92,
163, 102, 163, 111, 199, 179, 199, 177, 199, 187, 199, 197, 199, 208, 199, 218,
199, 228, 161, 86, 161, 168, 161, 172, 161, 194, 162, 74, 162, 185, 161, 246,
161, 212, 161, 252, 161, 239, 161, 220, 161, 218, 161, 230, 161, 233, 199, 233,
199, 243, 162, 119, 162, 122, 162, 125, 162, 116, 162, 114, 162, 164, 162, 165,
162, 166, 162, 98, 162, 110, 162, 121, 161, 189, 161, 182, 161, 191, 161, 187,
162, 168, 161, 185, 161, 240, 161, 64, 161, 109, 161, 101, 161, 170, 162, 202,
198, 165, 198, 175, 198, 185, 198, 195, 198, 205, 198, 215, 198, 225, 198, 235,
198, 245, 198, 162, 198, 254, 199, 73, 199, 83, 199, 93, 199, 103, 199, 113,
199, 123, 199, 167, 198, 161, 163, 119, 163, 163, 163, 173, 163, 183, 161, 192,
162, 85, 162, 80, 162, 87, 162, 83, 164, 64, 164, 87, 165, 66, 165, 224,
164, 88, 164, 89, 201, 64, 165, 69, 173, 188, 165, 228, 168, 197, 176, 174,
164, 169, 164, 172, 168, 200, 166, 235, 173, 189, 164, 176, 164, 181, 165, 74,
165, 81, 201, 101, 165, 242, 165, 247, 201, 179, 201, 187, 201, 180, 201, 173,
167, 68, 202, 100, 203, 189, 202, 90, 167, 65, 203, 191, 203, 185, 168, 219,
168, 215, 203, 200, 203, 197, 171, 86, 205, 238, 171, 80, 205, 221, 171, 85,
171, 72, 205, 239, 208, 212, 173, 206, 173, 211, 212, 91, 173, 201, 173, 197,
208, 209, 212, 99, 212, 89, 176, 186, 212, 93, 212, 94, 212, 101, 176, 176,
216, 95, 179, 199, 216, 81, 182, 202, 220, 195, 220, 190, 220, 193, 185, 178,
224, 254, 224, 246, 187, 248, 228, 230, 187, 252, 232, 245, 192, 118, 236, 190,
195, 95, 247, 208, 165, 251, 168, 224, 176, 194, 164, 186, 166, 64, 173, 221,
201, 78, 171, 96, 203, 205, 190, 173, 167, 77, 208, 226, 208, 229, 225, 64,
176, 196, 165, 88, 164, 192, 201, 191, 202, 108, 167, 83, 203, 209, 168, 239,
171, 103, 208, 235, 208, 234, 179, 209, 179, 207, 182, 207, 188, 66, 190, 176,
165, 92, 167, 85, 168, 241, 171, 105, 176, 199, 179, 211, 225, 67, 190, 177,
164, 196, 166, 73, 216, 98, 201, 108, 173, 234, 232, 250, 176, 208, 164, 200,
168, 242, 164, 82, 168, 246, 166, 76, 171, 111, 201, 196, 203, 218, 208, 239,
225, 70, 201, 88, 176, 209, 164, 207, 168, 252, 232, 251, 165, 115, 165, 114,
166, 83, 166, 84, 202, 126, 202, 165, 167, 109, 167, 108, 167, 102, 167, 96,
203, 235, 203, 225, 168, 254, 203, 220, 169, 81, 169, 66, 206, 72, 171, 125,
206, 69, 171, 115, 171, 117, 206, 78, 173, 239, 173, 245, 173, 243, 173, 246,
173, 240, 208, 247, 208, 240, 212, 124, 212, 168, 212, 126, 176, 221, 176, 227,
176, 213, 212, 113, 216, 120, 179, 239, 216, 109, 179, 220, 179, 217, 179, 237,
179, 235, 182, 229, 220, 216, 220, 223, 182, 224, 182, 226, 225, 85, 225, 83,
185, 199, 185, 192, 225, 71, 188, 74, 229, 67, 228, 246, 188, 78, 233, 69,
190, 185, 233, 68, 236, 198, 236, 193, 239, 188, 195, 96, 243, 254, 196, 90,
246, 215, 197, 241, 165, 124, 202, 169, 167, 120, 169, 84, 174, 70, 182, 233,
233, 71, 166, 98, 166, 96, 167, 163, 202, 173, 169, 89, 203, 245, 203, 250,
203, 249, 171, 172, 206, 94, 206, 82, 171, 177, 209, 82, 174, 71, 209, 74,
212, 177, 212, 197, 212, 200, 212, 173, 176, 237, 212, 196, 216, 170, 216, 168,
179, 243, 216, 175, 216, 167, 182, 246, 220, 235, 220, 227, 225, 93, 185, 209,
225, 101, 185, 211, 229, 75, 190, 165, 188, 88, 233, 74, 190, 194, 192, 164,
241, 248, 247, 211, 206, 95, 225, 105, 204, 66, 165, 126, 176, 247, 164, 211,
166, 105, 169, 97, 171, 182, 174, 78, 176, 248, 188, 93, 201, 209, 166, 113,
167, 176, 202, 181, 202, 187, 169, 103, 169, 102, 204, 77, 169, 107, 171, 185,
171, 187, 206, 118, 206, 110, 171, 194, 174, 91, 209, 92, 174, 87, 209, 93,
216, 194, 176, 250, 212, 231, 212, 236, 212, 206, 176, 253, 180, 64, 216, 198,
212, 227, 180, 65, 216, 190, 216, 181, 221, 70, 220, 244, 225, 110, 225, 124,
185, 224, 225, 115, 229, 86, 229, 94, 233, 86, 190, 197, 236, 211, 236, 208,
239, 193, 245, 200, 164, 109, 167, 184, 204, 86, 177, 69, 192, 169, 201, 115,
202, 188, 169, 120, 171, 200, 209, 104, 174, 97, 212, 242, 216, 213, 216, 211,
185, 230, 185, 235, 236, 217, 171, 202, 180, 77, 201, 212, 221, 75, 202, 191,
192, 170, 167, 190, 171, 206, 174, 105, 177, 79, 239, 195, 201, 118, 201, 217,
202, 194, 202, 197, 204, 104, 204, 103, 169, 167, 206, 171, 206, 121, 206, 162,
174, 111, 174, 115, 209, 109, 212, 245, 177, 86, 212, 248, 213, 68, 216, 236,
216, 233, 180, 79, 216, 232, 221, 78, 183, 68, 225, 176, 185, 241, 229, 101,
229, 106, 233, 97, 233, 95, 192, 172, 244, 68, 246, 222, 164, 116, 165, 169,
164, 118, 180, 83, 202, 203, 204, 110, 171, 212, 209, 115, 177, 98, 180, 85,
221, 90, 185, 247, 188, 109, 229, 110, 165, 173, 171, 213, 167, 200, 169, 179,
171, 214, 209, 119, 213, 72, 177, 104, 183, 72, 225, 188, 225, 185, 233, 113,
245, 203, 164, 123, 171, 217, 183, 73, 202, 211, 169, 183, 174, 122, 180, 93,
185, 251, 204, 116, 167, 206, 177, 108, 202, 213, 169, 185, 171, 222, 213, 79,
177, 116, 180, 96, 188, 120, 242, 64, 201, 123, 201, 225, 204, 120, 202, 218,
202, 220, 169, 191, 204, 175, 204, 172, 171, 228, 169, 202, 204, 125, 206, 202,
206, 201, 206, 207, 171, 236, 171, 239, 177, 118, 177, 120, 174, 172, 174, 169,
213, 85, 213, 86, 216, 253, 216, 252, 177, 166, 216, 251, 180, 107, 180, 105,
217, 79, 180, 112, 217, 75, 180, 104, 221, 108, 225, 210, 221, 105, 221, 111,
186, 70, 225, 209, 188, 162, 225, 207, 229, 126, 229, 162, 233, 118, 188, 165,
229, 123, 236, 225, 233, 123, 239, 207, 236, 227, 239, 204, 195, 106, 197, 116,
201, 124, 204, 177, 180, 117, 225, 214, 194, 87, 204, 178, 174, 176, 165, 183,
166, 170, 201, 236, 167, 232, 167, 229, 202, 231, 202, 233, 167, 233, 169, 225,
204, 187, 169, 226, 169, 230, 204, 181, 169, 219, 206, 218, 206, 215, 174, 179,
171, 246, 206, 225, 174, 193, 213, 102, 209, 189, 174, 185, 174, 189, 209, 199,
213, 103, 213, 114, 177, 204, 177, 193, 213, 113, 177, 184, 177, 180, 177, 197,
213, 107, 217, 107, 180, 161, 217, 102, 221, 183, 221, 120, 183, 112, 221, 122,
221, 178, 221, 126, 221, 185, 221, 166, 221, 162, 225, 218, 186, 75, 225, 242,
188, 176, 188, 177, 229, 177, 229, 180, 188, 193, 229, 184, 188, 189, 190, 216,
190, 213, 233, 167, 233, 174, 236, 234, 192, 195, 194, 94, 239, 213, 242, 70,
244, 76, 246, 225, 197, 205, 166, 172, 206, 231, 177, 211, 177, 208, 180, 178,
221, 188, 229, 191, 192, 197, 180, 180, 177, 216, 165, 184, 180, 181, 164, 232,
209, 208, 213, 168, 186, 88, 201, 94, 166, 175, 169, 244, 204, 198, 169, 250,
206, 239, 206, 240, 172, 82, 174, 208, 174, 206, 213, 172, 177, 224, 180, 182,
221, 199, 183, 121, 221, 194, 186, 92, 186, 91, 229, 198, 233, 185, 190, 232,
236, 242, 194, 96, 196, 102, 164, 234, 176, 210, 164, 235, 209, 215, 180, 194,
196, 103, 166, 182, 201, 242, 202, 253, 167, 246, 203, 64, 170, 87, 204, 220,
170, 81, 170, 80, 170, 85, 170, 75, 207, 91, 172, 101, 207, 81, 207, 90,
207, 67, 172, 107, 172, 90, 207, 78, 209, 236, 209, 227, 209, 238, 209, 226,
209, 222, 174, 233, 209, 224, 213, 196, 213, 189, 177, 236, 213, 206, 177, 234,
177, 248, 177, 239, 177, 238, 177, 243, 217, 205, 180, 198, 180, 208, 180, 203,
217, 197, 217, 172, 180, 213, 217, 216, 221, 242, 221, 210, 221, 228, 221, 204,
221, 230, 221, 205, 221, 218, 221, 229, 183, 167, 226, 74, 226, 93, 226, 90,
186, 103, 226, 96, 226, 78, 225, 254, 186, 98, 229, 213, 229, 212, 229, 235,
229, 202, 188, 212, 229, 220, 188, 207, 233, 202, 233, 194, 233, 215, 233, 221,
233, 209, 233, 203, 233, 208, 233, 212, 192, 207, 236, 247, 236, 253, 236, 251,
237, 66, 194, 98, 239, 223, 239, 229, 242, 79, 192, 205, 195, 111, 244, 90,
244, 87, 245, 208, 247, 226, 248, 242, 170, 89, 209, 247, 213, 216, 221, 250,
226, 101, 233, 222, 194, 106, 172, 110, 230, 209, 204, 234, 174, 237, 217, 222,
226, 102, 233, 226, 172, 113, 183, 181, 239, 233, 183, 182, 204, 236, 210, 64,
217, 228, 221, 254, 233, 232, 165, 194, 203, 74, 174, 244, 180, 225, 165, 195,
168, 68, 202, 64, 166, 189, 166, 195, 203, 77, 168, 90, 205, 69, 205, 67,
168, 92, 170, 106, 204, 243, 205, 71, 170, 121, 204, 244, 205, 70, 170, 96,
204, 254, 207, 124, 207, 116, 210, 100, 207, 168, 172, 120, 207, 110, 172, 163,
210, 76, 210, 77, 175, 64, 210, 72, 210, 98, 175, 71, 175, 75, 178, 101,
213, 226, 213, 240, 213, 236, 178, 89, 213, 242, 178, 104, 213, 249, 178, 76,
178, 77, 181, 65, 180, 237, 180, 244, 180, 229, 180, 251, 180, 248, 180, 240,
217, 240, 180, 243, 218, 78, 217, 236, 222, 95, 183, 200, 222, 87, 222, 85,
183, 196, 183, 201, 226, 108, 222, 93, 226, 170, 226, 109, 186, 161, 226, 117,
186, 118, 226, 183, 186, 120, 186, 115, 186, 121, 226, 118, 188, 239, 230, 67,
230, 73, 230, 72, 192, 223, 230, 90, 230, 93, 191, 73, 233, 251, 191, 74,
233, 253, 234, 65, 237, 81, 237, 87, 237, 91, 237, 88, 239, 246, 194, 112,
194, 114, 195, 115, 239, 245, 196, 106, 244, 100, 245, 212, 197, 120, 247, 228,
249, 163, 166, 199, 203, 95, 205, 85, 170, 162, 205, 81, 172, 183, 172, 182,
172, 181, 210, 119, 175, 81, 175, 79, 175, 78, 178, 109, 178, 105, 214, 79,
214, 91, 181, 72, 218, 94, 218, 92, 218, 100, 222, 161, 183, 206, 222, 120,
222, 114, 183, 213, 222, 116, 226, 191, 226, 192, 186, 179, 230, 109, 230, 99,
191, 81, 191, 85, 191, 82, 234, 84, 237, 98, 192, 236, 194, 119, 242, 108,
242, 105, 245, 220, 248, 177, 170, 167, 170, 168, 210, 125, 181, 80, 188, 248,
168, 101, 205, 91, 172, 187, 214, 92, 218, 104, 186, 185, 234, 89, 196, 235,
202, 67, 203, 97, 205, 95, 205, 98, 207, 194, 172, 188, 210, 168, 175, 86,
214, 98, 214, 99, 178, 114, 181, 84, 218, 111, 222, 173, 186, 187, 188, 250,
230, 117, 230, 161, 234, 94, 239, 254, 197, 122, 201, 167, 203, 107, 170, 179,
170, 177, 172, 197, 207, 210, 207, 201, 210, 180, 210, 179, 210, 176, 214, 120,
178, 123, 214, 121, 218, 126, 218, 169, 181, 88, 181, 94, 183, 233, 222, 178,
222, 190, 183, 231, 188, 252, 226, 207, 230, 167, 230, 162, 234, 102, 191, 88,
192, 245, 192, 244, 244, 111, 242, 116, 196, 110, 246, 242, 195, 164, 207, 216,
218, 171, 230, 173, 237, 112, 178, 162, 181, 100, 165, 208, 203, 109, 207, 218,
175, 96, 210, 194, 181, 102, 183, 237, 234, 106, 197, 124, 203, 111, 172, 207,
210, 195, 210, 202, 210, 198, 214, 124, 181, 107, 181, 103, 222, 210, 222, 202,
183, 241, 186, 198, 226, 221, 189, 69, 189, 68, 234, 111, 234, 110, 237, 119,
194, 161, 240, 74, 196, 238, 249, 204, 202, 71, 214, 166, 181, 113, 230, 185,
244, 113, 181, 114, 244, 114, 172, 213, 178, 176, 181, 115, 234, 117, 205, 120,
207, 231, 207, 226, 210, 207, 210, 214, 214, 176, 214, 169, 214, 168, 218, 188,
218, 192, 222, 225, 183, 254, 222, 226, 184, 64, 226, 234, 230, 188, 234, 161,
234, 120, 237, 123, 237, 122, 194, 163, 242, 121, 196, 239, 218, 195, 207, 233,
244, 120, 205, 123, 207, 234, 207, 238, 175, 124, 175, 122, 210, 229, 178, 185,
214, 182, 218, 200, 218, 207, 222, 238, 222, 237, 184, 72, 184, 75, 226, 246,
226, 245, 189, 84, 230, 201, 230, 197, 191, 106, 234, 164, 193, 70, 237, 172,
237, 174, 194, 166, 195, 170, 244, 122, 245, 225, 248, 181, 205, 162, 207, 245,
175, 170, 175, 168, 214, 191, 218, 210, 184, 83, 226, 249, 226, 253, 230, 205,
193, 73, 246, 247, 165, 221, 170, 196, 172, 237, 210, 243, 175, 179, 214, 198,
181, 125, 218, 215, 184, 90, 184, 92, 227, 72, 189, 94, 234, 175, 191, 111,
237, 180, 194, 169, 244, 163, 168, 115, 207, 254, 210, 251, 214, 201, 223, 68,
227, 74, 230, 211, 237, 183, 197, 209, 210, 253, 181, 164, 186, 220, 244, 165,
208, 67, 211, 70, 214, 211, 214, 208, 178, 196, 214, 219, 218, 224, 218, 227,
218, 229, 223, 83, 223, 72, 184, 95, 227, 93, 227, 85, 227, 83, 189, 105,
230, 217, 230, 214, 189, 104, 230, 215, 234, 187, 234, 198, 191, 122, 237, 195,
237, 201, 193, 76, 191, 117, 240, 97, 240, 104, 240, 108, 242, 172, 244, 170,
196, 240, 246, 254, 197, 210, 198, 89, 208, 69, 211, 73, 211, 74, 218, 232,
218, 233, 223, 84, 227, 96, 230, 224, 234, 199, 193, 83, 194, 179, 247, 65,
168, 116, 172, 245, 211, 89, 175, 200, 214, 240, 178, 216, 178, 209, 178, 204,
181, 178, 184, 108, 181, 185, 181, 187, 223, 93, 223, 100, 223, 94, 186, 238,
227, 117, 186, 237, 186, 246, 227, 108, 227, 107, 189, 112, 189, 116, 230, 239,
189, 123, 230, 233, 234, 219, 234, 206, 234, 205, 193, 95, 193, 103, 193, 104,
237, 215, 240, 119, 194, 182, 242, 184, 242, 182, 196, 126, 245, 235, 247, 66,
249, 65, 166, 206, 234, 223, 245, 237, 208, 77, 214, 241, 223, 103, 189, 125,
242, 187, 208, 79, 178, 220, 184, 115, 189, 126, 195, 188, 211, 99, 214, 248,
219, 68, 187, 66, 230, 246, 239, 168, 244, 177, 181, 192, 175, 209, 214, 254,
234, 230, 166, 213, 175, 212, 215, 66, 219, 69, 227, 163, 230, 252, 193, 109,
197, 165, 187, 70, 203, 119, 203, 120, 170, 211, 170, 212, 208, 91, 173, 73,
173, 68, 208, 88, 175, 218, 175, 221, 175, 215, 215, 78, 215, 74, 178, 231,
181, 200, 219, 74, 219, 80, 223, 119, 184, 123, 223, 126, 184, 161, 227, 166,
227, 168, 231, 68, 231, 66, 234, 239, 234, 242, 193, 117, 237, 234, 242, 193,
249, 68, 175, 229, 219, 84, 211, 112, 244, 181, 227, 171, 166, 224, 175, 232,
215, 86, 219, 90, 223, 164, 231, 71, 240, 163, 244, 182, 166, 226, 202, 75,
203, 161, 208, 106, 205, 188, 205, 191, 170, 218, 170, 222, 173, 98, 173, 86,
173, 89, 173, 102, 208, 109, 173, 83, 206, 65, 175, 250, 211, 126, 211, 179,
175, 249, 175, 251, 175, 240, 175, 239, 215, 94, 215, 104, 215, 119, 178, 247,
215, 107, 179, 65, 178, 245, 219, 163, 219, 164, 219, 175, 219, 111, 181, 216,
219, 94, 181, 209, 181, 211, 219, 174, 223, 199, 223, 217, 184, 168, 223, 215,
223, 177, 184, 175, 223, 176, 223, 211, 223, 173, 223, 179, 187, 96, 227, 187,
227, 173, 227, 180, 187, 93, 227, 178, 227, 196, 227, 201, 231, 115, 223, 169,
231, 88, 189, 183, 189, 176, 231, 87, 231, 114, 231, 122, 231, 77, 234, 249,
235, 70, 235, 74, 235, 86, 235, 75, 237, 251, 238, 81, 237, 245, 238, 75,
237, 250, 193, 166, 194, 202, 240, 167, 194, 197, 242, 205, 242, 202, 242, 198,
244, 190, 244, 189, 196, 173, 196, 244, 245, 250, 247, 74, 247, 76, 247, 241,
249, 199, 179, 66, 184, 185, 240, 186, 208, 169, 211, 196, 211, 205, 211, 195,
176, 68, 179, 76, 215, 167, 215, 174, 181, 241, 219, 182, 181, 240, 223, 232,
184, 190, 184, 196, 187, 106, 187, 103, 227, 224, 187, 108, 227, 225, 189, 193,
231, 161, 189, 187, 231, 176, 189, 185, 235, 97, 191, 196, 235, 93, 191, 195,
238, 93, 238, 107, 193, 172, 238, 105, 240, 197, 238, 111, 240, 208, 194, 206,
242, 228, 242, 227, 244, 197, 246, 66, 247, 81, 247, 215, 197, 251, 211, 207,
173, 108, 181, 243, 191, 197, 173, 109, 211, 210, 211, 215, 215, 178, 215, 195,
179, 79, 215, 180, 219, 205, 219, 202, 219, 207, 184, 199, 223, 245, 187, 116,
227, 248, 227, 252, 231, 201, 189, 197, 187, 117, 191, 200, 235, 122, 193, 182,
240, 214, 240, 215, 242, 234, 196, 176, 246, 74, 249, 72, 181, 247, 179, 87,
223, 251, 191, 204, 194, 209, 196, 253, 168, 164, 224, 67, 224, 68, 231, 210,
246, 76, 208, 176, 176, 81, 176, 79, 179, 96, 215, 203, 182, 70, 219, 218,
219, 226, 182, 64, 181, 252, 184, 229, 184, 211, 184, 217, 224, 70, 187, 165,
187, 121, 187, 163, 189, 210, 189, 203, 231, 213, 231, 216, 189, 219, 191, 205,
235, 169, 191, 206, 193, 195, 193, 188, 238, 161, 238, 166, 194, 213, 240, 232,
242, 249, 242, 248, 242, 252, 196, 183, 244, 207, 196, 254, 247, 86, 248, 67,
197, 254, 198, 109, 168, 166, 193, 197, 224, 83, 168, 168, 219, 229, 228, 82,
193, 199, 215, 209, 184, 232, 191, 223, 168, 169, 179, 104, 184, 237, 182, 75,
184, 234, 187, 172, 189, 231, 189, 225, 235, 183, 193, 200, 240, 244, 196, 185,
248, 69, 168, 170, 235, 186, 211, 228, 219, 237, 224, 96, 187, 175, 189, 236,
243, 77, 211, 230, 215, 218, 182, 91, 219, 247, 184, 242, 224, 109, 184, 245,
228, 91, 228, 94, 231, 251, 231, 249, 231, 245, 235, 198, 235, 195, 238, 191,
238, 188, 241, 67, 240, 254, 195, 219, 195, 218, 244, 215, 247, 97, 247, 95,
249, 80, 184, 250, 235, 200, 249, 172, 211, 233, 215, 221, 220, 69, 220, 73,
182, 97, 224, 120, 228, 100, 232, 74, 232, 72, 235, 202, 191, 233, 193, 212,
194, 225, 244, 220, 197, 175, 168, 175, 191, 236, 168, 176, 203, 166, 203, 165,
205, 206, 208, 182, 173, 125, 176, 106, 211, 234, 215, 227, 179, 119, 179, 116,
220, 77, 182, 104, 185, 69, 185, 77, 187, 184, 187, 189, 190, 68, 190, 69,
193, 218, 195, 229, 176, 111, 203, 167, 168, 184, 205, 212, 208, 188, 208, 191,
215, 238, 211, 247, 215, 241, 215, 237, 179, 163, 220, 90, 224, 167, 228, 113,
232, 89, 190, 70, 241, 75, 246, 91, 212, 64, 215, 243, 220, 96, 185, 83,
187, 197, 232, 94, 190, 76, 238, 200, 193, 222, 243, 95, 244, 227, 198, 93,
196, 192, 212, 66, 212, 67, 215, 247, 216, 65, 220, 98, 220, 105, 220, 119,
182, 116, 228, 186, 185, 87, 185, 100, 224, 188, 185, 103, 185, 95, 224, 191,
228, 164, 224, 182, 228, 126, 228, 165, 228, 175, 228, 163, 232, 161, 232, 101,
232, 115, 232, 120, 232, 106, 232, 108, 235, 244, 235, 233, 236, 64, 192, 67,
235, 224, 235, 249, 191, 248, 235, 234, 238, 216, 193, 237, 238, 208, 193, 236,
235, 227, 241, 96, 241, 85, 241, 88, 241, 90, 241, 100, 243, 115, 243, 108,
243, 109, 243, 96, 195, 245, 244, 243, 244, 254, 244, 251, 244, 232, 244, 234,
246, 102, 197, 75, 246, 94, 247, 111, 248, 75, 197, 224, 198, 68, 249, 114,
198, 113, 170, 248, 173, 172, 220, 122, 185, 104, 187, 213, 190, 91, 238, 243,
238, 241, 236, 71, 243, 119, 246, 108, 170, 250, 203, 174, 168, 190, 205, 218,
208, 198, 208, 194, 176, 165, 179, 173, 179, 178, 220, 125, 220, 126, 224, 209,
187, 217, 192, 71, 193, 244, 176, 166, 182, 174, 187, 220, 194, 248, 171, 66,
182, 179, 224, 215, 190, 95, 192, 76, 193, 247, 243, 126, 196, 197, 246, 111,
198, 71, 171, 67, 192, 82, 192, 83, 220, 170, 228, 192, 228, 198, 232, 178,
192, 84, 241, 161, 241, 121, 243, 163, 197, 183, 173, 179, 239, 66, 243, 166,
173, 180, 236, 88, 179, 187, 185, 124, 228, 203, 232, 187, 190, 102, 236, 89,
239, 69, 195, 68, 243, 170, 245, 79, 197, 227, 173, 183, 232, 190, 196, 66,
246, 116, 173, 184, 216, 79, 182, 186, 224, 227, 187, 229, 192, 92, 192, 97,
236, 92, 195, 75, 241, 174, 196, 67, 245, 84, 247, 119, 173, 186, 239, 87,
249, 178, 224, 231, 187, 233, 232, 207, 190, 114, 190, 111, 236, 104, 239, 90,
239, 97, 241, 177, 243, 192, 243, 190, 245, 96, 245, 91, 197, 91, 247, 125,
247, 162, 248, 211, 249, 195, 176, 169, 232, 210, 239, 100, 245, 101, 197, 233,
212, 73, 232, 212, 236, 110, 239, 103, 243, 194, 246, 163, 248, 85, 241, 190,
236, 114, 228, 221, 239, 105, 246, 168, 228, 225, 232, 227, 232, 222, 236, 115,
236, 124, 239, 106, 239, 113, 194, 65, 241, 201, 241, 197, 241, 202, 243, 202,
196, 72, 245, 108, 245, 109, 245, 110, 246, 171, 246, 174, 247, 180, 247, 177,
247, 170, 248, 89, 248, 218, 249, 90, 249, 120, 179, 190, 228, 226, 232, 236,
232, 238, 236, 172, 192, 112, 236, 163, 239, 123, 194, 69, 239, 161, 241, 209,
241, 213, 239, 120, 243, 221, 243, 235, 243, 233, 245, 125, 245, 167, 245, 121,
197, 97, 246, 191, 246, 187, 246, 199, 197, 195, 248, 105, 248, 101, 247, 196,
248, 229, 248, 228, 249, 124, 249, 207, 179, 191, 179, 192, 239, 171, 196, 83,
197, 101, 228, 229, 196, 84, 190, 163, 239, 174, 232, 241, 194, 75, 245, 182,
248, 118, 243, 245, 248, 120, 232, 243, 246, 208, 239, 178, 245, 185, 248, 122,
247, 204, 241, 233, 243, 250, 245, 187, 197, 198, 248, 235, 249, 211, 192, 115,
249, 213, 247, 207, 201, 74, 161, 74, 161, 104, 161, 124, 161, 201, 161, 125,
161, 223, 161, 73, 161, 207, 162, 180, 161, 72, 162, 215, 162, 225, 161, 196,
162, 241, 162, 251, 161, 98, 0
            });

        super.setUp();
    }

}
