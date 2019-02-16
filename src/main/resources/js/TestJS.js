var praiseBang = {
    check: function (msg) {
        return msg.message.length === 10;
    },
    operate: function () {
        praiseBang.robot.sendPersonMsg("499680328", "asdsadasdas");
    }
};