# GenerateModelPlugin

日常工作中，产品需求发布以后，前端开发首先要与后端商订新需求的网络契约协议，此过程会产生大量新的Model类，每次新建Model都是机械化操作，十分枯燥繁琐！

做为一个程序猿，秉着能不重复我就偷懒的原则，果断自己造轮子，最终效果如下：

![image](http://img.blog.csdn.net/20180208142854271?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjcyNTg3OTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

详细介绍可参考CSDN博客 [AS插件开发：根据特定格式的文本自动生成Java Bean字段](http://blog.csdn.net/qq_27258799/article/details/79295251)

近期还准备重新开一个仓库，专门放简化日常工作的AS插件，有些插件可能市场上已经有了，但是还是想自己动手操作，既放心又舒心~这里先预告一下：

 - 本文插件2.0：填写类名和字段文本生成类文件

 - 自动生成equals hashCode

 - 像AS中，.if生成if代码块一样，通过.onclick生成setOnClickListener代码块


感兴趣的可以star一下，或者有其他idea的，可以一起交流一下~