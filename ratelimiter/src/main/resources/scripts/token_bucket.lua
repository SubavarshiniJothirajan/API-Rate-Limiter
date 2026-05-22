local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

-- Fetch existing bucket state
local tokens = tonumber(redis.call('HGET', key, 'tokens'))
local lastRefill = tonumber(redis.call('HGET', key, 'lastRefill'))

-- First request
if tokens == nil then
    tokens = capacity
    lastRefill = now
end

-- Calculate refill
local elapsed = now - lastRefill

local newTokens =
    math.floor(elapsed * refillRate)

tokens =
    math.min(capacity, tokens + newTokens)

-- Update refill timestamp
if newTokens > 0 then
    lastRefill = now
end

-- Consume token if available
local allowed = 0

if tokens > 0 then
    tokens = tokens - 1
    allowed = 1
end

-- Save state
redis.call('HSET', key,
    'tokens', tokens,
    'lastRefill', lastRefill
)

redis.call('EXPIRE', key, 3600)

return {
    allowed,
    tokens,
    lastRefill
}