#-*- coding: utf-8 -*-
import sys
sys.path.insert(0,"../usr/local/lib/python3.5/site-packages/")
#reload(sys)
#sys.setdefaultencoding('utf-8')
from bs4 import BeautifulSoup
import urllib.parse
import requests
from time import sleep
import json

URL = 'http://search.daum.net/search?w=news&nil_search=btn&DA=NTB&enc=utf8&cluster=y&cluster_page=1&q='
s = "사드"
a = s.encode("utf-8")
a = urllib.parse.quote(a)






def __str__(self):
    return 'title=%s, content=%s, url=%s'%(self.title, self.content, self.url)

def obj_dict(obj):
    return obj.__dict__

def get_url_with_keyword(URL, keyword):
  source_code_from_URL = URL+keyword
  #print(source_code_from_URL)
  return source_code_from_URL

def crawling(URL):
  count =0
  objarray = []
  soup = BeautifulSoup(requests.get(URL).text, 'lxml')
  title = ''
  content = ''
  contentlist = soup.find_all('p', class_='f_eb')
  url = ''
  for i in soup.find_all("a", class_="f_link_bu"):
    obj = {'title':'', 'content':'', 'url':''}
    title = str(i.find_all(text=True))
    content = str(contentlist[count].find_all(text=True))
    url = str(i.get('href'))
    obj['title'] = title
    obj['content'] = content
    obj['url'] = url
    sleep(2)
    count = count+1
    objarray.append(obj)
  json.dumps(objarray, default=obj_dict)
  print(objarray)


def main(input_kwd):
    keyword = input_kwd
    keyword = urllib.parse.quote(keyword)

    result_url = get_url_with_keyword(URL, keyword)
    crawling(result_url)






if __name__ == '__main__':
  main(s)
