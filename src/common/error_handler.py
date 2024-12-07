# # RequestValidationError to 400 Bad Request
# @app.exception_handler(RequestValidationError)
# async def validation_exception_handler(request: Request, exc: RequestValidationError):
#     return JSONResponse(
#         status_code=400,
#         content=exc.errors(),
#     )
