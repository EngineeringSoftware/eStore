from collections import defaultdict
import json
import os
import re
import subprocess
import sys
import statistics

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ESTORE_DIR = os.path.join(REPO_ROOT, "estore")
PLUGIN_COORD = "org.estore:estore-maven-plugin:1.0-SNAPSHOT:codegen"
MVN_TEST_FLAGS = "-Dprofile=true -Dsurefire.redirectTestOutputToFile=false"

NUMBER_OF_RUNS = 5

QUERIES = {
    "Snb01": ["InteractiveDeleteQuery2", "InteractiveDeleteQuery3", "InteractiveDeleteQuery5", "InteractiveShortQuery1", "InteractiveShortQuery5",  "InteractiveUpdateQuery2", "InteractiveUpdateQuery3", "InteractiveUpdateQuery5", "InteractiveUpdateQuery8"],
    "Fin001": ["Tw1", "Tw2", "Tw3", "Tw4",  "Tw8", "Tw9", "Tw13", "Tsr1"]
}

QUERIES_MAP = {
    "Snb01": {
        "SNBQueryA": "InteractiveDeleteQuery2",
        "SNBQueryB": "InteractiveDeleteQuery3",
        "SNBQueryC": "InteractiveDeleteQuery5",
        "SNBQueryD": "InteractiveShortQuery1",
        "SNBQueryE": "InteractiveShortQuery5",
        "SNBQueryF": "InteractiveUpdateQuery2",
        "SNBQueryG": "InteractiveUpdateQuery3",
        "SNBQueryH": "InteractiveUpdateQuery5",
        "SNBQueryI": "InteractiveUpdateQuery8",
    },
}

def extract_times_sorted(output):
    pattern = r'(\w+)\nTime: (\d+\.\d+)ms'
    matches = re.findall(pattern, output)
    queries = [match[0] for match in matches]
    times = [match[1] for match in matches]
    return queries, times

def extract_time(output):
    pattern = r'Time: (\d+\.\d+)ms'
    match = re.search(pattern, output)
    return float(match.group(1)) if match else None

def extract_overhead_times(output):
    method_pattern = r"Time taken for method test(\w+): (\d+) ms"
    total_time_pattern = r"Total Time taken: (\d+) ms"
    method_data = re.findall(method_pattern, output)
    total_time_match = re.search(total_time_pattern, output)
    total_time = int(total_time_match.group(1)) if total_time_match else None
    
    results = {}
    for method, time in method_data:
        results[method] = int(time)
    results["total"] = total_time
    return results

def run_command(command, cwd=ESTORE_DIR):
    result = subprocess.run(command, shell=True, capture_output=True, text=True, cwd=cwd)
    output = (result.stdout + "\n" + result.stderr).strip()
    if result.returncode != 0:
        print(output)
        sys.exit(result.returncode)
    return output

def install_codegen_plugin():
    run_command("mvn -pl estore-maven-plugin -am install -DskipTests", cwd=REPO_ROOT)

def run_codegen():
    return run_command("mvn " + PLUGIN_COORD)

def generate_json_sequence():
    install_codegen_plugin()
    run_codegen()

    def run_test(test_name):
        times = []
        for _ in range(NUMBER_OF_RUNS):
            output = run_command(f"mvn test -Dtest={test_name} {MVN_TEST_FLAGS}")
            _, run_times = extract_times_sorted(output)
            times.append([float(time) for time in run_times])
        return times

    original_times = run_test("Snb01Test")
    transformed_times = run_test("TransformedSnb01Test")

    # Calculate averages
    avg_original = [statistics.mean(t) for t in zip(*original_times)]
    avg_transformed = [statistics.mean(t) for t in zip(*transformed_times)]

    result = {
        "original": avg_original,
        "transformed": avg_transformed
    }

    with open('codegen-sequence.json', 'w') as f:
        json.dump(result, f, indent=2)
    
def generate_json_standalone():
    install_codegen_plugin()
    run_codegen()

    results = {
        "original": defaultdict(lambda: defaultdict(lambda: defaultdict(float))),
        "transformed": defaultdict(lambda: defaultdict(lambda: defaultdict(float)))
    }
    
    for run in range(NUMBER_OF_RUNS):
        for version in ["original", "transformed"]:
            for bench, queries in QUERIES.items():
                for query in queries:
                    test_class = f"{'Transformed' if version == 'transformed' else ''}{bench}Test"
                    command = f"mvn test -Dtest={test_class}#test{query} {MVN_TEST_FLAGS}"
                    output = run_command(command)
                    time = extract_time(output)
                    if time is not None:
                        results[version][bench][query][str(run)] = time
                    else:
                        print(f"Error: {version} {bench} {query} failed")
                        print(output)
                        sys.exit(1)
                    print(f"Run {run + 1}/{NUMBER_OF_RUNS}: {version} {bench} {query} completed")

    # Calculate averages
    for version in results:
        for bench in results[version]:
            for query, runs in results[version][bench].items():
                run_times = [time for run, time in runs.items() if run != "average"]
                if run_times:
                    runs["average"] = round(sum(run_times) / len(run_times), 2)
                else:
                    runs["average"] = 0  # or None, depending on how you want to handle missing data

    results["queries"] = QUERIES_MAP["Snb01"]
    # Save results to JSON file
    with open('codegen-standalone.json', 'w') as f:
        json.dump(results, f, indent=2)
    
    print("Results have been saved to codegen-standalone.json")

def generate_json_overhead():
    install_codegen_plugin()
    results = {}
    averages = {}
    for run in range(NUMBER_OF_RUNS):
        output = run_codegen()
        data = extract_overhead_times(output)
        for bench, quries in QUERIES.items():
            for query in quries:
                if query not in data:
                    print(f"Error: missing codegen time for {query}")
                    print(output)
                    sys.exit(1)
                if bench not in results:
                    results[bench] = {}
                    averages[bench] = {}
                if query not in results[bench]:
                    results[bench][query] = {}
                    averages[bench][query] = []
                results[bench][query][str(run)] = data[query]
                averages[bench][query].append(data[query])
        if data["total"] is None:
            print("Error: missing Total Time taken")
            print(output)
            sys.exit(1)
        if "total" not in results:
            results["total"] = {}
            averages["total"] = []
        results["total"][str(run)] = data["total"]
        averages["total"].append(data["total"])
    
    # Calculate averages
    for bench in averages:
        if bench == "total":
            results["total"]["average"] = statistics.mean(averages["total"])
        else:
            for query in averages[bench]:
                results[bench][query]["average"] = statistics.mean(averages[bench][query])

    with open('codegen-overhead.json', 'w') as f:
        json.dump(results, f, indent=2)
    
    print("Results have been saved to codegen-overhead.json")

    
if __name__ == '__main__':
    if len(sys.argv) != 2 or sys.argv[1] not in ['standalone', 'sequence', 'overhead']:
        print("Usage: python scripts/gen_codegen_data.py [standalone|sequence|overhead]")
        sys.exit(1)
    
    if sys.argv[1] == 'sequence':
        generate_json_sequence()
    if sys.argv[1] == 'standalone':
        generate_json_standalone()
    if sys.argv[1] == 'overhead':
        generate_json_overhead()
