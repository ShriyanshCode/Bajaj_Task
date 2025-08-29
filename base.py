import os

# Define your directories
src_main = "src/main"
src_config = "src/config"
src_service = "src/service"
src_dto = "src/dto"
resources = "resources"
queries_dir = "queries"
output_dir = "output"

# Create them
for d in [src_main, src_config, src_service, src_dto, resources, queries_dir, output_dir]:
    os.makedirs(d, exist_ok=True)
