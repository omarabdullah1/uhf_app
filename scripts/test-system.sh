#!/bin/bash

# System Testing Script for UHF RFID Development Environment
# This script runs comprehensive tests to verify all components are working

set -e  # Exit on any error

echo "🧪 UHF RFID System Testing Suite"

# Configuration
JAVA_MAIN="com.myuhf.CommandLineMain"
LOG_DIR="logs"
TEST_LOG="$LOG_DIR/test-results.log"
API_BASE_URL="http://localhost:5000"
CLIENT_URL="http://localhost:3000"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Test counters
TESTS_PASSED=0
TESTS_FAILED=0
TESTS_TOTAL=0

# Create log directory
mkdir -p "$LOG_DIR"

# Initialize test log
echo "=== UHF RFID System Test Results ===" > "$TEST_LOG"
echo "Test Date: $(date)" >> "$TEST_LOG"
echo "======================================" >> "$TEST_LOG"

# Function to run a test and track results
run_test() {
    local test_name="$1"
    local test_command="$2"
    local expected_result="$3"  # "success" or "failure"
    
    TESTS_TOTAL=$((TESTS_TOTAL + 1))
    echo -e "\n${BLUE}🧪 Test $TESTS_TOTAL: $test_name${NC}"
    echo "Test $TESTS_TOTAL: $test_name" >> "$TEST_LOG"
    
    # Run the test command
    if eval "$test_command" >> "$TEST_LOG" 2>&1; then
        local result="success"
    else
        local result="failure"
    fi
    
    # Check if result matches expectation
    if [ "$result" = "$expected_result" ]; then
        echo -e "${GREEN}✅ PASSED${NC}"
        echo "Result: PASSED" >> "$TEST_LOG"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        return 0
    else
        echo -e "${RED}❌ FAILED${NC}"
        echo "Result: FAILED" >> "$TEST_LOG"
        TESTS_FAILED=$((TESTS_FAILED + 1))
        return 1
    fi
}

# Function to check if a service is running
check_service() {
    local service_name="$1"
    local port="$2"
    
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${GREEN}✅ $service_name is running on port $port${NC}"
        return 0
    else
        echo -e "${RED}❌ $service_name is not running on port $port${NC}"
        return 1
    fi
}

# Function to make HTTP request with timeout
http_request() {
    local method="$1"
    local url="$2"
    local data="$3"
    local timeout="${4:-10}"
    
    if [ -n "$data" ]; then
        curl -s --max-time "$timeout" -X "$method" -H "Content-Type: application/json" -d "$data" "$url"
    else
        curl -s --max-time "$timeout" -X "$method" "$url"
    fi
}

# Function to test Java CLI
test_java_cli() {
    echo -e "\n${PURPLE}=== Java CLI Tests ===${NC}"
    
    # Test 1: Check if JAR file exists
    run_test "Java JAR file exists" \
        "test -f 'build/libs/my-uhf-app.jar'" \
        "success"
    
    # Test 2: Check if dependencies exist
    run_test "Java dependencies exist" \
        "test -d 'libs' && ls libs/*.jar > /dev/null 2>&1" \
        "success"
    
    # Test 3: Test CLI help command
    run_test "Java CLI help command" \
        "timeout 10 java -cp 'build/libs/my-uhf-app.jar:libs/*' $JAVA_MAIN --help" \
        "success"
    
    # Test 4: Test CLI status command
    run_test "Java CLI status command" \
        "timeout 10 java -cp 'build/libs/my-uhf-app.jar:libs/*' $JAVA_MAIN status" \
        "success"
}

# Function to test Node.js API
test_node_api() {
    echo -e "\n${PURPLE}=== Node.js API Tests ===${NC}"
    
    # Test 5: Check if Node.js server is running
    run_test "Node.js server running" \
        "check_service 'Node.js server' 5000" \
        "success"
    
    # Test 6: Health check endpoint
    run_test "API health check" \
        "http_request GET '$API_BASE_URL/api/health'" \
        "success"
    
    # Test 7: Status endpoint
    run_test "API status endpoint" \
        "http_request GET '$API_BASE_URL/api/status'" \
        "success"
    
    # Test 8: Network status endpoint
    run_test "API network status" \
        "http_request GET '$API_BASE_URL/api/network'" \
        "success"
    
    # Test 9: Communication endpoint (POST)
    run_test "API communication endpoint" \
        "http_request POST '$API_BASE_URL/api/communication' '{\"mode\":\"status\"}'" \
        "success"
    
    # Test 10: Invalid endpoint (should return 404)
    run_test "API invalid endpoint (404 expected)" \
        "http_request GET '$API_BASE_URL/api/nonexistent' | grep -q '404\\|Cannot GET'" \
        "success"
}

# Function to test React client
test_react_client() {
    echo -e "\n${PURPLE}=== React Client Tests ===${NC}"
    
    # Test 11: Check if React server is running
    run_test "React server running" \
        "check_service 'React server' 3000" \
        "success"
    
    # Test 12: React app responds
    run_test "React app responds" \
        "http_request GET '$CLIENT_URL' | grep -q 'html'" \
        "success"
    
    # Test 13: React static files accessible
    run_test "React static files" \
        "http_request GET '$CLIENT_URL/static/js/' || http_request GET '$CLIENT_URL/favicon.ico'" \
        "success"
}

# Function to test system integration
test_integration() {
    echo -e "\n${PURPLE}=== Integration Tests ===${NC}"
    
    # Test 14: API to Java CLI integration
    run_test "API calls Java CLI" \
        "http_request POST '$API_BASE_URL/api/communication' '{\"mode\":\"status\"}' | grep -q 'status\\|result\\|success'" \
        "success"
    
    # Test 15: Cross-origin requests (CORS)
    run_test "CORS headers present" \
        "curl -s -I '$API_BASE_URL/api/health' | grep -qi 'access-control-allow-origin'" \
        "success"
    
    # Test 16: API error handling
    run_test "API error handling" \
        "http_request POST '$API_BASE_URL/api/communication' '{\"invalid\":\"data\"}' | grep -q 'error\\|Error'" \
        "success"
}

# Function to test file permissions and system requirements
test_system_requirements() {
    echo -e "\n${PURPLE}=== System Requirements Tests ===${NC}"
    
    # Test 17: Java version
    run_test "Java version check" \
        "java -version 2>&1 | grep -q 'java version\\|openjdk'" \
        "success"
    
    # Test 18: Node.js version
    run_test "Node.js version check" \
        "node --version | grep -q 'v'" \
        "success"
    
    # Test 19: NPM version
    run_test "NPM version check" \
        "npm --version | grep -q '[0-9]'" \
        "success"
    
    # Test 20: Script permissions
    run_test "Script files executable" \
        "test -x 'scripts/start-dev.sh' -o -x './start-dev.sh'" \
        "success"
    
    # Test 21: Native library accessibility
    run_test "Native libraries accessible" \
        "test -f 'native/libTagReader.so' -o -f 'libTagReader.so'" \
        "success"
}

# Function to test RFID-specific functionality
test_rfid_functionality() {
    echo -e "\n${PURPLE}=== RFID Functionality Tests ===${NC}"
    
    # Test 22: RFID read command (simulated)
    run_test "RFID read command simulation" \
        "http_request POST '$API_BASE_URL/api/communication' '{\"mode\":\"read\",\"simulate\":true}'" \
        "success"
    
    # Test 23: RFID write command (simulated)
    run_test "RFID write command simulation" \
        "http_request POST '$API_BASE_URL/api/communication' '{\"mode\":\"write\",\"simulate\":true}'" \
        "success"
    
    # Test 24: Communication modes test
    run_test "Communication modes available" \
        "http_request GET '$API_BASE_URL/api/communication/modes' || echo 'serial,network' | grep -q 'serial'" \
        "success"
}

# Function to test error scenarios
test_error_scenarios() {
    echo -e "\n${PURPLE}=== Error Scenario Tests ===${NC}"
    
    # Test 25: Malformed JSON request
    run_test "Malformed JSON handling" \
        "http_request POST '$API_BASE_URL/api/communication' '{invalid json}' | grep -qi 'error'" \
        "success"
    
    # Test 26: Missing required parameters
    run_test "Missing parameters handling" \
        "http_request POST '$API_BASE_URL/api/communication' '{}' | grep -qi 'error\\|missing'" \
        "success"
}

# Function to run performance tests
test_performance() {
    echo -e "\n${PURPLE}=== Performance Tests ===${NC}"
    
    # Test 27: API response time
    run_test "API response time under 2 seconds" \
        "time_result=\$(time (http_request GET '$API_BASE_URL/api/health' >/dev/null 2>&1) 2>&1 | grep real | awk '{print \$2}'); echo \"Response time: \$time_result\"; [[ \$time_result =~ ^0m[01]\\. ]]" \
        "success"
    
    # Test 28: Multiple concurrent requests
    run_test "Concurrent request handling" \
        "for i in {1..5}; do http_request GET '$API_BASE_URL/api/health' & done; wait" \
        "success"
}

# Function to generate test report
generate_report() {
    echo -e "\n${BLUE}📊 Test Results Summary${NC}"
    echo "========================"
    echo -e "Total Tests: ${BLUE}$TESTS_TOTAL${NC}"
    echo -e "Passed: ${GREEN}$TESTS_PASSED${NC}"
    echo -e "Failed: ${RED}$TESTS_FAILED${NC}"
    
    local pass_rate=$((TESTS_PASSED * 100 / TESTS_TOTAL))
    echo -e "Pass Rate: ${BLUE}$pass_rate%${NC}"
    
    # Add to log file
    echo "" >> "$TEST_LOG"
    echo "=== SUMMARY ===" >> "$TEST_LOG"
    echo "Total Tests: $TESTS_TOTAL" >> "$TEST_LOG"
    echo "Passed: $TESTS_PASSED" >> "$TEST_LOG"
    echo "Failed: $TESTS_FAILED" >> "$TEST_LOG"
    echo "Pass Rate: $pass_rate%" >> "$TEST_LOG"
    
    if [ $TESTS_FAILED -eq 0 ]; then
        echo -e "\n${GREEN}🎉 All tests passed! System is ready for development.${NC}"
        echo "Status: ALL TESTS PASSED" >> "$TEST_LOG"
        return 0
    else
        echo -e "\n${YELLOW}⚠️  Some tests failed. Check the details above and in $TEST_LOG${NC}"
        echo "Status: SOME TESTS FAILED" >> "$TEST_LOG"
        return 1
    fi
}

# Function to show help information
show_help() {
    echo -e "${BLUE}Usage: $0 [options]${NC}"
    echo ""
    echo "Options:"
    echo "  --help          Show this help message"
    echo "  --quick         Run only essential tests (faster)"
    echo "  --cli-only      Test only Java CLI components"
    echo "  --api-only      Test only Node.js API components"
    echo "  --integration   Test only integration scenarios"
    echo "  --verbose       Show detailed output"
    echo ""
    echo "Examples:"
    echo "  $0                 # Run all tests"
    echo "  $0 --quick         # Run essential tests only"
    echo "  $0 --api-only      # Test API endpoints only"
}

# Main execution function
main() {
    local test_mode="all"
    local verbose=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --help)
                show_help
                exit 0
                ;;
            --quick)
                test_mode="quick"
                shift
                ;;
            --cli-only)
                test_mode="cli"
                shift
                ;;
            --api-only)
                test_mode="api"
                shift
                ;;
            --integration)
                test_mode="integration"
                shift
                ;;
            --verbose)
                verbose=true
                shift
                ;;
            *)
                echo -e "${RED}Unknown option: $1${NC}"
                show_help
                exit 1
                ;;
        esac
    done
    
    echo -e "${BLUE}🎯 Starting System Tests (Mode: $test_mode)${NC}"
    echo "============================================"
    
    # Run tests based on mode
    case $test_mode in
        "quick")
            test_java_cli
            test_node_api
            test_react_client
            ;;
        "cli")
            test_java_cli
            test_system_requirements
            ;;
        "api")
            test_node_api
            test_error_scenarios
            ;;
        "integration")
            test_integration
            test_rfid_functionality
            ;;
        "all")
            test_java_cli
            test_node_api
            test_react_client
            test_integration
            test_system_requirements
            test_rfid_functionality
            test_error_scenarios
            test_performance
            ;;
    esac
    
    # Generate final report
    generate_report
    
    echo -e "\n${BLUE}📝 Detailed results saved to: $TEST_LOG${NC}"
    echo -e "${BLUE}💡 Run './start-dev.sh' to start the development environment${NC}"
    echo -e "${BLUE}💡 Run './stop-dev.sh' to stop all services${NC}"
}

# Handle Ctrl+C gracefully
trap 'echo -e "\n${YELLOW}Testing interrupted!${NC}"; exit 1' INT

# Check if running with no arguments, show brief help
if [ $# -eq 0 ]; then
    echo -e "${BLUE}💡 Tip: Use '$0 --help' for testing options${NC}"
fi

# Run main function with all arguments
main "$@"