# ExtractWordView #
Extract Word Demo For Android

![](https://raw.githubusercontent.com/jcodeing/ExtractWordView/master/lookme.gif)

Introduction
============
	这是一个，可以提取 英文单词 的 ListView Demo
	长按文章时，启动题词功能，含有 ”放大镜“ 功能，打开了手指触摸下的视角。
	长按，后可随意滑动，滑到那里，取到那里。松开后，便可获取，以选择的单词。
Features
========
	在android原生控件ListView上实现滑动取词。
	可以随意滑动取词，跨item进行取词。取词代码在ListView的onTouchEvent中处理。
Usage
=====
	只需将widget下的 
		EWListView.java
		EWListViewChildET.java
	两个文件copy到你的工程中，
	ListView用EWListView，或者，直接复制里面的逻辑到你自定义的ListView中即可
	Item用EWListViewChildET，或者copy，逻辑到你的item中。
License
=======
	The MIT License (MIT)

	Copyright (c) 2015 Jcodeing
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.