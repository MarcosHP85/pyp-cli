## Objetivo
Gestionar la información necesaria a la hora de planificar una tarea. Con el fin de reducir el tiempo que un planificador emplea en tareas administrativas y/o repetitivas, minimizando los efectos negativos que estas producen.
## Motivación
Para realizar una planificación optima de una tarea es necesario contar con toda la información de los componentes, estructuras y sistemas de planta que intervienen en esta, léase planos, manuales, historial, etc. 
Pero los diferentes criterios de organización y formato, en que se encuentra actualmente dicha información, provocan que la etapa de análisis y de confección del paquete de trabajo por parte del planificador lleve demasiado tiempo o sean incompleta.
## Propuesta
Desarrollar un sistema informático, que permita gestionar el proceso de planificación de una orden de trabajo. Desde que se genera, se le asigna planificador hasta la creación de un paquete de trabajo y el paso a programación de la misma.
Puntos a considerar:
1. **Interface:** El método en el que un usuario interactúa con el sistema es a través de sus pantallas, por lo que el diseño de estas dependerá sus necesidades, teniendo en cuente el flujo de trabajo. La interface será capaz de adaptarse al usuario, presentándole información que le interese como las ordenes de trabajo que le fueron asignada, también deberá facilitar el acceso a los datos relacionados a la tarea como historial, puenteos, modificaciones, paquetes de trabajo anteriores, plan de preventivos, documentación del componente, etc.
1. **Notificaciones:** En la actualidad, si un usuario desea conocer los cambios que se produjeron en el sistema (IFS), como por ej. se creó una nueva OT o le asignaron una para planificar; es el propio usuario quien debe buscar estas novedades, por lo que de sus habilidades técnicas para crear consultas y la frecuencia con las ejecuta, dependerá lo informado que esté. Para evitar perdidas de tiempo, es necesario invertir esta carga y que el propio sistema sea quien alerte al usuario de los cambios que lo involucren.
1. **Documentación:** A la hora de planificar, contar con toda la documentación relacionada al componente a intervenir es primordial si se quiere analizar correctamente la situación. Por lo que disponer de toda la documentación agrupada por componente en una única pantalla, eliminaría todos los pasos de ir recolectando información por diferentes sitios flowsheets, planos, fotos, manuales, hojas de medición, instrucciones, etc. ahorrando muchísimo tiempo y la posibilidad de obviar alguno de estos.
1. **Paquete de trabajo:** 
### Detalles
Una aplicación web es aquella 

> OFTEC : mysql -> pdf
> * pelec (eléctrico i&c)
> * flow
> * esq
> * esqreg
> * inin (trabajo calibración)
> * edificios

> ALARMA : mysql
> * vw_Alarmas

> MKB : mysql
> * principal <- variable

> ASK : mysql
> * principal <- variable

> HdeM : mdb
> * Lazos

> : postgres
> * MI
> * MIT
> * LVL (valores_limites)
> * Puenteos UI (puenteos_cnai)
> * Puenteos UII (puntos_abiertos_cnaii)

> CF : carpeta
> * YF -> pdf
> * YS -> pdf
