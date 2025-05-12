local key = KEYS[1]
local noteId = ARGV[1]

-- 检查布隆过滤器是否存在
local exists = redis.call('EXISTS', key)
if exists == 0 then
    return -1
end

-- 检查笔记是否收藏
local collected = redis.call('BF.EXISTS', noteId)
if collected == 1 then
    return 1
end

-- 更新布隆过滤器
redis.call('BF.ADD', noteId)
return 0