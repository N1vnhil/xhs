local key = KEYS[1]
local fanUserId = ARGV[1]
local timestamp = ARGV[2]

local exists = redis.call('EXISTS', key)
if exists == 0 then
    return -1
end

local size = redis.call('ZCARD', key)
if size >= 5000 then
    redis.call('ZPOPMIN', key)
end

redis.call('ZADD', key, timestamp, fanUserId)
return 0
