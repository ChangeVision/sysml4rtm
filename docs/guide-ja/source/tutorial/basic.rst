.. _ref-tutorial-basic:

========================================================================================
RT(Robot Technology)コンポーネントを設計する
========================================================================================
このチュートリアルでは、SysMLを用いて最も基本的なRTコンポーネントを設計する方法と、OpenRTPを用いてRTコンポーネントのソースコードのひな型を生成する方法について説明します。

なお、OpenRTMや、ソースコードを実行する際に必要なOpenRTM-aistの概要やインストール方法は `OpenRTM-aist公式サイト <http://openrtm.org/openrtm/>`_ を参照して下さい。

RTコンポーネントの設計、実行の手順は以下のとおりです。

* :ref:`ref-tutorial-basic-createproject`
* :ref:`ref-tutorial-basic-bdd`
* :ref:`ref-tutorial-basic-ibd`
* :ref:`ref-tutorial-basic-rts-rtc-profile`


.. describe:: このチュートリアルで作成するシステムについて
    
  RTコンポーネントMyFirstComponentを含むシステムを設計します。

**ブロック図**

  .. figure:: /images/tutorial/basic/bdd.png
     :alt: image

**内部ブロック図**

  .. figure:: /images/tutorial/basic/ibd.png
     :alt: image

  * :download:`モデルのダウンロード </sources/tutorial/basic.asml>`


astah* SysMLでRTコンポーネントを設計する
======================================================
まずは、astah* SysMLを起動し、設計するロボットシステムのモデルを管理するプロジェクトを作成します。

.. _ref-tutorial-basic-createproject:

プロジェクトの新規作成
----------------------------
OpenRTM-aistが提供するデータ型などを定義したテンプレートファイルを元に、新規ロボットシステムの設計を行うプロジェクトを作成します。

* astah* SysMLを起動し、メニュー :menuselection:`ファイル  --> テンプレートからプロジェクトの新規作成` を選択します。

  .. figure:: /images/tutorial/basic/templatemenu.png
       :alt: image
       
* サブメニューから"テンプレートからプロジェクトの新規作成"を選択し、この |plugin_name| のダウンロードファイルにも含まれる :download:`rtc_template.asml </sources/rtc_template.asml>` を選択します。

--------------

.. _ref-tutorial-basic-bdd:

ブロック図でシステムとRTコンポーネントの構造と関係を設計する
--------------------------------------------------------------------------------
| RTコンポーネントはブロック定義図と内部ブロック図で設計します。
| まず、ブロック定義図でシステムにどのような要素で構成されるかを設計し、内部ブロック図を用いて、システムを構成する要素間の関係性を設計します。

それでは、ブロック定義図を作成するため、メニュー :menuselection:`図  --> ブロック定義図` を選択します。

ブロック定義図では、設計するシステム自体を表すブロックと、そのシステムを構成するRTコンポーネントをrtcステレオタイプが付与されたブロックとして定義します。

最初に設計するシステム自体を表すブロックを作成するため、「ツールバー」-「ブロック」を選択してブロック定義図の好きな場所をクリックします。すると、図のように"ブロック0"という名前の長方形が表示されます。

  .. figure:: /images/tutorial/basic/block.png
       :alt: image

この長方形は、システムを構成する要素を意味するブロックというモデルです。

この「ブロック0」の名前を、これから作成したいロボットシステムの名前として、TutorialSystemに変更してみましょう。

  1. ブロック定義図上で作成した「ブロック0」を選択します。

  2. ブロック0の名前を変更します
  
     画面左下の「プロパティビュー」に、選択されたブロックのプロパティが表示されます。
     
     | これから設計するTutorialSystemをプロパティビューの名前に入力してください。
     | このように、図や構造ツリーで選択したモデルのプロパティは、プロパティビューから表示、編集できます。

    .. figure:: /images/tutorial/basic/block_property.png
       :alt: image

次に、TutorialSystemを構成するRTコンポーネントとして、MyFirstComponentブロックを作成しましょう。先程と同様にツールバーよりブロックを作成し、名前を"MyFirstComponent"と変更します。

ブロックにRTCステレオタイプを設計する
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
| システムはモーターやアクチュエーターなど、RTコンポーネントとは異なる要素も存在します。ブロックがRTコンポーネントであることを示すため、ステレオタイプを用います。
| ステレオタイプはプロパティビューから"ステレオタイプ"タブから設計します。
| 追加ボタンを押下し、追加された名前にRTコンポーネントを意味するステレオタイプ、rtcを入力して下さい。

  .. figure:: /images/tutorial/basic/stereotype.png
     :alt: image

ブロック定義図のデフォルトの設定では、ステレオタイプは図上に表示されません。
ブロック定義図を選択しプロパティビューの「初期設定」タブの「ステレオタイプの表示」にチェックをし、「全図要素に反映」ボタンをクリックすると、図上にステレオタイプが表示されるようになります。

これまでの操作で、次のようなモデルが作成されているはずです。

  .. figure:: /images/tutorial/basic/basic_1.png
       :alt: image
       
システムとRTコンポーネントの階層構造関係を設計する
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
システム(TutorialSystem)とRTコンポーネント(MyFirstComponent)を設計できたので、MyFirstComponentがTutorialSystemを構成する要素であることを、ブロック定義図上で設計しましょう。

SysMLでは全体と部分という関係を表すUMLのコンポジションに相当する、部品関連(Part Assosication)が用意されています。astah* SysMLでは部品関連をコンポジションと称して提供しています。
今回、MyFirstComponentはTutorialSystemを構成する部品であることを定義します。

「ツールバー」-「関連 - コンポジション」を選択し、TutorialSystemからMyFirstComponentに向けて線を引いて下さい。

**ツールバー**
  .. figure:: /images/tutorial/basic/composition.png
       :alt: image

**図上での操作**
  .. figure:: /images/tutorial/basic/composition_dnd.png
       :alt: image

黒菱形が付いているコネクタの端側が全体を表します。今回の場合は、TutorialSystem側に黒菱形が表示されているはずです。
このようにコンポジションによって、全体と部品関係を設計できます。

.. figure:: /images/tutorial/basic/composition_done.png
     :alt: image

--------------

.. _ref-tutorial-basic-ibd:

内部ブロック図で、システムを構成する要素間の関係性を設計する
--------------------------------------------------------------------------------
ブロック定義図でシステムにどのような要素で構成されるかを設計したので、次に内部ブロック図を用いて、システムを構成する要素間の関係性を設計します。

今回はTutorialSystemには、MyFirstComponentしか存在しないので関係性は設計できませんが、今後のチュートリアルで説明していきます。

内部ブロック図は、ブロック定義図上でブロックを選択しコンテキストメニューから「内部ブロック図の追加」を選択して作成します。

.. figure:: /images/tutorial/basic/ibd_contextmenu.png
     :alt: image

TutorialSystemを選択して「内部ブロック図の追加」を選択してください。次のような図が作成され、開かれるはずです。

.. figure:: /images/tutorial/basic/ibd_first.png
     :alt: image

内部ブロック図は作成されるときに、選択されたブロックからコンポジション関係にあるブロックからプロパティを作成します。

SysMLのプロパティはUMLのパートに相当し、あるブロックのインスタンスひとつから見た構成要素や、保持する値を表現するモデルです。astah* SysMLではプロパティをパートと称して提供しています。

| パートのラベルは"プロパティ名":"型名"というフォーマットで表示されています。
| プロパティ名とは、このシステムでのパートの役割を意味する名前です。たとえば、ロボットを表すrobot、右手を表すright-armなどと言った感じです。型名とは、パートという役割の振る舞いなどが定義された型(ブロック)を意味します。

今回は":MyFirstComponent"パートを選択し、プロパティビューから名前にmyFirstCompと設定して、"myFirstComp:MyFirstComponent"パートとして定義して下さい。


.. figure:: /images/tutorial/basic/part.png
     :alt: image
     
|plugin_name| では、この内部ブロック図を元にRTC/RTSプロファイルを生成します。生成されるRTSプロファイルは内部ブロック図の名前を利用します。そのため内部ブロック図の名前は英数字で構成される必要があります。

astah* SysMLの右下に「モデル検証」というビューに、次のようなエラーが表示されているはずです。

.. figure:: /images/tutorial/basic/modelvalidationview.png
     :alt: image


モデル検証ビューは、編集中の設計モデル中の不整合を一覧表示、操作するビューを提供します。エラーが存在する場合、RTC/RTSプロファイルが生成できませんので、エラーを解決してください。

内部ブロック図を選択し、プロパティビューから名前に"tutorial_system"と入力してください。設定すると、モデル検証ビューからエラーが削除されるはずです。

---------------

.. _ref-tutorial-basic-rts-rtc-profile:

RTS/RTCプロファイルの生成
=======================================
内部ブロック図からRTC/RTSプロファイルを生成するため、これまで設計してきた内部ブロック図を開き、メニュー :menuselection:`ツール  --> SysML-RTM --> 開いている図からRTC/RTSプロファイルを生成する` を選択します。


.. figure:: /images/tutorial/basic/plugin_menu.png
     :alt: image

次のようなダイアログが表示されるので、RTC/RTSプロファイルを生成するフォルダを指定し「生成」ボタンを押下して下さい。
 
.. figure:: /images/tutorial/basic/plugin_dialog.png
     :alt: image

出力場所で指定したフォルダ(以下の例では/tmp/tutorial/basic_tutorial)には、次のようなファイルが生成されています。
 
 ::
 
  /tmp/tutorial/basic_tutorial/
   |- MyFirstComponent.xml
   |- tutorial_system.xml
   
.. describe:: MyFirstComponent.xml

   RTコンポーネントMyFirstComponentのRTCプロファイル

.. describe:: tutorial_system.xml

   TutorialSystemのRTSプロファイル
 

なお、RTCBuilderやRTSystemEditorの操作方法やOpenRTM-aistに準拠したRTコンポーネントのビルド方法については、公式サイトを参照下さい。

-------

RTCプロファイルのインポート
--------------------------------------------------
生成されたRTCプロファイルをRTCBuilderにインポートし、MyFirstComponentのソースコードのひな形を生成します。

* RTCBuilderを起動し、MyFirstComponentプロジェクトを作成します。

  .. figure:: /images/tutorial/basic/rtcbuilder.png
       :alt: image
       
* 「基本」タブのプロファイル情報のインポート・エクスポートの「インポート」ボタンから生成したRTCプロファイルを選択します。
* 言語タブでC++やPythonなど任意の言語を選択し「基本」タブの「コード生成とパッケージ」の「コード生成」ボタンを押下し、ソースコードのひな形を生成します。

このような手順でSysMLから生成されたRTCプロファイルを元に、RTコンポーネントのソースコードのひな形を作成できます。

-------

.. _ref-tutorial-basic-import-rtcprofile:

RTSプロファイルのインポート
--------------------------------------------------
生成されたRTSプロファイルを用いて、RTSystemEditorでシステムを復元します。

具体的には、RTSystemEditorを起動し、システムエディタのコンテキストメニュー「Open and Restore」を選択し、RTSプロファイルを選択し復元します。

.. figure:: /images/tutorial/basic/rtsystemeditor.png
     :alt: image

.. hint:: 
  
   RTCBuilderから生成されるひな形は、パート名がコンポーネント名0という名前で生成されます。SysMLのパートで指定したプロパティ名と一致しない場合、RTSystemEditorでの復元でエラーが発生します。
   rtc.confの次のプロパティを修正し、RTコンポーネントのCORBA Naming Serverにバインドする際の名前、及びRTコンポーネントのインスタンス名が、SysMLのプロパティ名と一致するように修正してから、システムを復元して下さい。
   
    * naming.formatプロパティ : CORBA Naming Serverに登録する際の名前
    * manager.components.precreate : RTコンポーネントのインスタンス名
    
   ex)rtc.conf
   
   ::
   
    naming.format : myFirstComp.rtc
    manager.components.precreate: MyFirstComponent?instance_name=myFirstComp

このようにSysMLのブロック定義図と内部ブロック図を用い、RTコンポーネントを含んだロボットシステムのモデルを設計できます。
