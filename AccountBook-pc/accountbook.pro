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
INCLUDEPATH += src

SOURCES += \
    src/main.cpp\
    src/mainwidget.cpp \
    src/statisticstab.cpp \
    src/settingtab.cpp \
    src/expensetab.cpp \
    src/strings.cpp \
    src/expensemodel.cpp

HEADERS  += \
    src/mainwidget.h \
    src/statisticstab.h \
    src/settingtab.h \
    src/expensetab.h \
    src/globals.h \
    src/strings.h \
    src/expensemodel.h

FORMS    += \
    src/mainwidget.ui \
    src/statisticstab.ui \
    src/expensetab.ui \
    src/settingtab.ui

OTHER_FILES +=

RESOURCES += \
    resource.qrc
