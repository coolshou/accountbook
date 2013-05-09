#-------------------------------------------------
#
# Project created by QtCreator 2013-03-21T16:08:49
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = accountbook
TEMPLATE = app


SOURCES += main.cpp\
    mainwidget.cpp \
    statisticstab.cpp \
    settingtab.cpp \
    expensetab.cpp

HEADERS  += \
    mainwidget.h \
    statisticstab.h \
    settingtab.h \
    expensetab.h

FORMS    += \
    mainwidget.ui \
    statisticstab.ui \
    expensetab.ui \
    settingtab.ui

OTHER_FILES +=

RESOURCES += \
    resource.qrc
