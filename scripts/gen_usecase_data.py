import json
import os
import subprocess
import sys

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ESTORE_DIR = os.path.join(REPO_ROOT, "estore")
MVN_TEST_FLAGS = "-Dsurefire.redirectTestOutputToFile=false"

def run_command(command):
    result = subprocess.run(command, shell=True, capture_output=True, text=True, cwd=ESTORE_DIR)
    output = (result.stdout + "\n" + result.stderr).strip()
    if result.returncode != 0:
        print(output)
        sys.exit(result.returncode)
    return output

def extract_data(output, is_jdbc=False):
    if is_jdbc:
        total_query_time = None
        for line in output.split("\n"):
            if "Total Query Time" in line:
                total_query_time = int(line.split(":")[1].strip())
        return {"Total Query Time": total_query_time}
    else:
        data = {}
        metrics = ["Parse Tree Generation Time", "Query Plan AST Building Time", "Query Execution Time", "Total Query Time"]
        for line in output.split("\n"):
            for metric in metrics:
                if metric in line:
                    data[metric] = int(line.split(":")[1].strip())
        return data

def generate_json(data_type):
    if data_type == 'h2':
        queries = ["getCatalogs", "getSchemas", "getTables"]
        implementations = {
            "egraph-reflection": "\\ToolReflection",
            "jdbc": "\\JDBC"
        }
        test_class = "H2MetadataTest"
    elif data_type == 'egraph':
        queries = ["dbName", "options", "dynamicClass"]
        implementations = {
            "egraph-reflection": "\\ToolReflection",
        }
        test_class = "EstoreMetadataTest"
    
    data = []
    for query in queries:
        query_data = {query: []}
        for impl in implementations:
            impl_data = {}
            if impl == "jdbc":
                command = f"mvn test -Dtest={test_class}#test{query[3:]}JDBCRepeat -Dprofile=true {MVN_TEST_FLAGS}"
                total_query_times = []
                for _ in range(3):
                    output = run_command(command)
                    total_query_time = extract_data(output, is_jdbc=True)["Total Query Time"]
                    if total_query_time is None:
                        print(f"Error: missing Total Query Time for {query} jdbc")
                        print(output)
                        sys.exit(1)
                    total_query_times.append(total_query_time)
                impl_data[impl] = {
                    "Total Query Time": {
                        "0": total_query_times[0],
                        "1": total_query_times[1],
                        "2": total_query_times[2],
                        "average": sum(total_query_times) / len(total_query_times)
                    }
                }
            else:
                queryName = query[0].upper() + query[1:] if data_type == 'egraph' else query[3:]
                suffix = "ESTORE" if data_type == 'h2' else ""
                command = f"mvn test -Dtest={test_class}#test{queryName}{suffix}Repeat -Dprofile=true {MVN_TEST_FLAGS}"
                # print(command)
                metrics = ["Parse Tree Generation Time", "Query Plan AST Building Time", "Query Execution Time", "Total Query Time"]
                metric_data = {metric: [] for metric in metrics}
                for _ in range(3):
                    output = run_command(command)
                    run_data = extract_data(output)
                    for metric in metrics:
                        if metric not in run_data:
                            print(f"Error: missing {metric} for {query} {impl}")
                            print(output)
                            sys.exit(1)
                        metric_data[metric].append(run_data[metric])
                for metric in metrics:
                    values = metric_data[metric]
                    metric_data[metric] = {
                        "0": values[0],
                        "1": values[1],
                        "2": values[2],
                        "average": sum(values) / len(values)
                    }
                impl_data[impl] = metric_data
            query_data[query].append(impl_data)
        data.append(query_data)
    
    json_data = {
        "queries": queries,
        "implementations": implementations,
        "data": data
    }
    
    with open("output.json", "w") as file:
        json.dump(json_data, file, indent=4)

if __name__ == '__main__':
    if len(sys.argv) != 2 or sys.argv[1] not in ['h2', 'egraph']:
        print("Usage: python scripts/gen_usecase_data.py [h2|egraph]")
        sys.exit(1)
    
    generate_json(sys.argv[1])
