
# Лабораторная работа №4

## Описание
Данная программа дает возможность создавать b-сплайны и отображать их в формате фигуры вращения.

![бокал_сплайн](https://github.com/jeiswald/nsu_computer_graphics/assets/93196338/e261de95-7ce9-4908-a675-5f50dc12f317)

![бокал](https://github.com/jeiswald/nsu_computer_graphics/assets/93196338/1d797c60-f14c-4dea-a47e-1496f3fb6296)

Описание функционала меню окна редактирования сплайна:
- n количество участков ломаной на каждый участок сплайна
- m количество участков ломаной на каждый участок круга, соединяющий сплайны в фигуре вращения
- lines сколько сплайнов рисовать при создании фигуры вращения
- point номер текущей точки сплайна
- x,y координаты текущей точки сплайна, редактируемые поля
- add добавляет новую точку в конец последовательности
- "<", ">" кнопки навигации по точкам 
- cancel кнопка, отменяющая изменения
- default кнопка, возвращающая изменяемые параметры к стандартным значениям
- apply кнопка, применяющая изменения

Описание функционала рабочей области окна редактирования сплайна:
- нажатие левой кнопки на основную точку сплайна выбирет её
- нажатие правой кнопки на основную точку сплайна удаляет её
- нажатие на точку находящуюся между двумя основными точками сплайна создает новую основную точку сплайна
- присутствует возможность перемещать точки сплайна перетягиванием
- присутствует возможность перемещаться по рабочей области перетягиванием за свободную область

Описание меню основного окна:
- кнопка Save открывает меню для сохранения текущего сплайна
- кнопка Spline editor открывает меню редактирования сплайна.
- кнопка Reset angles устанавлиает стандартные значения углов

Описания функционала рабочей области основного окна:
- Присутствует возможность вращения объекта перетаскиванием курсора
- Прокрутка колеса мыши изменяет масштаб
