<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*, java.util.*, com.legacy.shop.model.Order" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Shopping Cart - Legacy Store</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; background-color: #f4f4f4; }
        .container { background: #fff; padding: 20px; border-radius: 6px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #007bff; color: white; }
        .btn { background: #28a745; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px; border: none; cursor: pointer; }
    </style>
</head>
<body>
<div class="container">
    <h2>Your Shopping Cart</h2>

    <%
        HttpSession currentSession = request.getSession();
        List<String> cartItems = (List<String>) currentSession.getAttribute("CART_ITEMS");
        if (cartItems == null) {
            cartItems = new ArrayList<String>();
            currentSession.setAttribute("CART_ITEMS", cartItems);
        }

        String action = request.getParameter("action");
        if ("add".equals(action)) {
            String itemId = request.getParameter("itemId");
            String qtyStr = request.getParameter("quantity");
            int quantity = (qtyStr != null) ? Integer.parseInt(qtyStr) : 1;

            // Legacy Anti-Pattern: Direct JDBC inside JSP scriptlet
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop_db", "root", "root123");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, price, stock FROM products WHERE id = " + itemId);
                
                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    if (stock >= quantity) {
                        cartItems.add(itemId + ":" + quantity + ":" + rs.getDouble("price"));
                        out.println("<p style='color:green;'>Item added successfully!</p>");
                    } else {
                        out.println("<p style='color:red;'>Insufficient stock available!</p>");
                    }
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                out.println("<p style='color:red;'>Database Error: " + e.getMessage() + "</p>");
            }
        }
    %>

    <form method="POST" action="cart.jsp?action=add">
        <label>Select Item ID:</label>
        <input type="text" name="itemId" value="101" required />
        <label>Quantity:</label>
        <input type="number" name="quantity" value="1" min="1" required />
        <button type="submit" class="btn">Add to Cart</button>
    </form>

    <h3>Cart Contents (<%= cartItems.size() %> items)</h3>
    <table>
        <thead>
            <tr>
                <th>Item Details</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <%
            for (int i = 0; i < cartItems.size(); i++) {
        %>
            <tr>
                <td><%= cartItems.get(i) %></td>
                <td><a href="cart.jsp?action=remove&index=<%= i %>" style="color:red;">Remove</a></td>
            </tr>
        <%
            }
        %>
        </tbody>
    </table>

    <div style="margin-top: 20px;">
        <a href="checkout.jsp" class="btn">Proceed to Checkout</a>
    </div>
</div>
</body>
</html>
