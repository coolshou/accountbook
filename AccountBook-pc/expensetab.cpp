#include "expensetab.h"
#include "ui_expensetab.h"
#include "expensemodel.h"
#include "globals.h"

#include <QDebug>

#include <QSqlQueryModel>
#include <QSqlRelationalDelegate>
#include <QSqlRecord>
#include <QSqlError>

#include <QCalendarWidget>
#include <QFileDialog>
#include <QHeaderView>
#include <QBuffer>
#include <QKeyEvent>

ExpenseTab::ExpenseTab(QWidget *parent)
    :QWidget(parent), _ui(new Ui::ExpenseTab), _editMode(false)
{
    _ui->setupUi(this);

    connect(_ui->capturePushButton, &QPushButton::clicked, this, &ExpenseTab::_onCapturePushButtonClicked);
    connect(_ui->expensePushButton, &QPushButton::clicked, this, &ExpenseTab::_onExpensePushButtonClicked);
    connect(_ui->deletePushButtom, &QPushButton::clicked, this, &ExpenseTab::_onDeletePushButtomClicked);

    connect(_ui->expenseTableView, &QTableView::clicked, this, &ExpenseTab::_onExpenseTableViewClicked);

    _clearExpense();

    _ui->dateEdit->setDisplayFormat(AccountBook::DATE_FORMAT);
    _ui->dateEdit->setCalendarPopup(true);

    _ui->expenseTableView->setEditTriggers(QTableView::NoEditTriggers);
    _ui->expenseTableView->setSelectionBehavior(QTableView::SelectRows);
    _ui->expenseTableView->setSelectionMode(QTableView::SingleSelection);
    _ui->expenseTableView->verticalHeader()->hide();
    _ui->expenseTableView->horizontalHeader()->setStretchLastSection(true);

    setFocusPolicy(Qt::StrongFocus);
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

void ExpenseTab::keyPressEvent(QKeyEvent *keyEvent)
{
    switch(keyEvent->key())
    {
    case Qt::Key_Escape: _clearExpense(); break;
    }
}

void ExpenseTab::_clearExpense()
{
    _ui->pictureLabel->clear();
    _ui->expenseLineEdit->clear();
    _ui->dateEdit->setDate(QDate::currentDate());
    _ui->categoryListView->clearSelection();
    _ui->noteTextEdit->clear();

    _ui->expensePushButton->setText("新增");
    _editMode = false;
}

void ExpenseTab::_onCapturePushButtonClicked()
{
    QString picturePath = QFileDialog::getOpenFileName(this, "選擇照片", QDir::homePath(), "照片 (*.jpg)");

    if(picturePath.isEmpty())
        return;

    if(!_currentPicture.load(picturePath, "JPG"))
        return;

    // 5:3
    _ui->pictureLabel->setPixmap(_currentPicture.scaled(300, 180, Qt::KeepAspectRatio));
}


void ExpenseTab::_onExpensePushButtonClicked()
{
    ExpenseModel *expenseModel = qobject_cast<ExpenseModel*>(_ui->expenseTableView->model());

    int row = 0;
    if(_editMode)
        row = _ui->expenseTableView->currentIndex().row();

    if(!_editMode)
        expenseModel->insertRow(row);

    QByteArray bytes;
    QBuffer buffer(&bytes);
    buffer.open(QIODevice::WriteOnly);
    _currentPicture.save(&buffer, "JPG");

    expenseModel->setData(expenseModel->index(row, 1), bytes);
    expenseModel->setData(expenseModel->index(row, 2),  _ui->expenseLineEdit->text().toUInt());
    expenseModel->setData(expenseModel->index(row, 3), _ui->dateEdit->text().trimmed());
    expenseModel->setData(expenseModel->index(row, 4), _ui->categoryListView->currentIndex().row() + 1);
    expenseModel->setData(expenseModel->index(row, 5), _ui->noteTextEdit->toPlainText());

    if(!expenseModel->submitAll())
        qDebug() << expenseModel->lastError();

    _clearExpense();
}

void ExpenseTab::_onDeletePushButtomClicked()
{
    ExpenseModel *expenseModel = qobject_cast<ExpenseModel*>(_ui->expenseTableView->model());
    int row = _ui->expenseTableView->currentIndex().row();
    expenseModel->removeRow(row);
    if(!expenseModel->submitAll())
        qDebug() << expenseModel->lastError();
}

void ExpenseTab::_onExpenseTableViewClicked()
{
    ExpenseModel *expenseModel = qobject_cast<ExpenseModel*>(_ui->expenseTableView->model());
    QSqlQueryModel *categoryModel = qobject_cast<QSqlQueryModel*>(_ui->categoryListView->model());

    int currentRow = _ui->expenseTableView->currentIndex().row();

    _currentPicture = expenseModel->data(expenseModel->index(currentRow, 1), Qt::DecorationRole).value<QPixmap>();
    if(!_currentPicture.isNull())
        _ui->pictureLabel->setPixmap(_currentPicture.scaled(300, 180, Qt::KeepAspectRatio));
    else
        _ui->pictureLabel->clear();

    unsigned int expense = expenseModel->data(expenseModel->index(currentRow, 2)).toUInt();
    _ui->expenseLineEdit->setText(QString::number(expense));


    QString date = expenseModel->data(expenseModel->index(currentRow, 3)).toString();
    _ui->dateEdit->setDate(QDate::fromString(date, AccountBook::DATE_FORMAT));

    int categoryRow = expenseModel->data(expenseModel->index(currentRow, 4)).toInt() - 1;
    _ui->categoryListView->setCurrentIndex(categoryModel->index(categoryRow, 0));

    QString note = expenseModel->data(expenseModel->index(currentRow, 5)).toString();
    _ui->noteTextEdit->setPlainText(note);

    _editMode = true;
    _ui->expensePushButton->setText("修改");
}
