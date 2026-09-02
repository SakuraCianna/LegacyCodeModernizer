# 🐍 Demo 02: Python 2.7 Financial Data Ingestion & ETL Pipeline

## 1. 赛道说明
- **迁移类型**：Python 2.7 ➔ Python 3.12+ 强类型注解 & 现代 Asyncio 异步化
- **核心技术债特征**：
  1. 使用已弃用的标准库：`urllib2`, `ConfigParser`, `Queue.Queue`, `cPickle`；
  2. Python 2 特有语法：`print >> sys.stderr`, `xrange()`, `dict.has_key()`, `except Exception, e`；
  3. `str` 与 `unicode` 类型混淆，缺少 Python 3 严格的字节流与字符串区分；
  4. 多线程阻塞式 I/O，缺少现代 `asyncio` 并发调度与 `typing` 类型约束。

## 2. 现代化目标
- **Python 3.12+ 原生语法**：`f-string`, `except Exception as e`, `queue.Queue`；
- **异步 I/O 网络层**：使用 `httpx.AsyncClient` 与 `asyncio.gather` 替换 `urllib2`；
- **强类型与数据契约**：引入 `pydantic` 与 `typing.Dict`, `typing.List`；
- **现代配置解析**：使用 `configparser` 与类型化配置类。
