local key = KEYS[1]
local userId = ARGV[1]

local exists = redis.call('EXISTS', key)
if exists == 0 then
    redis.call('BF.ADD', key, '')
    redis.call('EXPIRE', key, 20*60*60)
end

return redis.call('BF.EXISTS', key, userId)