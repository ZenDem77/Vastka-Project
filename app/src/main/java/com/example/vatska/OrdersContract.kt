package com.example.vatska
interface OrdersContract {

    interface View {
        fun showOrders(pending: List<String>, completed: List<String>)
        fun refreshOrders()
    }

    interface Presenter {
        fun loadOrders()
        fun onCompleteClicked(orderString: String)
    }
}