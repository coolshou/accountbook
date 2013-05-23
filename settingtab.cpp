#include "settingtab.h"
#include "ui_settingtab.h"

#include <QDebug>
#include <QFileDialog>


SettingTab::SettingTab(QWidget *parent) :
    QWidget(parent),
    _ui(new Ui::SettingTab)
{
    _ui->setupUi(this);

    //connect(_ui->changeDatabasePushButton, SIGNAL(clicked()), SLOT(onChangeDatabasePushButtonClicked()));
    connect(_ui->changeDatabasePushButton, &QPushButton::clicked, this, &SettingTab::onChangeDatabasePushButtonClicked);
}

void SettingTab::setDatabasePath(const QString &databasePath)
{
    if(databasePath == _ui->databasePathLineEdit->text())
        return;

    _ui->databasePathLineEdit->setText(databasePath);
    emit databasePathChanged(databasePath);
}

SettingTab::~SettingTab()
{
    delete _ui;
}

void SettingTab::onChangeDatabasePushButtonClicked()
{
    QString databasePath = QFileDialog::getSaveFileName(this, "選擇資料庫的位置和名稱", QDir::homePath() + "/accountbook.db");

    if(!databasePath.isEmpty())
        setDatabasePath(databasePath);

}


