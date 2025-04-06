local key = KEYS[1]
local noteId = ARGV[1]

local exists = redis.call('EXISTS', key)
if exists == 0 then
    return -1
end

local liked = redis.call('BF.EXISTS', key, noteId)
if liked == 1 then
    return 1
end

redis.call('BF.ADD', key, noteId)
return 0
