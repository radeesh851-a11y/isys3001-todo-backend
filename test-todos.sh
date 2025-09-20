# ====== CONFIG ======
BASE_URL="http://localhost:8080"
EMAIL="alice@example.com"
PASSWORD="Password123!"

# ====== 1) Register ======
echo "== Register =="
curl -i -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\",\"fullName\":\"Alice\"}"
echo -e "\n"

# If you already registered once, the above may return 409/400 – that's fine.

# ====== 2) Login and capture JWT ======
echo "== Login & capture JWT =="
TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}" | jq -r '.token')

echo "TOKEN: $TOKEN"
AUTH="Authorization: Bearer $TOKEN"
echo

# Sanity check
if [ "$TOKEN" = "null" ] || [ -z "$TOKEN" ]; then
  echo "❌ Failed to obtain JWT. Check login response and your auth endpoints."
  exit 1
fi

# ====== 3) Create a todo ======
echo "== Create Todo =="
CREATE_RES=$(curl -s -X POST "$BASE_URL/api/todos" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"title":"Write assignment report","description":"CI/CD section first"}')
echo "$CREATE_RES" | jq
TODO_ID=$(echo "$CREATE_RES" | jq -r '.id')
echo "Created TODO_ID=$TODO_ID"
echo

# ====== 4) List my todos ======
echo "== List Todos =="
curl -s "$BASE_URL/api/todos" -H "$AUTH" | jq
echo

# ====== 5) Get by id ======
echo "== Get Todo by ID =="
curl -s "$BASE_URL/api/todos/$TODO_ID" -H "$AUTH" | jq
echo

# ====== 6) Update (mark completed) ======
echo "== Update Todo (completed=true) =="
curl -s -X PUT "$BASE_URL/api/todos/$TODO_ID" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"completed":true}' | jq
echo

# ====== 7) Update (title/description) ======
echo "== Update Todo title/description =="
curl -s -X PUT "$BASE_URL/api/todos/$TODO_ID" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"title":"Write assignment report (final)","description":"Add screenshots"}' | jq
echo

# ====== 8) Delete ======
echo "== Delete Todo =="
curl -i -s -X DELETE "$BASE_URL/api/todos/$TODO_ID" -H "$AUTH"
echo -e "\n"

# ====== 9) Confirm deletion ======
echo "== Confirm deletion (should be 404) =="
curl -i -s "$BASE_URL/api/todos/$TODO_ID" -H "$AUTH"
echo
