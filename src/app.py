from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from fastapi.requests import Request
from fastapi.responses import JSONResponse

from src.containers import AppContainer
from src.route import set_router

INIT_DB = True

if INIT_DB:
    from src.common.db_utils import init

    init(["users"])
app = FastAPI()
app.container = AppContainer()
set_router(app)


# RequestValidationError to 400 Bad Request
@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    return JSONResponse(
        status_code=400,
        content=exc.errors(),
    )


@app.get("/health")
async def health():
    return {"status": "ok"}
