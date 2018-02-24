# Android-EasyJavaBean-Plugin
Java bean代码自动生成插件：根据复制过来的字段文本自动生成Java Bean文件或者对应字段。
每一行需要满足格式：

字段名（如：name）  字段类型（如：String） 字段注释（如：姓名，注释不是必要的）

有两种生成方式：

 - 在已有文件中追加字段

    使用方式：在文件中"右键-->generate... ”或者使用快捷键“Alt + Insert”，接着选择命令“Generate Field By String”

 - 根据复制的文本生成Java Bean文件

    使用方式：在目标包上右键-->New-->New Java Bean File By String
    
效果图如下：

追加字段：
![image](http://img.blog.csdn.net/20180223155507769?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjcyNTg3OTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

    
新建实体：
![image](http://img.blog.csdn.net/20180223155444767?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjcyNTg3OTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

详见CSDN博客：[AS插件开发：根据特定格式的文本自动生成Java Bean字段或文件](http://blog.csdn.net/qq_27258799/article/details/79295251)