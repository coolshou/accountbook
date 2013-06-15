#-------------------------------------------------
#
# Project created by QtCreator 2013-03-21T16:08:49
#
#-------------------------------------------------

QT       += core gui sql

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = accountbook
TEMPLATE = app

CONFIG += c++11

SOURCES += main.cpp\
    mainwidget.cpp \
    statisticstab.cpp \
    settingtab.cpp \
    expensetab.cpp \
    strings.cpp \
    expensemodel.cpp

HEADERS  += \
    mainwidget.h \
    statisticstab.h \
    settingtab.h \
    expensetab.h \
    globals.h \
    strings.h \
    expensemodel.h

FORMS    += \
    mainwidget.ui \
    statisticstab.ui \
    expensetab.ui \
    settingtab.ui

OTHER_FILES +=

RESOURCES += \
    resource.qrc
