package com.legacy.shop.servlet;

import com.legacy.shop.dao.ProductDAO;
import com.legacy.shop.model.Product;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CartServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("SESSION_CART");
        if (cart == null) {
            cart = new HashMap<Long, Integer>();
            session.setAttribute("SESSION_CART", cart);
        }

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        StringBuilder json = new StringBuilder("{\"cartSize\":").append(cart.size()).append(",\"items\":[");
        int count = 0;
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            if (count > 0) json.append(",");
            json.append("{\"productId\":").append(entry.getKey()).append(",\"quantity\":").append(entry.getValue()).append("}");
            count++;
        }
        json.append("]}");
        out.print(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String productIdStr = req.getParameter("productId");
        String quantityStr = req.getParameter("quantity");

        HttpSession session = req.getSession(true);
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("SESSION_CART");
        if (cart == null) {
            cart = new HashMap<Long, Integer>();
            session.setAttribute("SESSION_CART", cart);
        }

        if ("add".equalsIgnoreCase(action) && productIdStr != null) {
            Long productId = Long.parseLong(productIdStr);
            int qty = (quantityStr != null) ? Integer.parseInt(quantityStr) : 1;
            
            // Validate positive quantity (Fool-proof boundary guard)
            if (qty <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quantity must be greater than zero");
                return;
            }

            cart.put(productId, cart.getOrDefault(productId, 0) + qty);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"status\":\"SUCCESS\",\"productId\":" + productId + ",\"newQuantity\":" + cart.get(productId) + "}");
        } else if ("clear".equalsIgnoreCase(action)) {
            cart.clear();
            resp.getWriter().write("{\"status\":\"CLEARED\"}");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action or parameters");
        }
    }
}
