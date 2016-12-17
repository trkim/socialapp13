#-*- coding: utf-8 -*-
import sys
sys.path.insert(0,"../usr/local/lib/python3.5/site-packages/")
from bs4 import BeautifulSoup
import urllib.parse
import requests
from time import sleep

URL = 'http://search.daum.net/search?w=tot&DA=23A&rtmaxcoll=NNS&q='
s = "사드"
a = s.encode("utf-8")
a = urllib.parse.quote(a)

def get_url_with_keyword(URL, keyword):
  source_code_from_URL = URL+keyword
  return source_code_from_URL

def get_title(URL):
  soup = BeautifulSoup(requests.get(URL).text, 'lxml')
  text = ''
  for div in soup.find_all("a", class_="f_l"):
    text = text + str(div.find_all(text=True))
    sleep(2)
  print('get_title : '+text)


def get_text(URL):
  soup = BeautifulSoup(requests.get(URL).text, 'lxml')
  text = ''
  for item in soup.find_all('p', class_='f_eb'):
    text = text + str(item.find_all(text=True))
    sleep(2)
  print('get_text : '+text)

def get_news_url(URL):
  soup = BeautifulSoup(requests.get(URL).text, 'lxml')
  text = ''
  for link in soup.find_all('a', class_='f_l'):
    text = text + str(link.get('href'))
    sleep(2)
  print('get_news_url : '+text)


def main(input_kwd):
    keyword = input_kwd.encode("utf-8")
    keyword = urllib.parse.quote(keyword)

    result_url = get_url_with_keyword(URL, keyword)
    result_title = get_title(result_url)
    result_text = get_text(result_url)
    result_news_url = get_news_url(result_url)

    print(result_title)
    print(result_text)
    print(result_news_url)






if __name__ == '__main__':
  main(a)
