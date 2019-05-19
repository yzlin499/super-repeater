var Objects = Java.type("java.util.Objects");
var praiseBang = {
    count: 1,
    threshold: 4,
    lastRecord: "",
    priority: "aa",
    check: function (msg) {
        if (praiseBang.lastRecord === msg.message) {
            praiseBang.count++;
            if (praiseBang.count === praiseBang.threshold) {
                praiseBang.count++;
                return true;
            }
        } else {
            praiseBang.lastRecord = msg.message;
            praiseBang.count = 1;
        }
        return false;
    },
    operate: function () {
        return praiseBang.lastRecord;
    }
};