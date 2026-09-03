package com.legacy.shop.servlet;

import com.legacy.shop.dao.OrderDAO;
import com.legacy.shop.model.Order;
import com.legacy.shop.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String orderNumber = req.getParameter("orderNumber");
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if (orderNumber != null && !orderNumber.trim().isEmpty()) {
            Order order = orderDAO.findByOrderNumber(orderNumber);
            if (order != null) {
                req.setAttribute("order", order);
                req.getRequestDispatcher("order_detail.jsp").forward(req, resp);
                return;
            } else {
                out.println("<h3>Order Not Found: " + orderNumber + "</h3>");
                out.println("<a href='cart.jsp'>Return to Cart</a>");
                return;
            }
        }

        List<Order> recent = orderDAO.listRecentOrders(10);
        out.println("<html><head><title>Recent Orders</title></head><body>");
        out.println("<h2>Recent Completed Orders</h2><ul>");
        for (Order o : recent) {
            out.println("<li><strong>" + o.getOrderNumber() + "</strong> - Customer: " + o.getCustomerName() + " - Total: $" + o.getTotalAmount() + " - Status: " + o.getStatus() + "</li>");
        }
        out.println("</ul><a href='cart.jsp'>Back to Shop</a></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(true);

        // Extract session cart or parse form items
        Map<Long, Integer> items = (Map<Long, Integer>) session.getAttribute("SESSION_CART");
        if (items == null || items.isEmpty()) {
            // Fallback: parse from single item checkout form parameters
            String singleItem = req.getParameter("productId");
            String singleQty = req.getParameter("quantity");
            if (singleItem != null && singleQty != null) {
                items = new HashMap<Long, Integer>();
                items.put(Long.parseLong(singleItem), Integer.parseInt(singleQty));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot checkout with an empty cart");
                return;
            }
        }

        String idempotencyKey = req.getParameter("idempotencyKey");
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            idempotencyKey = "IDEM-" + UUID.randomUUID().toString();
        }

        String customerName = req.getParameter("customerName");
        String customerEmail = req.getParameter("customerEmail");
        String shippingAddress = req.getParameter("shippingAddress");
        String couponCode = req.getParameter("couponCode");

        // Basic parameter validation (Fool-proof check)
        if (customerName == null || customerName.trim().isEmpty() || shippingAddress == null || shippingAddress.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer name and shipping address are mandatory");
            return;
        }

        try {
            Order order = orderService.checkout(
                idempotencyKey,
                customerName.trim(),
                customerEmail != null ? customerEmail.trim() : "customer@example.com",
                shippingAddress.trim(),
                items,
                couponCode
            );

            // Clear session cart upon successful payment
            session.removeAttribute("SESSION_CART");

            resp.sendRedirect("order?orderNumber=" + order.getOrderNumber());
        } catch (IllegalStateException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict for duplicate/overselling
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<h3 style='color:red;'>Order Processing Error: " + e.getMessage() + "</h3>");
            resp.getWriter().println("<a href='checkout.jsp'>Go back and modify</a>");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<h3 style='color:red;'>Transaction Failed: " + e.getMessage() + "</h3>");
            resp.getWriter().println("<a href='cart.jsp'>Return to Cart</a>");
        }
    }
}
