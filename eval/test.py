import argparse
import json
import re
import os
from pathlib import Path
from dataclasses import dataclass, field
from typing import Dict, List
import seutil as su
from statistics import mean
import subprocess

_DIR : Path = Path(os.path.dirname(os.path.realpath(__file__)))
IMAGES_PATH : Path = _DIR / "images"

class PROJECT:
    name : str = ''
    queries : Dict[str, str] = {"find-element" : "testFindElement"}
    workloads : List[str] = ["100", "1000", "10000", "100000", "1000000", "100000000"]
    data_structures : List[str] = []

    def __init__(self,
                 name : str,
                 data_structures : List[str]):
        self.name = name
        self.data_structures = data_structures
#ssalc

@dataclass
class DataSet:
    name : str = ''
    queries : Dict[str, str] = field(default_factory=dict) 
#ssalc

class LDBC_SNB(DataSet):
    queries : Dict[str, str] = {"interactive-delete-query2" : "testInteractiveDeleteQuery2",
                                "interactive-delete-query3" : "testInteractiveDeleteQuery3",
                                "interactive-delete-query5" : "testInteractiveDeleteQuery5",
                                "interactive-short-query1"  : "testInteractiveShortQuery1",
                                "interactive-short-query5"  : "testInteractiveShortQuery5",
                                "interactive-update-query2" : "testInteractiveUpdateQuery2",
                                "interactive-update-query3" : "testInteractiveUpdateQuery3",
                                "interactive-update-query5" : "testInteractiveUpdateQuery5",
                                "interactive-update-query8" : "testInteractiveUpdateQuery8"}
    def __init__(self,
                 name : str):
        super().__init__(name, self.queries)
    #fed

class LDBC_FINBENCH(DataSet):
    queries : Dict[str, str] = {"tw1" : "testTw1",
                                "tw2" : "testTw2",
                                "tw3" : "testTw3",
                                "tw4" : "testTw4",
                                "tw8"  : "testTw8",
                                "tw9"  : "testTw9",
                                "tw13" : "testTw13",
                                "tsr1" : "testTsr1"}
    def __init__(self,
                 name : str):
        super().__init__(name, self.queries)
    #fed

DATASETS : Dict[str, DataSet] = {"LDBC-SNB-0.1" : LDBC_SNB("LDBC-SNB-0.1"),
                                 "LDBC-SNB-0.3" : LDBC_SNB("LDBC-SNB-0.3"),
                                 "LDBC-SNB-1"   : LDBC_SNB("LDBC-SNB-1"),
                                 "LDBC-SNB-3"   : LDBC_SNB("LDBC-SNB-3"),
                                 "LDBC-SNB-10"  : LDBC_SNB("LDBC-SNB-10"),
                                 "LDBC-SNB-30"  : LDBC_SNB("LDBC-SNB-30"),
                                 "LDBC-SNB-100"  : LDBC_SNB("LDBC-SNB-100"),
                                 "LDBC-FINBENCH-0.01"  : LDBC_FINBENCH("LDBC-FINBENCH-001"),
                                 "LDBC-FINBENCH-0.1"  : LDBC_FINBENCH("LDBC-FINBENCH-0.1"),
                                 "LDBC-FINBENCH-0.3"  : LDBC_FINBENCH("LDBC-FINBENCH-0.3"),
                                 "LDBC-FINBENCH-3"  : LDBC_FINBENCH("LDBC-FINBENCH-3"),
                                 "LDBC-FINBENCH-10"  : LDBC_FINBENCH("LDBC-FINBENCH-10")}


SYSTEMS : Dict[str, str] = {"neo4j-server"       : "Neo4jServerTest",
                            "neo4j-impermanant"  : "Neo4jImpermanantTest",
                            "ingraph-reflection" : "InGraphReflectionTest",
                            "estore-neo"            : "EstoreNeoTest",
                            "estore-mem"            : "EstoreMemTest",
                            "memgraph-server"    : "MemgraphServerTest",
                            "arcadedb-embedded"  : "ArcadeDBEmbeddedTest"}

PROJECTS : Dict[str, PROJECT] = {
    "jcf" : PROJECT("JCF",["ArrayList", "LinkedList", "Vector", "ArrayDeque", "HashMap"]),
    "guava" : PROJECT("Guava",["ArrayTable", "HashMultiset"]),
    "eclipse" : PROJECT("Eclipse",["UnifiedSet", "UnifiedMap", "FastList", "ArrayStack", "ImmutableArrayList"])}

def exec_datastructure(project : str,
                       datastructures : List[str],
                       workloads : List[str],
                       systems : List[str],
                       queries : List[str],
                       runs : int):
    project2 : PROJECT = PROJECTS[project]
    if queries is None:
        queries = PROJECT.queries
    #fi
    if systems is None:
        systems = ["ingraph-reflection"]
    #fi
    if datastructures is None:
        datastructures = project2.data_structures
    #fi
    if workloads is None:
        workloads = PROJECT.workloads
    #fi
    exec_result : Dict = {}
    exec_result[project] = {}
    for system in systems:
        exec_result[project][system] = {}
        for query in queries:
            exec_result[project][system][query] = {}
            for datastructure in datastructures:
                exec_result[project][system][query][datastructure] = {}
                for workload in workloads:
                    exec_result[project][system][query][datastructure][workload] = {}
                    file_contents : List[str] = []
                    class_suffix : str = f"{datastructure}{workload}"
                    file_contents.append("#!/bin/bash")
                    if "estore" in system:
                        file_contents.append("export LD_LIBRARY_PATH=/home/libs")
                        file_contents.append("java -jar /home/libs/estore-1.0.0-server.jar &")
                        file_contents.append(f"mvn -q test -Dtest={SYSTEMS[system]}{class_suffix}#{queries[query]} -DargLine='-agentlib:estoreAgent'")
                    else:
                        file_contents.append(f"mvn -q test -Dtest={SYSTEMS[system]}{class_suffix}#{queries[query]}")
                    #fi
                    file_content : str = "\n".join(file_contents)
                    with open(f"{IMAGES_PATH}/execute.sh", "w") as exec_file:
                        exec_file.write(file_content)
                    #htiw
                    result = su.bash.run(cmd = f"docker images {project.lower()}-{datastructure.lower()}{workload}:{system} --format '{{{{.ID}}}}'", check_returncode = 0)
                    if len(result.stdout) == 0:
                        result = su.bash.run(cmd = f"docker build --build-arg username=$(id -un) --build-arg uid=$(id -u) --build-arg groupname=$(id -gn) --build-arg gid=$(id -g) -t {project.lower()}-{datastructure.lower()}{workload}:{system} -f {IMAGES_PATH}/{project.lower()}-{datastructure.lower()}-{workload}-{system}.dockerfile .", check_returncode=0)
                        if result.returncode != 0:
                            print("Exiting ...")
                        #fi
                    #fi
                    image_id : str = result.stdout
                    print(image_id)
                    sum : float = 0
                    for i in range(0, runs):
                        result = su.bash.run(cmd = f"docker run --rm {image_id}")
                        print(result.stdout)
                        execution_time = re.search('Execution Time : (.*)', result.stdout)
                        if execution_time:
                            sum = sum + float(execution_time.group(1))
                            exec_result[project][system][query][datastructure][workload][i] = execution_time.group(1)
                        #fi
                    #rof
                    exec_result[project][system][query][datastructure][workload]["average [ms]"] = sum / (1000000 * runs)
                #rof
            #rof
        #rof
    #rof
    print(json.dumps(exec_result, indent = 4))
    return
#fed

def process_results(results : List[subprocess.CompletedProcess],
                    search_string : str
                    )->List[float]:
    run_times : List[float] = []
    for idx, result in enumerate(results):
        time = re.search(f'{search_string} : (.*)', result.stdout)
        if time:
            run_times.append(float(time.group(1)))
        else:
            run_times.append(0)
        #fi
    #rof
    return run_times
#fed
    
def exec_benchmark(benchmark : str,
                   systems : List[str],
                   queries : List[str],
                   runs : int):
    data_set : DataSet = DATASETS[benchmark]
    if queries is None:
        queries = data_set.queries.keys()
    #fi
    if systems is None:
        systems = SYSTEMS.keys()
    #fi
    class_suffix : str = (benchmark.split("-")[-1]).replace('.','')
    exec_result : Dict = {}
    exec_result[benchmark] = {}
    for system in systems:
        exec_result[benchmark][system] = {}
        for query in queries:
            exec_result[benchmark][system][query] = {}
            file_contents : List[str] = []
            file_contents.append("#!/bin/bash")
            file_contents.append("SECONDS=0")
            if "neo4j-server" in system:
                file_contents.append("neo4j console &")
                file_contents.append("sleep 50")
            elif "memgraph" in system:
                file_contents.append("/memgraph/memgraph-2.14.0+20~c15b62a88_Release &")
                file_contents.append("sleep 5")
            #fi
            file_contents.append(f"mvn test -Dtest={SYSTEMS[system]}{class_suffix}#{data_set.queries[query]}")
            file_contents.append("echo 'Total Time : '$((SECONDS * 1000000000))")
            file_content : str = "\n".join(file_contents)
            with open(f"{IMAGES_PATH}/execute.sh", "w") as exec_file:
                exec_file.write(file_content)
            #htiw
            result = su.bash.run(cmd = f"docker images {benchmark.lower()}:{system} --format '{{{{.ID}}}}'", check_returncode = 0)
            if len(result.stdout) == 0:
                print(f"docker build --build-arg username=$(id -un) --build-arg uid=$(id -u) --build-arg groupname=$(id -gn) --build-arg gid=$(id -g) -t {benchmark.lower()}:{system} -f {IMAGES_PATH}/{benchmark.lower()}-{system}.dockerfile .")
                result = su.bash.run(cmd = f"docker build --progress=plain --build-arg username=$(id -un) --build-arg uid=$(id -u) --build-arg groupname=$(id -gn) --build-arg gid=$(id -g) -t {benchmark.lower()}:{system} -f {IMAGES_PATH}/{benchmark.lower()}-{system}.dockerfile .", check_returncode=0)
                if result.returncode != 0:
                    print("Exiting ...")
                #fi
            #fi
            image_id : str = result.stdout
            print(image_id)
            stage_time_strings : List[str] = ['Parse Tree Generation Time',
                                              'Parse Tree Generation Time - 1',
                                              'Parse Tree Generation Time - 2',
                                              'Query Plan AST Building Time',
                                              'Query Execution Time',
                                              'Total Query Time',
                                              'Total Time']
            results : List[subprocess.CompletedProcess] = []
            for i in range(0, runs):
                result = su.bash.run(cmd = f"docker run --rm {image_id}")
                results.append(result)
            #rof
                         
            for stage_time in stage_time_strings:
                exec_result[benchmark][system][query][stage_time] = {}
                run_times : List[float] = process_results(results, stage_time)
                for idx, run_time in enumerate(run_times):
                    exec_result[benchmark][system][query][stage_time][idx] = run_time
                #rof
                exec_result[benchmark][system][query][stage_time]["average"] = mean(run_times) 
            #rof

            for i in range(0, runs):
                exec_result[benchmark][system][query]['Parse Tree Generation Time'][i] = exec_result[benchmark][system][query]['Parse Tree Generation Time - 1'][i] + exec_result[benchmark][system][query]['Parse Tree Generation Time - 2'][i]
            #rof
            
            exec_result[benchmark][system][query]['Parse Tree Generation Time']['average'] = exec_result[benchmark][system][query]['Parse Tree Generation Time - 1']['average'] + exec_result[benchmark][system][query]['Parse Tree Generation Time - 2']['average']
        #rof
    #rof
    print(json.dumps(exec_result, indent = 4))
#fed

# currently only supports h2, but can be extended to support other use cases
def exec_usecase(usecase : str,
                 impls : List[str],
                 queries : List[str],
                 runs : int):
    if usecase != "h2":
        print("Use case not supported")
        return
    #fi
    if impls is None:
        impls = ["ingraph-reflection", "jdbc"]
    #fi
    if queries is None:
        queries = ["dbname", "tables", "users"]
    #fi
    
    impl_test_map : Dict[str, str] = {"ingraph-reflection" : "InGraphReflectionMetaDataTest",
                                     "jdbc" : "JDBCMetaDataTest"}
    
    query_method_map : Dict[str, str] = {"dbname" : "testH2DbNameQuery",
                                         "tables" : "testH2TablesQuery",
                                         "users" : "testH2UsersQuery"}
    
    exec_result : Dict = {}
    for impl in impls:
        exec_result[impl] = {}
        
        for query in queries:
            # exec_result[impl][query] = {}
            file_contents : List[str] = []
            file_contents.append("#!/bin/bash")
            file_contents.append(f"mvn -q test -Dtest={impl_test_map[impl]}#{query_method_map[query]}")
            file_content : str = "\n".join(file_contents)
            with open(f"{IMAGES_PATH}/execute.sh", "w") as exec_file:
                exec_file.write(file_content)

            result = su.bash.run(cmd = f"docker images h2metadata-{impl} --format '{{{{.ID}}}}'", check_returncode = 0)
            if len(result.stdout) == 0:
                result = su.bash.run(cmd = f"docker build --build-arg username=$(id -un) --build-arg uid=$(id -u) --build-arg groupname=$(id -gn) --build-arg gid=$(id -g) -t h2metadata-{impl} -f {IMAGES_PATH}/h2metadata-{impl}.dockerfile .", check_returncode=0)
                if result.returncode != 0:
                    print("Exiting ...")
                #fi
                print("image build done")
                return
            #fi
            
            image_id : str = result.stdout
            print(image_id)

            sum : float = 0
            for i in range(0, runs):
                result = su.bash.run(cmd = f"docker run --rm {image_id}")
                print(result.stdout)
                execution_time = re.search('Execution Time : (.*)', result.stdout)
                if execution_time:
                    sum = sum + float(execution_time.group(1))
                    # exec_result[impl][query][i] = execution_time.group(1)
                #fi
            #rof
            exec_result[impl][query] = sum / (1000000 * runs)
        #rof
    #rof
    print(json.dumps(exec_result, indent = 4))
#fed
    
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Script for evaluating database systems")
    parser.add_argument("--benchmark",
                        help="Benchmark to eval against",
                        required = False,
                        choices=DATASETS.keys(),
                        nargs=1,)
    parser.add_argument("--project",
                        help="Projects to eval",
                        required = False,
                        choices=PROJECTS.keys(),
                        nargs=1,)
    parser.add_argument("--datastructures",
                        help="DataStructure to eval",
                        required = False,
                        choices=[y for x in PROJECTS.values() for y in x.data_structures],
                        nargs='*',)
    parser.add_argument("--workloads",
                        help="Workloads to eval",
                        required = False,
                        choices=PROJECT.workloads,
                        nargs='*',)
    parser.add_argument("--queries",
                        help="Query to execute",
                        required = False,
                        choices=[y for x in [LDBC_SNB.queries.keys(),LDBC_FINBENCH.queries.keys()] for y in x],
                        nargs='*',)
    parser.add_argument("--runs",
                        help="Number of runs per query",
                        default = 1,
                        type=int,
                        required = False,
                        choices=range(1,100),
                        nargs=1,) 
    parser.add_argument("--dbms",
                        help="DBMS to use",
                        required = False,
                        choices=SYSTEMS.keys(),
                        nargs='*',)
    parser.add_argument("--h2queries",
                        help="H2 query to execute",
                        required = False,
                        choices=["dbname", "tables", "users"],
                        nargs=1,)
    parser.add_argument("--h2impls",
                        help="H2 query implementation to use",
                        required = False,
                        choices=["ingraph-reflection", "jdbc"],
                        nargs=1,)
    parser.add_argument("--usecase",
                        help="Use case to evaluate",
                        required = False,
                        choices=["h2"],
                        nargs=1,)
    args = parser.parse_args()
    if args.benchmark is not None:
        exec_benchmark(args.benchmark[0], args.dbms, args.queries, args.runs[0])
    elif args.project is not None:
        exec_datastructure(args.project[0], args.datastructures, args.workloads, args.dbms, args.queries, args.runs[0])
    elif args.usecase is not None:
        exec_usecase(args.usecase[0], args.h2impls, args.h2queries, args.runs[0])
    #fi
#fi
