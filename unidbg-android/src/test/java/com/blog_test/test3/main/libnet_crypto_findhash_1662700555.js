
function monitor_constants(targetSo) {
    let const_array = [];
    let const_name = [];
    let const_addr = [['SHA1 K table', '0xc97c0'], ['SHA256 / SHA224 K tabke', '0xca740'], ['SHA512 / SHA384 K table', '0xcc4a0']];

    for (var i = 0; i < const_addr.length; i++) {
        const_array.push({base:targetSo.add(const_addr[i][1]),size:0x1});
        const_name.push(const_addr[i][0]);
    }

    MemoryAccessMonitor.enable(const_array, {
        onAccess: function (details) {
            console.log("\n");
            console.log("监控到疑似加密常量的内存访问\n");
            console.log(const_name[details.rangeIndex]);
            console.log("访问来自:"+details.from.sub(targetSo)+"(可能有误差)");
    }
});
}

function hook_suspected_function(targetSo) {
    const funcs = [['sub_65540', '函数sub_65540疑似哈希函数，包含初始化魔数的代码。', '0x65541'], ['sub_9C488', '函数sub_9C488疑似哈希函数运算部分。', '0x9c489'], ['MD5_Init', '函数MD5_Init疑似哈希函数，包含初始化魔数的代码。', '0xad140'], ['md5_block_data_order', '函数md5_block_data_order疑似哈希函数运算部分。', '0xad18c'], ['SHA1_Init', '函数SHA1_Init疑似哈希函数，包含初始化魔数的代码。', '0xc82ec'], ['SHA224_Init', '函数SHA224_Init疑似哈希函数，包含初始化魔数的代码。', '0xc8344'], ['SHA256_Init', '函数SHA256_Init疑似哈希函数，包含初始化魔数的代码。', '0xc83c8'], ['SHA384_Init', '函数SHA384_Init疑似哈希函数，包含初始化魔数的代码。', '0xc8a54'], ['SHA512_Init', '函数SHA512_Init疑似哈希函数，包含初始化魔数的代码。', '0xc8b28'], ['sha1_block_data_order', '函数sha1_block_data_order疑似哈希函数运算部分。', '0xc92a0'], ['sha256_block_data_order', '函数sha256_block_data_order疑似哈希函数运算部分。', '0xca860'], ['sha256_block_data_order_neon', '函数sha256_block_data_order_neon疑似哈希函数主体，包含初始化常数以及运算部分。', '0xcb660'], ['sha512_block_data_order', '函数sha512_block_data_order疑似哈希函数运算部分。', '0xcc740'], ['sub_CF680', '函数sub_CF680疑似哈希函数主体，包含初始化常数以及运算部分。', '0xcf680'], ['sub_CFE50', '函数sub_CFE50疑似哈希函数主体，包含初始化常数以及运算部分。', '0xcfe50'], ['sub_D04A0', '函数sub_D04A0疑似哈希函数，包含初始化魔数的代码。', '0xd04a0'], ['bsaes_ctr32_encrypt_blocks', '函数bsaes_ctr32_encrypt_blocks疑似哈希函数，包含初始化魔数的代码。', '0xd0860'], ['bsaes_xts_encrypt', '函数bsaes_xts_encrypt疑似哈希函数运算部分。', '0xd0aa0'], ['bsaes_xts_decrypt', '函数bsaes_xts_decrypt疑似哈希函数运算部分。', '0xd1070'], ['aes_v8_set_encrypt_key', '函数aes_v8_set_encrypt_key疑似哈希函数，包含初始化魔数的代码。', '0xd16c0'], ['bn_mul_comba8', '函数bn_mul_comba8疑似哈希函数运算部分。', '0xdddd4'], ['bn_sqr_comba8', '函数bn_sqr_comba8疑似哈希函数运算部分。', '0xde674'], ['sub_13F548', '函数sub_13F548疑似哈希函数，包含初始化魔数的代码。', '0x13f548'], ['DES_encrypt1', '函数DES_encrypt1疑似哈希函数运算部分。', '0x152fd0'], ['DES_encrypt2', '函数DES_encrypt2疑似哈希函数运算部分。', '0x154268'], ['bn_mul_mont', '函数bn_mul_mont疑似哈希函数运算部分。', '0x157980'], ['sub_182944', '函数sub_182944疑似哈希函数，包含初始化魔数的代码。', '0x182945']];
    for (var i in funcs) {
        let relativePtr = funcs[i][2];
        let funcPtr = targetSo.add(relativePtr);
        let describe = funcs[i][1];
        let handler = (function() {
        return function(args) {
            console.log("\n");
            console.log(describe);
            console.log(Thread.backtrace(this.context,Backtracer.ACCURATE).map(DebugSymbol.fromAddress).join("\n"));
        };
        })();
    Interceptor.attach(funcPtr, {onEnter: handler});
}
}


function main() {
    var targetSo = Module.findBaseAddress('libnet_crypto.so');
    // 对疑似哈希算法常量的地址进行监控，使用frida MemoryAccessMonitor API，有几个缺陷，在这里比较鸡肋。
    // 1.只监控第一次访问，所以如果此区域被多次访问，后续访问无法获取。可以根据这篇文章做改良和扩展。https://bbs.pediy.com/thread-262104-1.htm
    // 2.ARM 64无法使用
    // 3.无法查看调用栈
    // 在这儿用于验证这些常量是否被访问，访问了就说明可能使用该哈希算法。
    // MemoryAccessMonitor在别处可能有较大用处，比如ollvm过的so，或者ida xref失效/过多等情况。
    // hook和monitor这两个函数，只能分别注入和测试，两个同时会出错，这可能涉及到frida inline hook的原理
    // 除非hook_suspected_function 没结果，否则不建议使用monitor_constants。
    // monitor_constants(targetSo);

    hook_suspected_function(targetSo);
}

setImmediate(main);
    