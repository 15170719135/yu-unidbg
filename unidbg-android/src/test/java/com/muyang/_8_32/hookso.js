// function inline_hook() {
//     var libnative_lib_addr = Module.findBaseAddress("libyoda.so");
//     console.log(libnative_lib_addr);
//     if (libnative_lib_addr) {
//         console.log("libnative_lib_addr:", libnative_lib_addr);
//         var addr_101F4 = libnative_lib_addr.add(0x8ff1);
//         console.log("addr_10x8ff1:", addr_101F4);

//         Java.perform(function () {
//             Interceptor.attach(addr_101F4, {
//                 onEnter: function (args) {
//                     console.log("addr_101F4 OnEnter :", this.context.PC, 
//                                 this.context.x1, this.context.x5, 
//                                 this.context.x10);
//                 },
//                 onLeave: function (retval) {
//                      console.log("retval is :", retval) 
//                 }
//             }
//             )
//         })
//     }
// }

// // setImmediate(inline_hook)
// setInterval(inline_hook,1000);
function hex2str(hex) {
    var trimedStr = hex.trim();
    var rawStr = trimedStr.substr(0, 2).toLowerCase() === "0x" ? trimedStr.substr(2) : trimedStr;
    var len = rawStr.length;
    if (len % 2 !== 0) {
        return "";
    }
    var curCharCode;
    var resultStr = [];
    for (var i = 0; i < len; i = i + 2) {
        curCharCode = parseInt(rawStr.substr(i, 2), 16);
        resultStr.push(String.fromCharCode(curCharCode));
    }
    return resultStr.join("");
}

function main() {
    Java.perform(function () {
        var NativeUtils = Java.use("com.inno.yodasdk.utils.NativeUtils");
        // console.log(NativeUtils);
        NativeUtils.bulwark.implementation = function (str, str2, str3) {
            console.log('bulwark is called' + ', ' + 'str: ' + str + ', ' + 'str2: ' + str2 + ', ' + 'str3: ' + str3);
            var ret = this.bulwark(str, str2, str3);
            var ByteString = Java.use("com.android.okhttp.okio.ByteString");
            console.log(ByteString.of(ret).hex());
            console.log('bulwark ret value is ' + ret);
            return ret;
        };
    })
}

setInterval(main, 1000);
