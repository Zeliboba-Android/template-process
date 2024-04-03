# Разработка программного комплекса для подготовки документов

1. [Описание проекта](#Description)
2. [Первичные настройки библиотеки](#Settings)
3. [Работа с документами типа .doc](#Doc)
4. [Работа с документами типа .docx](#Docx)
5. [Оконное приложение](#Window)
6. [Возникшие проблемы](#Problems)

## <a name="Description"></a> Описание проекта

Этот проект представляет собой программный комплекс, разработанный на языке Java, который обеспечивает возможность подготовки документов по шаблону в форматах .doc и .docx. Для достижения этой цели используется библиотека Apache POI.

## <a name="Settings"></a> Первичные настройки библиотеки

Apache POI представляет собой API, который позволяет использовать файлы MS Office в Java приложениях. Данная библиотека разрабатывается и распространяется Apache Software Foundation и носит открытый характер. Apache POI включает классы и методы для чтения и записи информации в документы MS Office.

В проекте используется фреймворк Maven, необходимо установить следующие зависимости: 
- [poi-ooxml](https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml) - чтобы редактировать файлы .docx:
- 
```
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

- [poi-scratchpad](https://mvnrepository.com/artifact/org.apache.poi/poi-scratchpad) - для работы с файлами .doc:

```
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-scratchpad</artifactId>
    <version>5.2.5</version>
</dependency>
```

Так как мы работаем в среде IntelliJ IDEA, нужно перезагрузите проект Maven, при необходимости скачать индексы.

## <a name="Doc"></a> Работа с документами типа .doc

## <a name="Docx"></a> Работа с документами типа .docx

## <a name="Window"></a> Оконное приложение

## <a name="Problems"></a> Возникшие проблемы

#### 1. Ошибка компилятора. Решено.

>ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using Simple Logger to log to...

В intellij зависимость log4j по какой-то причине не была добавлена в путь к классам. [Решение проблемы](https://stackoverflow.com/questions/47881821/error-statuslogger-log4j2-could-not-find-a-logging-implementation).

Добавляет зависимости [log4j-core](https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core) и [log4j-api](https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api) в pom.xml:

```
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>2.23.1</version>
</dependency>

<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.23.1</version>
</dependency>
```

После перезагрузить проект Maven.

#### 2. Проблема шрифта и размера при замене слова в .docx. Частично решено.

При реализации метода replaceParagraph таким образом:

```
private void replaceParagraph(XWPFParagraph paragraph, String tag, String replaceWord){
	String paragraphText = paragraph.getParagraphText();
	if (paragraphText != null && paragraphText.contains(tag)) {
		String updatedParagraphText = paragraphText.replace(tag, replaceWord);
		while (paragraph.getRuns().size() > 0) {
			paragraph.removeRun(0);
		}
		XWPFRun newRun = paragraph.createRun();
		newRun.setText(updatedParagraphText);
	}
}
```

возникает проблема с XWPFRun newRun, так как createRun() создает новый текстовый элемент с стилем по умолчанию, который обычно является шрифтом Calibri и размером 11.

Предпологаемое решение проблемы: 

```
private void replaceParagraph(XWPFParagraph paragraph, String tag, String replaceWord){
    for (XWPFRun run: paragraph.getRuns()){
        String paragraphText = run.getText(0);
        if (paragraphText != null && paragraphText.contains(tag)) {
            String updatedParagraphText = paragraphText.replace(tag, replaceWord);
            run.setText(updatedParagraphText, 0);
        }
    }
}
```

Однако в одном параграфе текст может быть разбит на несколько текстовых элементов из-за изменений стилей форматирования.
Таким образом наш runs может выглядеть так:

**Первый случай:**

```
0 = ФИО: ${
1 = fio
2 = }
```

или 

```
0 = ФИО:
1 = ${
2 = fio
3 = }
```

**Второй случай:**

```
0 = ФИО:
1 = $
2 = {
3 = fio
4 = }
```

Решение этой проблемы от нас реализовано немного кастыльно.

- replaceVariableFirstCase - реализация первого случая. Решили искать "\${" в runs, он может быть в объекте c текстом или в отдельном объекте XWPFRun. После нахождения меняем "\${" на replaceWord, остальные объекты меняем на пустое значение, ибо с удалением объекта могли возникнуть затруднения при разбиянии на методов. Итоговой вид:

```
0 = ФИО: <Значение>
1 = ""
2 = ""
```

или 

```
0 = ФИО:
1 = <Значение>
2 = ""
3 = ""
```

- replaceVariableSecondCase - реализация второго случая. Решили реализовать подобно первому, только будем искать "\$" в runs. После нахождения "\$" меняем на replaceWord, остальные объекты, включая отдельно "\{", меняем на пустое значение. Итоговой вид:

```
0 = ФИО:
1 = <Значение>
2 = ""
3 = ""
4 = ""
```

Также реализиованные дополнительные методы для оптимизации:

- replaceWord - заменяет указанное слово в тексте объекта XWPFRun на другое слово

- replaceTextAndRemoveBrace - заменяет указанный текст в объекте XWPFRun на пустую строку. Могут быть случаи, когда после "\}" в объекте может быть продолжение текста. В таком случае меняем его на пустое значение, другой текст не меняем.

**Возможные проблемы в будущем:**

Может возникнуть третий случай по типу:

```
0 = ФИО: ${
1 = fio}
```

Пока мы не встречались с таким типом, при необходимости подумаем, как исправить этот случай. 

Еще может возникнуть проблема: разбиена тега в тексте между разными объектами XWPFRun. Например:

```
0 = Дата рождения: ${
1 = birth
2 = _
3 = date
4 = }
```

В таком случае остается только подбирать теги или думать, как исправить этот случай.