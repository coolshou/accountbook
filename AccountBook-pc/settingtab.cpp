#include "settingtab.h"
#include "ui_settingtab.h"

#include <QDebug>
#include <QFileDialog>
#include <QSqlTableModel>
#include <QSqlError>


SettingTab::SettingTab(QWidget *parent) :
    QWidget(parent),
    _ui(new Ui::SettingTab)
{
    _ui->setupUi(this);

    //connect(_ui->changeDatabasePushButton, SIGNAL(clicked()), SLOT(onChangeDatabasePushButtonClicked()));
    connect(_ui->changeDatabasePushButton, &QPushButton::clicked, this, &SettingTab::onChangeDatabasePushButtonClicked);

    connect(_ui->addCategoryPushButton, &QPushButton::clicked, this, &SettingTab::_onAddCategoryPushButtonClicked);
    connect(_ui->deleteCategoryPushButton, &QPushButton::clicked, this, &SettingTab::_onDeleteCategoryPushButtonClicked);

    _ui->categoryListView->setEditTriggers(QListView::NoEditTriggers);
}

void SettingTab::setDatabasePath(const QString &databasePath)
{
    if(databasePath == _ui->databasePathLineEdit->text())
        return;

    _ui->databasePathLineEdit->setText(databasePath);
    emit databasePathChanged(databasePath);
}

void SettingTab::loadData()
{
    //category
    QSqlTableModel *categoryModel= new QSqlTableModel(this);
    categoryModel->setTable("categories");
    categoryModel->setEditStrategy(QSqlTableModel::OnManualSubmit);
    categoryModel->select();
    _ui->categoryListView->setModel(categoryModel);
    _ui->categoryListView->setModelColumn(1);
}

SettingTab::~SettingTab()
{
    delete _ui;
}

void SettingTab::onChangeDatabasePushButtonClicked()
{
    QString databasePath = QFileDialog::getOpenFileName(this, "選擇資料庫的位置和名稱", QDir::homePath() + "/accountbook.db");

    if(!databasePath.isEmpty())
        setDatabasePath(databasePath);

}


void SettingTab::_onAddCategoryPushButtonClicked()
{
    QString category = _ui->categoryLineEdit->text();
    if(category.isEmpty())
        return;

    QSqlTableModel *categoryModel = qobject_cast<QSqlTableModel*>(_ui->categoryListView->model());

    int row = categoryModel->rowCount();
    categoryModel->insertRow(row);

    categoryModel->setData(categoryModel->index(row, 1), category);

    if(!categoryModel->submitAll())
        qDebug() << categoryModel->lastError();
}

void SettingTab::_onDeleteCategoryPushButtonClicked()
{
    QSqlTableModel *categoryModel = qobject_cast<QSqlTableModel*>(_ui->categoryListView->model());
    int row = _ui->categoryListView->currentIndex().row();
    categoryModel->removeRow(row);
    if(!categoryModel->submitAll())
        qDebug() << categoryModel->lastError();
}

