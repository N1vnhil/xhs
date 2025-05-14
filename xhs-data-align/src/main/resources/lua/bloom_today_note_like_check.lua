local key = KEYS[1]
local noteIdAndNoteCreatorId = ARGV[1]

local exists = redis.call('EXISTS', key)
if exists then
    redis.call('BF.ADD', key, '')
    redis.call('EXPIRE', key, 20*60*60)
end

return redis.call('BF.EXISTS', key, noteIdAndNoteCreatorId)