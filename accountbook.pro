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
    settingstab.cpp \
    paytab.cpp \
    statisticstab.cpp

HEADERS  += \
    mainwidget.h \
    settingstab.h \
    paytab.h \
    statisticstab.h

FORMS    += \
    mainwidget.ui \
    settingstab.ui \
    paytab.ui \
    statisticstab.ui

OTHER_FILES +=

RESOURCES += \
    resource.qrc
