#include "expensetab.h"
#include "ui_expensetab.h"
#include "expensemodel.h"

#include <QDebug>

#include <QSqlQueryModel>
#include <QSqlRelationalDelegate>
#include <QSqlRecord>
#include <QSqlError>

#include <QCalendarWidget>
#include <QFileDialog>
#include <QHeaderView>
#include <QBuffer>

ExpenseTab::ExpenseTab(QWidget *parent)
    :QWidget(parent), _ui(new Ui::ExpenseTab)
{
    _ui->setupUi(this);

    connect(_ui->calendarWidget, &QCalendarWidget::clicked, _ui->dateEdit, &QDateEdit::setDate);
    connect(_ui->dateEdit, &QDateEdit::dateChanged, _ui->calendarWidget, &QCalendarWidget::setSelectedDate);

    connect(_ui->capturePushButton, &QPushButton::clicked, this, &ExpenseTab::_onCapturePushButtonClicked);
    connect(_ui->expensePushButton, &QPushButton::clicked, this, &ExpenseTab::_onExpensePushButtonClicked);
    connect(_ui->deletePushButtom, &QPushButton::clicked, this, &ExpenseTab::_onDeletePushButtomClicked);

    _ui->dateEdit->setDate(QDate::currentDate());

    _ui->expenseTableView->setEditTriggers(QTableView::NoEditTriggers);
    _ui->expenseTableView->setSelectionBehavior(QTableView::SelectRows);
    _ui->expenseTableView->setSelectionMode(QTableView::SingleSelection);
    _ui->expenseTableView->verticalHeader()->hide();
    _ui->expenseTableView->horizontalHeader()->setStretchLastSection(true);
}

void ExpenseTab::loadData()
{
    //category
    QSqlQueryModel *categoryModel= new QSqlQueryModel(this);
    categoryModel->setQuery("SELECT category FROM categories");
    _ui->categoryListView->setModel(categoryModel);
    _ui->categoryListView->setCurrentIndex(categoryModel->index(0, 0));

    //expenses
    ExpenseModel *expenseModel = new ExpenseModel(this);
    expenseModel->setTable("expenses");
    expenseModel->setEditStrategy(ExpenseModel::OnManualSubmit);

    expenseModel->setHeaderData(1, Qt::Horizontal, "照片");
    expenseModel->setHeaderData(2, Qt::Horizontal, "金額");
    expenseModel->setHeaderData(3, Qt::Horizontal, "日期");
    expenseModel->setHeaderData(4, Qt::Horizontal, "分類");
    expenseModel->setHeaderData(5, Qt::Horizontal, "筆記");
    expenseModel->setSort(0, Qt::DescendingOrder);
    expenseModel->setRelation(4, QSqlRelation("categories", "_id", "category"));
    expenseModel->select();

    _ui->expenseTableView->setModel(expenseModel);
    //_ui->expenseTableView->setItemDelegateForColumn(4, new QSqlRelationalDelegate(_ui->expenseTableView));
    _ui->expenseTableView->setColumnHidden(0, true);
}

ExpenseTab::~ExpenseTab()
{
    delete _ui;
}

void ExpenseTab::_onCapturePushButtonClicked()
{
    QString picturePath = QFileDialog::getOpenFileName(this, "選擇照片", QDir::homePath(), "照片 (*.jpg)");

    if(picturePath.isEmpty())
        return;

    if(!_currentPicture.load(picturePath, "JPG"))
        return;

    // 5:3
    _ui->pictureLabel->setPixmap(_currentPicture.scaled(235, 141, Qt::KeepAspectRatio));
}


void ExpenseTab::_onExpensePushButtonClicked()
{
    QSqlRelationalTableModel *expenseModel = qobject_cast<ExpenseModel*>(_ui->expenseTableView->model());
    expenseModel->insertRow(0);

    QByteArray bytes;
    QBuffer buffer(&bytes);
    buffer.open(QIODevice::WriteOnly);
    _currentPicture.save(&buffer, "JPG");

    expenseModel->setData(expenseModel->index(0, 1), bytes);
    expenseModel->setData(expenseModel->index(0, 2),  _ui->expenseLineEdit->text().toUInt());
    expenseModel->setData(expenseModel->index(0, 3), _ui->dateEdit->text());
    expenseModel->setData(expenseModel->index(0, 4), _ui->categoryListView->currentIndex().row() + 1);
    expenseModel->setData(expenseModel->index(0, 5), _ui->noteTextEdit->toPlainText());

    if(!expenseModel->submitAll())
        qDebug() << expenseModel->lastError();
}

void ExpenseTab::_onDeletePushButtomClicked()
{
    QSqlRelationalTableModel *expenseModel = qobject_cast<ExpenseModel*>(_ui->expenseTableView->model());
    int row = _ui->expenseTableView->currentIndex().row();
    expenseModel->removeRow(row);
    if(!expenseModel->submitAll())
        qDebug() << expenseModel->lastError();
}
