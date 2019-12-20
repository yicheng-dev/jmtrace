# jmtrace

## Build & Run

- Build: `$ mvn package` (then `jmtrace-1.0-jar-with-dependencies.jar` will be generated)
- Run: `$ java -javaagent:[path to jmtrace-1.0-jar-with-dependencies.jar] -jar [path to your jar]`

## Description

For every memory access (`getstatic/putstatic/getfield/putfield/*aload/*astore`), a log will be instrumented, whose format is as follows.

```
R/W     ThreadID    Object-HashCode     Member
```

such as 

```
W       1031        e7df7cd2ca07f4f1    java.lang.Object[0]
```