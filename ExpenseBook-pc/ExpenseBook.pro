#-------------------------------------------------
#
# Project created by QtCreator 2013-06-15T18:13:32
#
#-------------------------------------------------

QT       += core gui sql

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = ExpenseBook-pc
TEMPLATE = app


SOURCES += main.cpp\
        mainwindow.cpp \
    globals.cpp \
    expensesqlmodel.cpp

HEADERS  += mainwindow.h \
    globals.h \
    expensesqlmodel.h

FORMS    += mainwindow.ui

RESOURCES += \
    resources.qrc
