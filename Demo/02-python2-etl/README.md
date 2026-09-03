# 🐍 Demo 02: Python 2.7 Financial Ticker & Risk Scoring Batch Engine

## 1. 业务场景说明
这是一个经典的**高吞吐量金融投资组合风险评测与外汇清洗批处理引擎**。系统包含：
1. **多端点限流轮询采集模块**（Token Bucket 令牌桶限流、重试退避机制、`urllib2` HTTP 客户端）；
2. **多币种实时折算与风险度量核心**（外汇汇率安全换算、组合杠杆率、波动率标准差、95% 置信度 VaR 风险价值、Sharpe 比率计算与零除保护）；
3. **多线程并发批处理队列与哨兵机制**（`Queue.Queue`、Worker 线程池、Poison Pill 停机机制）；
4. **事务级审计追踪与二进制状态快照**（多线程文件写锁、JSON 审计流水、`cPickle` 状态归档与 CSV 导出）；
5. **单元测试与行为断言契约**（`test_etl_pipeline.py`）。

## 2. 遗留技术债与坏味道 (Antipatterns)
- **过时标准库**：`urllib2`, `cookielib`, `ConfigParser`, `Queue.Queue`, `cPickle`；
- **Python 2 特有语法**：`print >> sys.stderr`, `xrange()`, `dict.has_key()`, `except Exception, err`；
- **类型混淆**：`types.StringType` / `types.UnicodeType`，无类型约束；
- **面向过程与全局状态耦合**：大量字典无约束传递，缺乏 Pydantic 强类型数据契约。

## 3. 现代化目标 (Target Stack)
- **Python 3.12+ 现代语言特性**：`f-string`、`match-case`、`typing` 类型注解；
- **异步高并发 I/O**：使用 `httpx.AsyncClient` 与 `asyncio.gather` 替换多线程阻塞轮询；
- **数据契约强类型化**：使用 `pydantic.BaseModel` 封装金融数据模型，杜绝脏数据注入；
- **安全序列化**：替换不安全的 `cPickle`，使用现代 typed JSON / Parquet 归档。
