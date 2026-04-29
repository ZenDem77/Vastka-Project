package com.example.vatska
class OrdersPresenter(
    private val view: OrdersContract.View,
    private val repository: OrderRepository
) : OrdersContract.Presenter {

    override fun loadOrders() {
        val all       = repository.getOrders()
        val pending   = all.filter { it.endsWith("|||Pending") }
        val completed = all.filter { it.endsWith("|||Completed") }
        view.showOrders(pending, completed)
    }

    override fun onCompleteClicked(orderString: String) {
        repository.markAsCompleted(orderString)
        view.refreshOrders()
    }
}