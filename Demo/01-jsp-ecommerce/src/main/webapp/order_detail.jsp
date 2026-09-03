<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.legacy.shop.model.Order, com.legacy.shop.model.OrderItem" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Confirmation - Legacy Store</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; background-color: #f4f4f4; }
        .container { background: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); max-width: 700px; margin: 0 auto; }
        .status-badge { background: #28a745; color: white; padding: 5px 10px; border-radius: 4px; font-weight: bold; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background: #f8f9fa; }
        .summary-box { margin-top: 20px; border-top: 2px solid #eee; padding-top: 15px; text-align: right; }
        .btn { display: inline-block; background: #007bff; color: white; padding: 10px 18px; text-decoration: none; border-radius: 4px; margin-top: 15px; }
    </style>
</head>
<body>
<div class="container">
    <%
        Order order = (Order) request.getAttribute("order");
        if (order == null) {
            out.println("<p style='color:red;'>Order details could not be loaded.</p>");
        } else {
    %>
        <h2>Order Confirmation</h2>
        <p>Order Number: <strong><%= order.getOrderNumber() %></strong></p>
        <p>Status: <span class="status-badge"><%= order.getStatus() %></span></p>
        <p>Date: <%= order.getCreatedAt() %></p>
        <hr/>
        <p><strong>Customer:</strong> <%= order.getCustomerName() %> (<%= order.getCustomerEmail() %>)</p>
        <p><strong>Ship to:</strong> <%= order.getShippingAddress() %></p>
        
        <h3>Ordered Items</h3>
        <table>
            <thead>
                <tr>
                    <th>Product</th>
                    <th>Qty</th>
                    <th>Unit Price</th>
                    <th>Subtotal</th>
                </tr>
            </thead>
            <tbody>
            <%
                if (order.getItems() != null) {
                    for (OrderItem item : order.getItems()) {
            %>
                <tr>
                    <td><%= item.getProductName() %></td>
                    <td><%= item.getQuantity() %></td>
                    <td>$<%= item.getUnitPrice() %></td>
                    <td>$<%= item.getSubtotal() %></td>
                </tr>
            <%
                    }
                }
            %>
            </tbody>
        </table>

        <div class="summary-box">
            <p>Raw Subtotal: $<%= order.getRawSubtotal() %></p>
            <p>Coupon Discount (<%= order.getAppliedCoupon() != null ? order.getAppliedCoupon() : "None" %>): -$<%= order.getDiscountAmount() %></p>
            <h3>Final Charged: $<%= order.getTotalAmount() %></h3>
        </div>

        <a href="cart.jsp" class="btn">Shop Again</a>
    <%
        }
    %>
</div>
</body>
</html>
