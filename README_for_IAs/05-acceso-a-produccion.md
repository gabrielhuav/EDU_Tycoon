# Solicitud de acceso a producción

## Estado

| | |
|---|---|
| Enviada | 15 de agosto de 2026, 6:04 p.m. |
| **Aprobada** | **19 de agosto de 2026, 7:59 p.m.** |
| Tiempo de respuesta real | 4 días |
| Estado | ✅ Acceso a producción concedido |

El correo llegó de `no-reply-googleplay-developer@google.com` al propietario de la
cuenta. Las respuestas se aceptaron **sin observaciones ni solicitud de
aclaraciones**, así que este expediente sirve como plantilla validada para las
siguientes apps.

> Congratulations! Your app has been granted Google Play production access.
> You applied for Google Play production access for your app EDU Tycoon
> (io.moviles.IPN_Tycoon), and this has now been granted.

Requisitos previos, cumplidos antes de poder abrir el formulario:

- ✅ Publicar una versión de prueba cerrada
- ✅ Al menos 12 verificadores aceptando participar
- ✅ Ejecutar la prueba cerrada con mínimo 12 verificadores durante 14 días

La prueba cerrada de EDU Tycoon corrió del **12 de junio al 15 de agosto de 2026**
(más de dos meses) con **más de 14 verificadores** y cinco versiones: 1.0, 1.1,
1.2, 1.3 y 1.3.1.

## Respuestas enviadas

**Guardadas aquí a propósito: el formulario de Play no deja consultar lo que
enviaste.** Si rechazan la solicitud, un reenvío que improvise respuestas nuevas
sería incoherente con estas, y esa incoherencia es justo lo que dispara
escrutinio. Si hay que reenviar, corrige solo lo que el correo de rechazo señale.

Cada campo topa en **300 caracteres**.

### Bloque 1 — Acerca de la prueba cerrada

**1.1 ¿Cómo reclutaste usuarios para la prueba cerrada?** · 270/300

> Recluté a estudiantes de ESCOM-IPN, que son el público objetivo porque el juego
> simula fundar y administrar una institución educativa inspirada en el
> Politécnico, y completé el grupo con amigos y familiares. Participaron más de 14
> verificadores durante más de dos meses.

**1.2 ¿Qué tan fácil fue reclutar verificadores?**

> Difícil

**1.3 Describe el nivel de participación de los verificadores** · 294/300

> Probaron las funciones principales: el tutorial, la creación de partida, el mapa
> isométrico, la construcción y mejora de escuelas y el HUD de dinero, estudiantes
> y reputación. Jugaron como un usuario real. Lo menos usado fue el guardado en
> varias ranuras, casi todos guardaron una sola partida.

**1.4 Resumen de los comentarios y método de recolección** · 300/300

> Los comentarios se recogieron en grupos de mensajería como WhatsApp y Discord,
> sesiones presenciales con los estudiantes e issues en GitHub. Señalaron sobre
> todo errores puntuales, el balance de la economía del juego y controles de
> navegación del mapa. Todo se atendió en las versiones 1.1, 1.2 y 1.3

### Bloque 2 — Acerca de tu juego

**2.1 ¿Cuál es el público objetivo?** · 289/300

> Estudiantes y jugadores jóvenes aficionados a los juegos de simulación y
> gestión, en especial de la comunidad del IPN. Está pensado para partidas en
> móvil con control táctil, donde el jugador construye y administra su propia
> institución educativa hasta convertirla en un imperio educativo.

**2.2 ¿Qué aspectos hacen que tu juego se destaque?** · 296/300

> Es un tycoon ambientado en escuelas como el Instituto Politécnico Nacional: se
> funda y administra una institución educativa propia sobre un mapa isométrico
> donde se construyen y mejoran escuelas. El tutorial lo guía el Ing. Lázaro
> Cárdenas y el avance se mide en dinero, estudiantes y reputación.

**2.3 ¿Cuántas instalaciones esperas el primer año?**

> entre 0 y 10,000

### Bloque 3 — Nivel de preparación para producción

**3.1 ¿Qué cambios hiciste según lo aprendido en la prueba cerrada?** · 268/300

> A partir del feedback corregí los errores reportados y ajusté el balance de la
> economía y los controles de navegación del mapa, en las versiones 1.1, 1.2 y
> 1.3. La 1.3.1 actualizó el juego a Android 16 para cumplir el requisito de nivel
> de API objetivo de Google Play.

**3.2 ¿Cómo decidiste que tu juego está listo para producción?** · 295/300

> Tras más de dos meses de prueba cerrada con más de 14 verificadores y varias
> actualizaciones (1.1, 1.2, 1.3 y 1.3.1), los errores reportados por los usuarios
> fueron atendidos y las funciones principales se mantuvieron estables. El juego
> se puede completar sin fallos, por eso lo considero listo.

## Coherencia interna del expediente

Los cruces que importan, por si hay que reenviar:

| Cruce | |
|---|---|
| 1.1 "más de 14, más de dos meses" ↔ 3.2 | coincide |
| 1.4 declara 3 temas de feedback ↔ 3.1 resuelve esos 3 | coincide |
| 1.4 "versiones 1.1, 1.2 y 1.3" ↔ 3.1 | coincide |
| 1.3 funciones probadas ↔ 2.2 funciones descritas | coincide |
| ¿El bloque 1 promete funciones que la build probada no tenía? | no |

## Trampa: contar caracteres

**No uses `${#v}` de bash en esta máquina.** El locale no es UTF-8, así que cuenta
*bytes*, y cada vocal acentuada o eñe suma 2 en vez de 1. Infla el conteo unos 5–7
caracteres por campo y te hace recortar respuestas que sí cabían.

Usa Python, que sí cuenta caracteres:

```bash
python -c "import io; [print(l.split('|')[0], len(l.split('|',1)[1].rstrip())) for l in io.open('respuestas.txt',encoding='utf-8') if l.strip()]"
```

Con el formato `clave|texto` por línea.

## Después de la aprobación

**Acceso a producción ≠ publicar en producción.** Que aprueben la solicitud solo
habilita el permiso. Producción sigue en *Inactivo* hasta que promuevas una
versión a propósito.

No reenvíes ni crees otra solicitud mientras esté en revisión: no acelera nada y
puede reiniciar la cola.

Antes de publicar en producción hace falta el resto del expediente en orden:
formulario de Seguridad de los datos coherente con lo que la app recopila de
verdad (el que más rechazos causa), política de privacidad y página de
eliminación de cuenta que carguen sin 404, y correo de contacto correcto en
ambas.

## Referencia

La plantilla reutilizable con las 9 preguntas y los criterios de qué hace que una
respuesta funcione está en un artefacto de Claude, hecha a partir de Freedom
Knight y Politécnico Open World:

<https://claude.ai/code/artifact/f00e726c-1ad8-49de-9a0b-f0faf6213868>
