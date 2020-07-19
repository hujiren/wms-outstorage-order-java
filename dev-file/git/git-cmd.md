
初始
  git init

  git add .

## 连接git库
   git remote add origin https://github.com/arranapl/apl-wms-outstorage-order-java.git


提交描述

  git commit -m 


从git库拉取代码
  git pull   origin master
  
  git pull -f  origin master
  
140.82.114.4    github.com
199.232.69.194  github.global.ssl.fastly.net

合并代码
   自动合并
   解决冲突  vcs/git


推送代码到git库
  git push -u origin master
  git push -f origin master



解决.gitignore文件不生效
git rm -r --cached .
git add .
git commit -m "update .gitignore"



