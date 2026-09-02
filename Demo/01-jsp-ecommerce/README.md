# 🛍️ Demo 01: JSP Monolithic E-Commerce System

## 1. 赛道说明
- **迁移类型**：JSP ➔ Java 21 / Spring Boot 3.4 & 现代 REST API
- **核心技术债特征**：
  1. `cart.jsp` 中直接内嵌 Java Scriptlet (`<% ... %>`) 和原始 JDBC 直连操作；
  2. 强依赖 `HttpSession` 存储购物车状态（单体有状态，阻碍微服务集群横向扩展）；
  3. `OrderDAO.java` 中存在字符串拼接 SQL，存在 SQL 注入风险；
  4. 使用老旧的 `HttpServlet`、`javax.servlet.*` 与 `java.util.Date`。

## 2. 现代化目标
- **Spring Boot 3.4 REST 控制器**：`CartRestController.java` 与 `OrderRestController.java`；
- **强类型 DTO Record**：`CartItemRequest.java`, `OrderResponse.java`；
- **Spring Data JPA & 事务安全**：`OrderRepository.java`；
- **无状态 JWT 鉴权**：替代原生 `HttpSession`。
