import requests_pkcs12,json

def main():
    url = 'https://180.76.60.244:18443/api/app5'
    total  =0 
    for page in range(1, 101):
        data = {
            'page': page
        }
        response = requests_pkcs12.post(url, data=data, verify=False, pkcs12_filename="1.p12",pkcs12_password="MZ4cozY8Qu32UzGe")
        print(response.text)
        jsondatas = json.loads(response.text)['data']
        for jsondata in jsondatas:
            total += int(jsondata['value'])
    print(total)
if __name__ == '__main__':
    main()