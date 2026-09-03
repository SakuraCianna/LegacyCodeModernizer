<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Checkout - Legacy Store</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; background-color: #f4f4f4; }
        .container { background: #fff; padding: 20px; border-radius: 6px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"] { width: 300px; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
        .btn { background: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }
    </style>
</head>
<body>
<div class="container">
    <h2>Checkout & Place Order</h2>
    
    <%
        HttpSession currentSession = request.getSession();
        List<String> cartItems = (List<String>) currentSession.getAttribute("CART_ITEMS");
        if (cartItems == null || cartItems.isEmpty()) {
    %>
        <p style="color:red;">Your cart is empty! <a href="cart.jsp">Return to Shop</a></p>
    <%
        } else {
    %>
        <form method="POST" action="order">
            <div class="form-group">
                <label>Customer Name:</label>
                <input type="text" name="customerName" required />
            </div>
            <div class="form-group">
                <label>Shipping Address:</label>
                <input type="text" name="shippingAddress" required />
            </div>
            <div class="form-group">
                <label>Payment Method:</label>
                <select name="paymentMethod" style="padding: 8px;">
                    <option value="CREDIT_CARD">Credit Card</option>
                    <option value="PAYPAL">PayPal</option>
                    <option value="BANK_TRANSFER">Bank Transfer</option>
                </select>
            </div>
            <button type="submit" class="btn">Confirm and Pay</button>
        </form>
    <%
        }
    %>
</div>
</body>
</html>
