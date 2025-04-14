local key = KEYS[1]
local noteId=  ARGV[1]

local exists = redis.call('EXISTS', key)
if exists == 0 then
    return -1
end

-- 布隆过滤器校验：已点赞（1），未点赞（0）
return redis.call('BF.EXISTS', key, noteId)