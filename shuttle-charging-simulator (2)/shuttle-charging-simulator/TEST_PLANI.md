# Test Planı: Shuttle Charging Simulator

80 test, iki öğrenciye 40'ar test olarak bölündü. Her öğrencinin kendi sınıfları var, bu yüzden testler birbirini tekrar etmiyor.

| | Öğrenci 1 | Öğrenci 2 |
|---|---|---|
| Sınıflar | `Shuttle`, `Charger`, `ShuttleComparator`, `ChargingRecord`, `SimulationResult`, `InputValidator` | `InputParser`, `Simulator` |
| Test sayısı | 40 | 40 |
| Parameterized test | #16 | #27 |
| Assertion türleri | 11 tür | 10 tür |
| Suite | `ModelTestSuite` | `SimulationTestSuite` |

**Tabloları okuma:**
- **Kurulum** sütunu, testte hangi nesneleri oluşturacağını söyler. `Shuttle(id, priority, arrival, service)` sırasıyla yazılmıştır.
- **Assertion** sütunu, hangi assertion'ı kullanacağını ve beklenen değeri verir. Satırı kendin yazacaksın.
- **🐞** işaretli testler bilerek **kırmızı** kalır, çünkü projedeki bir hatayı yakalarlar. Beklenen değer doğru davranıştır, programın verdiği değer yanlıştır. Bunlara `@Tag("bug")` ve `@DisplayName("BUG: ...")` ekle.
- Tüm beklenen değerler projede çalıştırılarak doğrulandı.

---

## Başlamadan önce

1. Test sınıflarını **`src/test/java/com/merickilic/shuttle/`** içine koy. Paket adı, ana koddaki paketle aynı olmalı. Böyle olmazsa `initializeSimulation` gibi package-private metotlara erişemezsin.
2. `ExampleTest.java` dosyasını bir kez çalıştır. 5 test de yeşil olursa kurulum tamamdır. Bu dosya kalıp olarak duruyor, teslimden önce sil (plana dahil değil).
3. Her test sınıfının başına şu import'ları koy:
   ```java
   import org.junit.jupiter.api.*;
   import static org.junit.jupiter.api.Assertions.*;
   import static org.hamcrest.MatcherAssert.assertThat;   // Hamcrest kullanacaksan
   import static org.hamcrest.Matchers.*;
   ```
4. **Türkçe locale uyarısı:** Bilgisayarın Türkçe ayarlıysa `String.format` ondalık sayıları `3,50000` diye virgülle yazar. Ondalık sayı içeren bir metni karşılaştıran testlerde şunu ekle:
   ```java
   private Locale original;
   @BeforeEach void setLocale()     { original = Locale.getDefault(); Locale.setDefault(Locale.US); }
   @AfterEach  void restoreLocale() { Locale.setDefault(original); }
   ```
5. `Simulator` ekrana her atamayı yazar. Konsolu temiz tutmak için çıktıyı boşa yönlendir: `new Simulator(list, new PrintStream(OutputStream.nullOutputStream()))`.

---

## Öğrenci 1: Model sınıfları (40 test)

### `ShuttleTest` (3)

| # | Test | Kurulum | Assertion |
|---|---|---|---|
| 1 | Constructor tüm alanları saklar | `Shuttle(7, 3, 15, 20)` | `assertAll` içinde 4 adet `assertEquals` (7, 3, 15, 20) |
| 2 | Uç değerler saklanır | priority = `Integer.MAX_VALUE`, arrival = `Integer.MIN_VALUE` | `assertEquals(Integer.MAX_VALUE, s.priority)` |
| 3 | Negatif service kabul edilir (doğrulama yok) | `new Shuttle(1, 1, 0, -5)` | `assertDoesNotThrow(() -> ...)` |

### `ChargerTest` (11)

| # | Test | Kurulum | Assertion |
|---|---|---|---|
| 4 | Yeni charger id'yi saklar | `new Charger(3)` | `assertEquals` → 3 |
| 5 | Yeni charger'da shuttle yok | `new Charger(0)` | `assertNull(c.currentShuttle)` |
| 6 | Yeni charger'ın availableTime'ı 0 | `new Charger(0)` | `assertEquals` → 0 |
| 7 | Yeni charger 0. dakikada müsait (sınır) | `new Charger(0)` | `assertTrue(c.isAvailable(0))` |
| 8 | Atama availableTime'ı ayarlar | `Shuttle(1,1,5,10)`, 5. dakikada atanır | `assertEquals` → 15 |
| 9 | Atama aynı nesneyi saklar | #8 ile aynı | `assertSame(shuttle, c.currentShuttle)` |
| 10 | Şarj bitmeden müsait değil | #8 sonrası | `assertFalse(c.isAvailable(14))` |
| 11 | Tam bitiş anında müsait (sınır) | #8 sonrası | `assertTrue(c.isAvailable(15))` |
| 12 | İkinci atama ilk shuttle'ı siler (asıl hatanın kökü) | s1 0. dakikada, s2 15. dakikada atanır | `assertNotSame(s1, c.currentShuttle)` |
| 13 | null shuttle atanamaz | `assignShuttle(null, 0)` | `assertThrows(NullPointerException.class, ...)` |
| 14 | 0 dakikalık şarjda charger hemen boşalır | `Shuttle(1,1,0,0)`, 7. dakikada atanır | `assertTrue(c.isAvailable(7))` |

### `ShuttleComparatorTest` (6)

| # | Test | Kurulum | Assertion |
|---|---|---|---|
| 15 | Kendisiyle karşılaştırma 0 döner | aynı nesne iki kez | `assertEquals(0, cmp.compare(s, s))` |
| 16 | **Parameterized:** önce öncelik, sonra varış | aşağıdaki tablo | `assertEquals(expectedSign, Integer.signum(cmp.compare(a, b)))` |
| 17 | Uç önceliklerde taşma olmaz | priority `MAX_VALUE` ile `MIN_VALUE` | `assertEquals(-1, ...)` |
| 18 | null karşılaştırılamaz | `compare(null, s)` | `assertThrows(NullPointerException.class, ...)` |
| 19 | Liste doğru sıralanır | `(1,1,0,5)`, `(2,3,5,5)`, `(3,3,2,5)`, `(4,2,1,5)` → `list.sort(cmp)` | `assertIterableEquals(List.of(3, 2, 4, 1), ids)` |
| 20 | 🐞 Eşit shuttle'larda geliş sırası korunmalı | `PriorityQueue`'ya id 1, 2, 3 eklenir (hepsi `p=1, a=0`), sonra sırayla `poll` | `assertEquals(List.of(1, 2, 3), polled)`. Gerçek sonuç `[1, 3, 2]` |

#16 için `@CsvSource` satırları (`p1, a1, p2, a2, expectedSign`):
```
"3, 0, 1, 0, -1"   yüksek öncelik önce
"1, 0, 3, 0,  1"   düşük öncelik sonra
"2, 1, 2, 5, -1"   aynı öncelik, erken gelen önce
"2, 5, 2, 1,  1"   aynı öncelik, geç gelen sonra
"2, 4, 2, 4,  0"   tamamen eşit
"5, 9, 1, 0, -1"   öncelik varıştan baskın
```

### `ChargingRecordTest` (6)

| # | Test | Kurulum | Assertion |
|---|---|---|---|
| 21 | Constructor alanları saklar | `ChargingRecord(1, 0, 3, 8, 18)` | `assertAll` |
| 22 | Bekleme süresi = başlangıç − varış | #21 ile aynı | `assertEquals` → 5 |
| 23 | Şarj süresi = bitiş − başlangıç | #21 ile aynı | `assertEquals` → 10 |
| 24 | Başlangıç = varış ise bekleme 0 (sınır) | `ChargingRecord(1, 0, 4, 4, 9)` | `assertEquals` → 0 |
| 25 | toString shuttle ve charger bilgisini içerir | #21 ile aynı | `assertThat(str, allOf(containsString("shuttle=1"), containsString("charger=0")))` |
| 26 | Tutarsız kayıt negatif bekleme verir (doğrulama yok) | `ChargingRecord(1, 0, 10, 5, 8)` | `assertTrue(r.getWaitingTime() < 0)` |

### `SimulationResultTest` (3)

| # | Test | Kurulum | Assertion |
|---|---|---|---|
| 27 | Alanları saklar | `SimulationResult(2, 3.5)` | `assertAll`; double için `assertEquals(3.5, r.averageWaitingTime, 1e-9)` |
| 28 | toString 5 ondalık basamak kullanır | `Locale.US` | `assertEquals("SimulationResult{chargers=2, avgWait=3.50000}", r.toString())` |
| 29 | 🐞 Çıktı locale'e bağlı | `Locale.setDefault(Locale.forLanguageTag("tr-TR"))` | #28 ile aynı beklenti. Gerçek sonuç `3,50000` |

### `InputValidatorTest` (11)

| # | Test | Kurulum | Assertion |
|---|---|---|---|
| 30 | Geçerli liste geçerlidir | `(1,1,0,5)`, `(2,1,2,5)` | `assertTrue(InputValidator.isValid(list))` |
| 31 | Boş liste tek hata verir | `List.of()` | `assertEquals(List.of("No shuttles in input"), errors)` |
| 32 | Tekrar eden id bildirilir | `(1,1,0,5)`, `(1,1,2,5)` | `assertTrue(errors.contains("Line 2: duplicate shuttle id 1"))` |
| 33 | Negatif varış bildirilir | `(1,1,-3,5)` | mesaj: `"Line 1: negative arrival -3"` |
| 34 | service = 0 bildirilir (sınır) | `(1,1,0,0)` | mesaj: `"Line 1: service must be positive"` |
| 35 | service = 1 geçerlidir (sınır) | `(1,1,0,1)` | `assertTrue(isValid(...))` |
| 36 | Sırasız varış bildirilir | `(1,1,5,5)`, `(2,1,2,5)` | mesaj: `"Line 2: not sorted by arrival time"` |
| 37 | Eşit varışlar sıralı sayılır (sınır) | `(1,1,4,5)`, `(2,1,4,5)` | `assertTrue(isValid(...))` |
| 38 | Birden çok hata birlikte bildirilir | `(1,1,0,0)`, `(2,1,1,5)`, `(3,1,-1,5)` | `assertThat(errors, hasSize(3))` |
| 39 | null liste hata verir | `validate(null)` | `assertThrows(NullPointerException.class, ...)` |
| 40 | Girdi listesi değiştirilmez | `ArrayList` + kopyası | `assertEquals(copy, list)` |

**Öğrenci 1'in assertion türleri (11):** `assertEquals`, `assertAll`, `assertTrue`, `assertFalse`, `assertNull`, `assertSame`, `assertNotSame`, `assertThrows`, `assertDoesNotThrow`, `assertIterableEquals`, `assertThat`

---

## Öğrenci 2: Simülasyon (40 test)

### `InputParserTest` (14)

| # | Test | Girdi (`parseString`) | Assertion |
|---|---|---|---|
| 1 | Geçerli girdide doğru sayı | `"2\n1 2 0 10\n2 1 3 5\n"` | `assertEquals(2, list.size())` |
| 2 | Alanlar doğru sırada okunur | #1 ile aynı, ilk shuttle | `assertAll`: id 1, priority 2, arrival 0, service 10 |
| 3 | Dosyadaki sıra korunur | `"3\n3 1 0 5\n1 1 1 5\n2 1 2 5\n"` | `assertIterableEquals(List.of(3, 1, 2), ids)` |
| 4 | Çoklu boşluk ve tab kabul edilir | `"1\n1\t2  3 4\n"` | `assertEquals(4, list.get(0).service)` |
| 5 | N = 0 ise boş liste | `"0\n"` | `assertTrue(list.isEmpty())` |
| 6 | Fazla satırlar yok sayılır | `"1\n1 2 3 4\n5 6 7 8\n"` | `assertEquals(1, size)` |
| 7 | Negatif N boş liste verir (doğrulama yok) | `"-1\n"` | `assertEquals(0, size)` |
| 8 | 🐞 Başında boşluk olan satır okunabilmeli | `"1\n 1 2 3 4\n"` | `assertDoesNotThrow(...)`. Gerçekte `NumberFormatException` |
| 9 | Boş girdi hata verir | `""` | `assertThrows(NumberFormatException.class, ...)` |
| 10 | Sayı olmayan değer hata verir | `"1\n1 a 0 5\n"` | `assertThrows(NumberFormatException.class, ...)` |
| 11 | Eksik satır hata verir | `"2\n1 1 0 5\n"` | `assertThrows(NullPointerException.class, ...)` |
| 12 | Eksik sütun hata verir | `"1\n1 1 0\n"` | `assertThrows(ArrayIndexOutOfBoundsException.class, ...)` |
| 13 | Örnek dosya okunur | `parseFile("src/test/resources/shuttles_sample.txt")` | `assertEquals(5, size)` |
| 14 | Olmayan dosya hata verir | `parseFile("yok.txt")` | `assertThrows(IOException.class, ...)` |

### `SimulatorTest` (26)

| # | Test | Kurulum | Assertion |
|---|---|---|---|
| 15 | K adet charger oluşturulur | `initializeSimulation(3)` | `assertEquals(3, sim.getChargers().length)` |
| 16 | Charger id'leri 0'dan sıralı | #15 ile aynı | `assertArrayEquals(new int[]{0, 1, 2}, ids)` |
| 17 | Yeni çalıştırma durumu sıfırlar | önce `simulateWithKChargers(1)`, sonra `initializeSimulation(1)` | `assertAll`: `getCurrentTime()` 0, `getWaitingQueueSize()` 0, kayıt listesi boş |
| 18 | Constructor listeyi kopyalar | `ArrayList` ile oluştur, sonra listeye bir shuttle daha ekle, `simulateWithKChargers(1)` | `assertEquals(1, records.size())` |
| 19 | Sadece gelmiş shuttle'lar kuyruğa girer | varışlar 0, 0, 5; `initializeSimulation(1)` + `addArrivingShuttles()` | `assertEquals(2, getWaitingQueueSize())` |
| 20 | Zaman ilerleyince kalanlar da girer | #19 sonrası `setCurrentTime(5)` + `addArrivingShuttles()` | `assertEquals(3, ...)` |
| 21 | Boş charger'ların hepsi dolar | 3 shuttle (hepsi 0'da), `initializeSimulation(2)`, ekle, ata | `assertAll`: 2 kayıt, kuyrukta 1 shuttle |
| 22 | Dolu charger'a tekrar atama yapılmaz | #21 sonrası aynı dakikada tekrar `assignShuttlesToChargers()` | `assertEquals(2, records.size())` |
| 23 | Atama satırı ekrana yazılır | çıktı yakalanır, `(1,2,0,5)`, `(2,1,0,5)`, K=1 | `assertThat(output, containsString("Charger 0 takes shuttle 1 at minute 0 (wait: 0 mins)"))` |
| 24 | Sonraki olay: charger'ın bitiş zamanı | `(1,1,0,10)`, `(2,1,20,5)`; init(1), ekle, ata | `assertEquals(10, getNextEventTime())` |
| 25 | Sonraki olay: daha erken varış | `(1,1,0,10)`, `(2,1,4,5)`; aynı adımlar | `assertEquals(4, ...)` |
| 26 | Tek shuttle'da ortalama bekleme 0 | `(1,1,3,5)`, K=1 | `assertEquals(0.0, avg, 1e-9)` |
| 27 | **Parameterized:** tek shuttle varış anında başlar | `@CsvSource({"0, 5", "3, 10", "100, 1"})` → `(arrival, service)` | `assertEquals(arrival, records.get(0).startTime)` |
| 28 | Yüksek öncelikli önce şarj olur | `(1,1,0,5)`, `(2,5,0,5)`, K=1 | `assertEquals(2, records.get(0).shuttleId)` |
| 29 | Charger geç gelen shuttle'ı bekler | `(1,1,0,2)`, `(2,1,10,3)`, K=1 | `assertEquals(10, records.get(1).startTime)` |
| 30 | Kayıt listesi değiştirilemez | `getChargingRecords().clear()` | `assertThrows(UnsupportedOperationException.class, ...)` |
| 31 | Yeniden çalıştırma eski kayıtları siler | 3 shuttle, önce K=1, sonra K=2 | `assertEquals(3, records.size())` |
| 32 | Çakışmayan shuttle'lara 1 charger yeter | `(1,1,0,5)`, `(2,1,10,5)`, `findMinimumChargers(0)` | `assertEquals(1, result.chargerCount)` |
| 33 | Sonuç metni doğru biçimde | `Locale.US`, çıktı yakalanır, `printResults(2, 1.5)` | `assertAll`: `"Minimum number of chargers required: 2"` ve `"Average waiting time: 1.50000 minutes"` |
| 34 | Olmayan dosyada null döner | `Simulator.runSimulation("yok.txt", 5)` | `assertNull(result)` |
| 35 | 🐞 Ortalama bekleme yanlış | 3 × `(i,1,0,10)`, `simulateWithKChargers(1)` | `assertEquals(10.0, avg, 1e-9)`. Gerçek sonuç `0.0` |
| 36 | 🐞 Önceki shuttle'ın çıkış zamanı kayboluyor | #35 sonrası `getDepartureTime(shuttle1)` | `assertEquals(10, ...)`. Gerçek sonuç `0` |
| 37 | 🐞 Ortalama bekleme negatif olmamalı | `(1,1,0,5)`, `(2,1,10,5)`, K=1 | `assertTrue(avg >= 0)`. Gerçek sonuç `-2.5` |
| 38 | 🐞 Yoğun yükte 1 charger yetmez | 10 × `(i,1,0,10)`, `findMinimumChargers(5)` | `assertEquals(5, result.chargerCount)`. Gerçek sonuç `1` |
| 39 | 🐞 Sırasız dosyada erken gelen bekletiliyor | `(1,1,5,3)`, `(2,1,0,3)`, K=1 | shuttle 2'nin kaydı için `assertEquals(0, startTime)`. Gerçek sonuç `5` |
| 40 | 🐞 Boş listede sonsuz döngü | `new Simulator(List.of(), ...)` | `assertTimeoutPreemptively(Duration.ofSeconds(1), () -> sim.findMinimumChargers(5))` |

**Öğrenci 2'nin assertion türleri (10):** `assertEquals`, `assertAll`, `assertTrue`, `assertThrows`, `assertDoesNotThrow`, `assertIterableEquals`, `assertArrayEquals`, `assertNull`, `assertTimeoutPreemptively`, `assertThat`

**Notlar:**
- #16 için id dizisini şöyle çıkarabilirsin: `Arrays.stream(sim.getChargers()).mapToInt(c -> c.id).toArray()`
- #40 zaman aşımına uğrayıp kırmızı olur. Arkada çalışan thread test bitince durmaz ama zarar vermez, JVM kapanınca o da kapanır.
- 🐞 testlerinin ortak kökü: her charger sadece **son** shuttle'ı hatırlıyor (`currentShuttle`). Öğrenci 1'in #12 testi bunu doğrudan gösteriyor.

---

## İki test suite

Suite'ler yeni test yazmaz, var olan test sınıflarını gruplar. İkisini `src/test/java/com/merickilic/shuttle/` içine koy.

```java
import org.junit.platform.suite.api.*;

@Suite
@SuiteDisplayName("Model Tests")
@SelectClasses({ShuttleTest.class, ChargerTest.class, ShuttleComparatorTest.class,
                ChargingRecordTest.class, SimulationResultTest.class, InputValidatorTest.class})
public class ModelTestSuite { }
```

```java
@Suite
@SuiteDisplayName("Simulation Tests")
@SelectClasses({InputParserTest.class, SimulatorTest.class})
public class SimulationTestSuite { }
```

**Sunum için ipucu:** Bir suite'e `@ExcludeTags("bug")` eklersen o suite tamamen yeşil çalışır. Hatalı testleri ise `@IncludeTags("bug")` kullanan ayrı bir suite ile ayrıca gösterebilirsin.

---

## Örnek: bir satırı teste çevirmek

Öğrenci 1, #8 için:

```java
@Test
@DisplayName("assignShuttle sets availableTime to time + service")
void assignSetsAvailableTime() {
    Charger c = new Charger(0);
    Shuttle s = new Shuttle(1, 1, 5, 10);

    c.assignShuttle(s, 5);

    assertEquals(15, c.availableTime);
}
```

Öğrenci 2, 🐞 #35 için:

```java
@Test
@Tag("bug")
@DisplayName("BUG: average waiting time ignores earlier shuttles")
void averageWaitIsWrong() {
    Simulator sim = new Simulator(List.of(
        new Shuttle(1, 1, 0, 10), new Shuttle(2, 1, 0, 10), new Shuttle(3, 1, 0, 10)), silent);

    double avg = sim.simulateWithKChargers(1);

    assertEquals(10.0, avg, 1e-9, "real waits are 0, 10, 20 -> average 10");
}
```

Diğer kalıplar (çıktı yakalama, parameterized test, zaman sınırı) `ExampleTest.java` içinde.
