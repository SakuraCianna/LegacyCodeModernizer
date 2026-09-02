import Fastify from "fastify";
import cors from "@fastify/cors";
import dotenv from "dotenv";

// Load environment configuration
dotenv.config();

const port = Number(process.env.PORT) || 4000;
const host = process.env.HOST || "0.0.0.0";

// Instantiate Fastify application
const app = Fastify({
  logger: {
    transport: {
      target: process.env.NODE_ENV !== "production" ? "pino-pretty" : "pino",
      options: {
        colorize: true,
        translateTime: "HH:MM:ss Z",
        ignore: "pid,hostname"
      }
    }
  }
});

// Register CORS middleware
await app.register(cors, {
  origin: process.env.FRONTEND_URL || "http://localhost:5173",
  credentials: true
});

// Basic server health-check route
app.get("/api/health", async () => {
  return {
    status: "ok",
    service: "Legacy Code Modernizer Backend",
    version: "0.1.0",
    nodeVersion: process.version,
    timestamp: new Date().toISOString()
  };
});

// Graceful startup function
async function startServer() {
  try {
    await app.listen({ port, host });
    app.log.info(`Legacy Code Modernizer backend running on http://${host}:${port}`);
  } catch (error) {
    app.log.error(error);
    process.exit(1);
  }
}

// Start runtime
startServer();
