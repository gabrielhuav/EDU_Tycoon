# Licencias y procedencia de recursos

## Licencia del proyecto

El código de IPN Tycoon (EDU_Tycoon) se distribuye bajo la **licencia MIT**.

- Archivo: [`LICENSE`](../LICENSE), en la raíz del repositorio.
- Titular: Copyright (c) 2026 Emmanuel Juarez Palma.
- Repositorio original: https://github.com/EmmanuelJuarez14/EDU_Tycoon
- Este repositorio es un fork del original. Las modificaciones del equipo se publican bajo la misma licencia MIT y conservan el aviso de copyright original, como exige la licencia.

## Recursos del juego (`assets/`)

| Recurso | Tipo | Autor / procedencia | Licencia |
|---|---|---|---|
| Archivos de mapa | Mapas del juego | Equipo del repositorio original (EmmanuelJuarez14/EDU_Tycoon) | MIT, la del proyecto |
| `assets.txt` | Índice de recursos | Generado automáticamente por la tarea `generateAssetList` de Gradle. No se versiona (está en `.gitignore`) | No aplica |

Recursos de Android en `android/res/` (ícono `logo_app`, textos, tema): procedencia del repositorio original, licencia MIT del proyecto.

> **Nota:** el repositorio original no documenta la autoría de los recursos gráficos. Se atribuyen al autor del proyecto original salvo que se indique lo contrario. Si se identifica un recurso de terceros, se agregará a esta tabla con su autor, sitio de origen y licencia.

## Dependencias de terceros

Se descargan con Gradle; no se incluye su código en el repositorio. Versiones definidas en `gradle.properties`.

| Dependencia | Uso en el proyecto | Licencia |
|---|---|---|
| libGDX 1.14.0 (gdx, backend-android, box2d, freetype) | Motor del juego: render, entrada, física, fuentes | Apache 2.0 |
| KTX 1.13.1-rc1 | Extensiones de Kotlin para libGDX | CC0 1.0 |
| gdx-ai 1.8.2 | Inteligencia artificial del juego | Apache 2.0 |
| Ashley 1.7.4 | Sistema de entidades (ECS) | Apache 2.0 |
| Artemis-odb 2.3.0 | Sistema de entidades (ECS) | BSD 2-Clause |
| VisUI 1.5.5 | Componentes de interfaz para Scene2D | Apache 2.0 |
| Kotlin 2.2.10 y kotlinx-coroutines 1.10.2 | Lenguaje y programación asíncrona | Apache 2.0 |
| AndroidX Room 2.7.0-alpha13 | Base de datos local SQLite | Apache 2.0 |
| AndroidX Lifecycle 2.8.7 | ViewModel y LiveData | Apache 2.0 |
| desugar_jdk_libs 2.1.5 | APIs de Java modernas en Android antiguo | GPL v2 con Classpath Exception |
| JUnit 4.13.2 y AndroidX Test | Pruebas | EPL 1.0 / Apache 2.0 |

## Declaración de uso de IA

Este documento se redactó con apoyo de Claude (Anthropic) a partir de los archivos del repositorio. El equipo verificó su contenido.
