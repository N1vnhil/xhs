local key = KEYS[1]
local noteId = ARGV[1]
local timestamp = ARGV[2]

-- 检查Zset是否存在
local exists = redis.call('EXISTS', key)
if exists == 0 then
    return -1
end

-- 最多点赞100条笔记
local size = redis.call('ZCARD', key)
if size >= 100 then
    redis.call('ZPOPMIN', key)
end

-- 添加新的笔记点赞关系
redis.call('ZADD', key, timestamp, noteId)
return 0