import importlib
import os
from typing import List

from sqlalchemy import inspect

from src.database import Base, engine


def row_to_dict(row):
    return {key: getattr(row, key) for key in inspect(row).attrs.keys()}


def import_all_models(subdirectories):
    # for subdir in subdirectories:
    #     module_name = f"src.{subdir}.infra.db_models"
    #     _path = os.path.join(*module_name.split("."))
    #     for filename in os.listdir(_path):
    #         if filename.endswith(".py") and filename != "__inist__.py":
    #             model_name = filename.replace(".py", "")
    #             try:
    #                 importlib.import_module(f"{module_name}.{model_name}")
    #             except ModuleNotFoundError as e:
    #                 print(f"Module {model_name} not found: {e}")
    pass

def init(models: List[str]):
    import_all_models(models)
    Base.metadata.create_all(bind=engine)
