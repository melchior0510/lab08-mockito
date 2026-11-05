# Лаборатори №8: Mock объектууд, Mockito-гоор хамаарлыг орлуулж тестлэх

Оюутны нэр: Munkh-Orgil
Оюутны код: B242270130

## Орчин

Команд: java -version, mvn -version (гаралтыг доор хавсаргав)

## Хэсэг A: Найман тест

Файл: src/test/java/mn/edu/must/sqat/CourseRegistrationServiceTest.java

Команд: mkdir -p results && mvn test 2>&1 | tee results/mvn-test.txt

Үр дүн: Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, BUILD SUCCESS. Нийт 10 тест (7 энгийн тест + A8-ийн 3 мөр). Бүтэн гаралт results/mvn-test.txt файлд байгаа.

Хэрэглэсэн хэрэгслүүд: when/thenReturn (A1, A4), verify ба never (A1, A2, A4, A5), verifyNoInteractions (A3, A6), doThrow (A5), assertThrows (A6), ArgumentCaptor (A7), @ParameterizedTest + @CsvSource (A8).

### A8 дахь lenient()

A8 параметржүүлсэн тестийн мөр бүр өөр сценари тул нэг мөрд тохируулсан stub заримдаа өөр мөрд ашиглагддаггүй. Жишээ нь оюутан олдохгүй мөрд courseExists-ийг service огт дууддаггүй. MockitoExtension-ий strict stubs ашиглагдаагүй stub-ийг UnnecessaryStubbingException гэж алдаа болгодог тул lenient().when(...) хэрэглэсэн.

## Хэсэг B: Мини мутаци

Мутант бүрийн дараа кодыг эх хэлбэрт нь сэргээсэн.

| Мутант | Эвдрэл | Унасан тестүүд | Файл |
|--------|--------|----------------|------|
| 1 | Идэвхгүй оюутны шалгалт (if (!student.active())) комментлов | A3, A8 (INACTIVE мөр) | results/mvn-test-mutant-1.txt |
| 2 | saveRegistration(...) дуудалтыг комментлов | A1, A5 | results/mvn-test-mutant-2.txt |
| 3 | register()-ийн эхний if-ээс `\|\| courseId == null` хэсгийг устгав | A6 | results/mvn-test-mutant-3.txt |

## Орчны гаралт

java -version:

    openjdk version "17.0.20.1" 2026-08-18
    OpenJDK Runtime Environment (build 17.0.20.1+1-1-26.04-Ubuntu)
    OpenJDK 64-Bit Server VM (build 17.0.20.1+1-1-26.04-Ubuntu, mixed mode, sharing)

mvn -version:

    [1mApache Maven 3.9.12[m
    Maven home: /usr/share/maven
    Java version: 17.0.20.1, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
    Default locale: en_US, platform encoding: UTF-8
    OS name: "linux", version: "7.0.0-38-generic", arch: "amd64", family: "unix"

Мутант тус бүрийн үр дүн: мутант 1 Failures: 2, мутант 2 Failures: 2, мутант 3 Failures: 1 (бүгд Tests run: 10, BUILD FAILURE).

## Дүгнэлт

Энэ лабораторид CourseRegistrationService-ийг өгөгдлийн сан, и-мэйл сервергүйгээр Mockito-гоор бүрэн тусгаарлаж тестэлсэн. Mock болон гараар бичсэн fake-ийн ялгаа нь fake нь жинхэнэ ажилладаг хялбаршуулсан хэрэгжүүлэлт (жишээ нь санах ой дахь жагсаалт), харин mock нь урьдчилан тохируулсан хариу өгч, ямар дуудалт хийгдсэнийг бүртгээд verify-аар шалгах боломж өгдөг. Fake-ийг хэрэглэхэд хэрэгжүүлэлтийг өөрөө бичиж, засварлах хэрэгтэй, харин mock нь интерфейс бүрт хурдан, цөөн мөр кодоор бэлддэг. Mock хийх ёсгүй зүйлд тестлэж буй классыг өөрийг нь, Student record шиг энгийн өгөгдлийн объектыг, мөн өөрийн хяналтаас гадуурх, хэт нарийн гадаад API-г оруулна, учир нь тэгвэл тест жинхэнэ ажиллагааг биш өөрийн таамаглалыг шалгах болно. Verify-г хэтрүүлбэл тест дотоод хэрэгжүүлэлтийн дэс дараа, дуудалтын тоонд хэт наалдаж, бизнес логик зөв хэвээр байхад рефакторинг хийхэд л унах эрсдэлтэй. Тиймээс үр дүнг (буцаах утга) assert-ээр, чухал сайд нөлөөг (хадгалалт, и-мэйл) л verify-аар шалгах нь зөв тэнцвэр. Хэсэг B-ийн мутаци нь тест ногоон байх нь тест сайн гэсэн үг биш гэдгийг харууллаа: кодыг зориуд эвдэхэд тест унадаг эсэх нь жинхэнэ шалгуур. Мөн never() болон verifyNoInteractions нь "юу хийгдэхгүй" гэдгийг шалгадаг тул алдааны замуудыг тестлэхэд чухал. Ингээд mock нь хурдан, найдвартай, тусгаарлагдсан нэгжийн тест бичих хүчтэй хэрэгсэл боловч зөв хэмжээнд хэрэглэх хэрэгтэйг ойлгов.