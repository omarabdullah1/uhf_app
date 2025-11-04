#!/bin/bash

echo "🧪 Testing RFID System APIs..."
echo "==============================="

SERVER_URL="http://localhost:5000"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to test API endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -n "Testing $description... "
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "%{http_code}" -o /tmp/api_response "$SERVER_URL$endpoint")
    else
        response=$(curl -s -w "%{http_code}" -o /tmp/api_response -X "$method" \
                  -H "Content-Type: application/json" \
                  -d "$data" "$SERVER_URL$endpoint")
    fi
    
    http_code="${response: -3}"
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ PASS${NC}"
        if [ -s /tmp/api_response ]; then
            echo "   Response: $(head -c 100 /tmp/api_response)..."
        fi
    else
        echo -e "${RED}❌ FAIL (HTTP $http_code)${NC}"
        if [ -s /tmp/api_response ]; then
            echo "   Error: $(cat /tmp/api_response)"
        fi
    fi
    echo
}

# Wait for server to be ready
echo "⏳ Waiting for server to be ready..."
sleep 3

# Test all endpoints
test_endpoint "GET" "/api/health" "" "Health Check"
test_endpoint "GET" "/api/status" "" "Reader Status"
test_endpoint "GET" "/api/tags" "" "Get Tags"
test_endpoint "POST" "/api/start" "" "Start Reading"
test_endpoint "GET" "/api/network" "" "Network Check"
test_endpoint "POST" "/api/read" '{"bank":"EPC","address":0,"length":6}' "Read Tag"
test_endpoint "POST" "/api/write" '{"data":"test123","bank":"USER","address":0}' "Write Tag"
test_endpoint "POST" "/api/tags/add" '{"tagId":"E200001660160123456789012","rssi":-45,"antenna":1}' "Add Test Tag"
test_endpoint "POST" "/api/communication" '{"mode":"network"}' "Change Communication"
test_endpoint "POST" "/api/stop" "" "Stop Reading"
test_endpoint "GET" "/api/info" "" "API Info"

echo "==============================="
echo -e "${YELLOW}🌐 Open your browser to:${NC}"
echo "   React GUI: http://localhost:3000"
echo "   API Info:  http://localhost:5000/api/info"
echo ""
echo -e "${YELLOW}💡 Test the GUI by:${NC}"
echo "   1. Click 'Start Reading' button"
echo "   2. Click 'Add Test Tag' to simulate tags"
echo "   3. Try writing data to tags"
echo "   4. Check the Activity Logs"
echo "   5. Monitor real-time tag updates"

# Cleanup
rm -f /tmp/api_response