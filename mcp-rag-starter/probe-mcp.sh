#!/usr/bin/env bash
set -e
URL=${MCP_URL:-http://localhost:8082/mcp}
H=(-s -H 'Content-Type: application/json' -H 'Accept: application/json, text/event-stream')
if [ -n "$MCP_TOKEN" ]; then H+=(-H "Authorization: Bearer $MCP_TOKEN"); fi
HDR=$(mktemp)

curl "${H[@]}" -D "$HDR" -o /dev/null -X POST "$URL" \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-06-18","capabilities":{},"clientInfo":{"name":"probe-mcp","version":"0.1"}}}' \
  || { echo "cannot reach $URL: is the app running?" >&2; exit 1; }
SID=$(grep -i '^mcp-session-id' "$HDR" | awk '{print $2}' | tr -d '\r')
if [ -z "$SID" ]; then
  echo "no Mcp-Session-Id from $URL (HTTP $(grep -m1 '^HTTP' "$HDR" | awk '{print $2}'))" >&2
  exit 1
fi
S=(-H "Mcp-Session-Id: $SID")

curl "${H[@]}" "${S[@]}" -o /dev/null -X POST "$URL" \
  -d '{"jsonrpc":"2.0","method":"notifications/initialized"}'

if [ $# -eq 0 ]; then
  BODY='{"jsonrpc":"2.0","id":2,"method":"tools/list"}'
  FILTER='if .error then .error else "\(.result.tools | length) tools", (.result.tools[] | "\(.name)  readOnly=\(.annotations.readOnlyHint)  \(.description)") end'
else
  TOOL=$1
  ARGS=${2:-{\}}
  BODY="{\"jsonrpc\":\"2.0\",\"id\":3,\"method\":\"tools/call\",\"params\":{\"name\":\"$TOOL\",\"arguments\":$ARGS}}"
  FILTER='if .error then .error else (.result.content[0].text | fromjson? // .) end'
fi

curl "${H[@]}" "${S[@]}" -X POST "$URL" -d "$BODY" | sed 's/^data://' | grep '^{' | tail -1 | jq -r "$FILTER"
