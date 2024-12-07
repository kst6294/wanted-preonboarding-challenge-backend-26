import os
import time

from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker

DATABASE_URL = os.getenv("DATABASE_URL", "sqlite:///./sql_app.db")


def create_db_engine_with_retry(retry_count=10, retry_delay=5):
    for attempt in range(retry_count):
        try:
            print(f"DATABASE_URL: {DATABASE_URL}")
            _engine = create_engine(
                DATABASE_URL,
                connect_args=(
                    {"check_same_thread": False} if "sqlite" in DATABASE_URL else {}
                ),
                pool_pre_ping=True,
            )
            print("start connect...")
            with _engine.connect() as conn:
                print("데이터베이스에 성공적으로 연결되었습니다.")
            return _engine
        except Exception as e:
            print(
                f"데이터베이스 연결 실패: {e}. 재시도 중... ({attempt + 1}/{retry_count})"
            )
            time.sleep(retry_delay)
    raise Exception("데이터베이스에 연결할 수 없습니다. 모든 재시도 실패.")


engine = create_db_engine_with_retry()
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
