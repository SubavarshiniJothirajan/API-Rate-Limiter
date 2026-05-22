local current = redis.call('GET', KEYS[1])

-- Request limit exceeded
if current and tonumber(current) >= tonumber(ARGV[1]) then
    local ttl = redis.call('TTL', KEYS[1])
    return {0, current, ttl}
end

-- Increment request count
current = redis.call('INCR', KEYS[1])

-- First request: set expiry window
if tonumber(current) == 1 then
    redis.call('EXPIRE', KEYS[1], ARGV[2])
end

local ttl = redis.call('TTL', KEYS[1])

-- Allowed
return {1, current, ttl}