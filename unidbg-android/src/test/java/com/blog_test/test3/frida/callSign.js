function callSign(){
    Java.perform(function () {
        var NetCrypto = Java.use("com.izuiyou.network.NetCrypto");
        var JavaString = Java.use("java.lang.String");

        var plainText = "r0ysue";
        var plainTextBytes = JavaString.$new(plainText).getBytes("UTF-8");

        var result = NetCrypto.a("12345", plainTextBytes);
        console.log(result);
    });
}

// frida -UF -l D:\Project\.2022\yu-unidbg2\unidbg-android\src\test\java\com\blog_test\test3\frida\callSign.js