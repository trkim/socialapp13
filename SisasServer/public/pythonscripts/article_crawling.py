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
  print(source_code_from_URL)
  return source_code_from_URL

def get_title(URL):
  print('get_title')
  soup = BeautifulSoup(requests.get(URL).text, 'lxml')
  text = ''
  for div in soup.find_all("a", class_="f_l"):
    text = text + str(div.find_all(text=True))
    sleep(2)
  return text


def get_text(URL):
  print('get_text')
  soup = BeautifulSoup(requests.get(URL).text, 'lxml')
  text = ''
  for item in soup.find_all('p', class_='f_eb'):
    text = text + str(item.find_all(text=True))
    sleep(2)
  return text

def get_news_url(URL):
  print('get_news_url')
  soup = BeautifulSoup(requests.get(URL).text, 'lxml')
  text = ''
  for link in soup.find_all('a', class_='f_l'):
    text = text + str(link.get('href'))
    sleep(2)
  return text


def main(input_kwd):
    keyword = input_kwd.encode("utf-8")
    keyword = urllib.parse.quote(keyword)

    result_url = get_url_with_keyword(URL, keyword)
    result_title = get_title(result_url)
    result_text = get_text(result_url)
    result_news_url = get_news_url(result_url)

    print('python코드에서 출력')
    print(result_title)
    print(result_text)
    print(result_news_url)

    return {
        'result_title' : result_title,
        'result_text' : result_text,
        'result_news_url' : result_news_url
    }




if __name__ == '__main__':
  main(a)
