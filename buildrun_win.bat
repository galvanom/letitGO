REM Копирование patterns33.dat нужно для DraGo, который не понимает несколько classpath т.к. в шоке от знака ";"
REM Для работы через GTP уажите опцию -gtp в конце команды

javac -d build com\letitgo\Main.java && copy /y res\patterns33.dat build && java -cp build;res com.letitgo.Main -gtp