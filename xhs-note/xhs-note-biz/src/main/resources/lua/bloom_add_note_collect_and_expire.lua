local key = KEYS[1]
local noteId = ARGV[1]
local expireSeconds = ARGV[2]

redis.call('BF.ADD', key, noteId)
redis.call('EXPIRE', key, expireSeconds)
return 0