package com.legacy.shop.servlet;

import com.legacy.shop.dao.OrderDAO;
import com.legacy.shop.model.Order;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Legacy HttpServlet for handling checkout and order creation.
 */
public class OrderServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String customerName = request.getParameter("customerName");
        List<Order> orders = orderDAO.findOrdersByCustomer(customerName != null ? customerName : "");
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h2>Orders for: " + customerName + "</h2>");
        out.println("<ul>");
        for (Order o : orders) {
            out.println("<li>Order #" + o.getOrderNumber() + " - Total: $" + o.getTotalAmount() + " - Status: " + o.getStatus() + "</li>");
        }
        out.println("</ul>");
        out.println("<a href='cart.jsp'>Back to Cart</a>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        
        List<String> cartItems = (List<String>) session.getAttribute("CART_ITEMS");
        if (cartItems == null || cartItems.isEmpty()) {
            response.sendRedirect("cart.jsp");
            return;
        }

        String customerName = request.getParameter("customerName");
        String shippingAddress = request.getParameter("shippingAddress");

        // Calculate total amount from session cart strings
        double total = 0.0;
        for (String item : cartItems) {
            String[] parts = item.split(":");
            if (parts.length >= 3) {
                int qty = Integer.parseInt(parts[1]);
                double price = Double.parseDouble(parts[2]);
                total += qty * price;
            }
        }

        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setTotalAmount(total);
        order.setStatus("PAID");
        order.setCreatedAt(new Date());

        boolean saved = orderDAO.saveOrder(order);

        if (saved) {
            session.removeAttribute("CART_ITEMS");
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2 style='color:green;'>Order Placed Successfully!</h2>");
            out.println("<p>Order Number: <strong>" + order.getOrderNumber() + "</strong></p>");
            out.println("<p>Total Paid: $" + order.getTotalAmount() + "</p>");
            out.println("<a href='cart.jsp'>Continue Shopping</a>");
            out.println("</body></html>");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to persist order in database.");
        }
    }
}
