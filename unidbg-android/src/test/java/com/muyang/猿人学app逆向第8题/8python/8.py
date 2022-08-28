import re
import jpype
import requests
import time,json

def main():
    jpype.startJVM(jpype.getDefaultJVMPath(), "-ea", "-Djava.class.path=/root/Desktop/unidbg/out/artifacts/unidbg_android_jar/unidbg-android.jar")
    ChallengeTwoFragment = jpype.JPackage("com.muyang.match").app8_jar()
    url = 'https://appmatch.yuanrenxue.com/app8'
    sum = 0
    for page in range(1, 101):
        data = {
            "page": page,
        }
        data_ = {}
        data_["s"] = str(ChallengeTwoFragment.callgetData(page))
        response = requests.post(url, data=data_).json()
        # print(response)
        jsondatas = response['data']
        for i in jsondatas:
            sum+=int(i['value'].replace("\r",""))
    print(sum)
    jpype.shutdownJVM()

if __name__ == '__main__':
    main()