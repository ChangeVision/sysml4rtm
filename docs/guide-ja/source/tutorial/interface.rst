.. _ref-tutorial-interface:

========================================================================================
インタフェースを介して、他のRTコンポーネントと連携するモデルを設計する
========================================================================================
:ref:`ref-tutorial-dataport` では、SysMLのポートを用いた設計方法について説明しました。

| SysMLではインタフェースでも、RTコンポーネント間のやり取りを設計できます。
| 今回SysMLで設計するインタフェースは、RTコンポーネントにおけるサービスポートとインタフェースに該当します。

このチュートリアルでは、次の項目について説明します。

* :ref:`ref-tutorial-interface-port-and-if`
* :ref:`ref-tutorial-interface-req-prov`
* :ref:`ref-tutorial-interface-rts-rtc`

.. describe:: このチュートリアルで作成するシステムについて

  MyFirstComponentブロックが提供するインタフェースを利用して、MySecondComponentからMyFirstComponentを移動させる指示を行うシステムを設計します
  
**ブロック図**

  .. figure:: /images/tutorial/interface/bdd.png
     :alt: image
     
**内部ブロック図**

  .. figure:: /images/tutorial/interface/ibd.png
     :alt: image
       
  * :download:`モデルのダウンロード </sources/tutorial/interface.asml>`

.. _ref-tutorial-interface-port-and-if:

ポートとインタフェースを設計する
========================================================
ブロックがどのようなサービスを提供、要求しているかといったことをポートとインタフェースを使って表すことができます。

先の :ref:`ref-tutorial-dataport` で作成したモデルを元に、MyFirstComponentブロックに移動を指示できるMoveServiceインタフェースを追加しましょう。

その前に、前回作成したポートを一旦すべて削除します。MyFirstComponent、MySecondComponentに設計されたポートを選択し「モデルから削除」メニューを選択して削除し、ポートを持たないmyFirstCompとmySecondCompパートを作成して下さい。

インタフェースを設計するには、まずポートを追加する必要があります。:ref:`ref-tutorial-dataport` で行ったように、新しいポートをmyFirstCompパートに作成し、名前をserviceと設計して下さい。

次にMyServiceインタフェースに操作を設計し、MyFirstComponentブロックに作成した"service"ポートに接続します。

--------

インタフェースの作成
--------------------
インタフェースはツールバー「インタフェース」から作成できます。

.. figure:: /images/tutorial/interface/if_toolbar.png
   :alt: image
   
ツールバー「インタフェース」を選択し、内部ブロック図の好きな場所を選択します。すると、図上に"インタフェース0"というラベルのインタフェースが作成されます。

作成されたインタフェースのプロパティは、今までと同様にプロパティビューから編集できるので、"インタフェース0"を選択しプロパティビューを表示させます。
表示されたプロパティから、名前をMoveServiceに変更します。

操作の設計
-----------------------------------
次にMoveServiceインタフェースの操作に、driveという名前の操作を追加します。

#. 操作タブを開きます
#. 操作タブの下部にある「追加」ボタンを押下します
#. 一覧にOperation0という名前の操作が作成されます
#. Operation0という名前をダブルクリックして、driveと名前を変更します

ここまでの操作で、次のようなdrive操作が追加されているはずです。

.. figure:: /images/tutorial/interface/drive_1.png
   :alt: image

パラメータをもつ操作の設計
---------------------------------------
次に、drive操作にパラメータを設計しましょう。

#. 作成されたdrive操作を選択し「編集」ボタンを押下します
#. 表示された「操作」ダイアログの「パラメータ」タブを表示します
#. パラメータタブの下部にある「追加」ボタンを押下します
#. 一覧にparam0という名前のパラメータが作成されます
#. param0という名前をダブルクリックして、左車輪の速度を指定するleftWheelと名前を変更します
#. leftWheelパラメータの型から、IDL::shortを選択します
#. 同じようにパラメータをもう一つ追加し、今度は右車輪の速度を指定する型がIDL::shortのrightWheelパラメータを作成します

ここまでの操作で、パラメータは次のようになっているはずです。

.. figure:: /images/tutorial/interface/drive_parameter.png
   :alt: image

.. hint::
   パラメータ型の候補には、IDLパッケージのshortの他にshort、int、longなどの型が表示されます。
   RTMと連携したSysMLモデルを設計する場合、IDLパッケージに用意されたIDL型を利用して下さい。
   
これでleftWheelとrightWheelパラメータをもつdrive操作を設計できました。

--------

.. _ref-tutorial-interface-req-prov:

提供、要求インタフェースを設計する
========================================================

MyFirstComponentコンポーネントにMoveServiceインタフェースを、提供インタフェースとして定義する
---------------------------------------------------------------------------------------------------
このように作成されたMoveServiceインタフェースを、MyFirstComponentブロックが提供することを設計します。

ブロックがインタフェースを提供、つまりインタフェースの操作を他のブロックから利用できるように提供していることを示すには、ポートに提供インタフェースとして接続する必要があります。

インタフェースをポートに提供インタフェースとして接続するには、ツールバー「実現」を利用します。

.. figure:: /images/tutorial/interface/prov_if_toolbar.png
     :alt: image

ツールバー「実現」を選択し、myFirstComponentパートの"service"ポートをクリックします。

続いて、そのままMoveServiceにマウスをホバーさせると、次のように三角のアイコンがついた線がMoveServiceまで伸びるので、そのままクリックしてください。

.. figure:: /images/tutorial/interface/add_provide_if.png
     :alt: image

これで、"service"ポートにMoveServiceを提供インタフェースとして定義できました。

--------

MySecondComponentコンポーネントにMoveServiceインタフェースを、要求インタフェースとして定義する
---------------------------------------------------------------------------------------------------
これまでの操作で、MyFirstComponentブロックがMoveServiceインタフェースを提供していることを設計できました。次はMySecondComponentブロックが、このMoveServiceインタフェースを利用してメッセージのやり取りを行っていることを設計しましょう。

| 先程と同様に、MySecondComponentにポートを作成し、名前をserviceと設定して下さい。
| OpenRTM-aistでは、インタフェースを提供するポートとやり取りする場合、そのポートの名前を一致させる必要があります。

.. warning::
   ポートの名前を一致させないと、インタフェースを提供、要求するポートを用いたメッセージのやりとりが正しく動作しません

インタフェースをポートに要求インタフェースとして接続するには、ツールバー「使用依存」を利用します。

.. figure:: /images/tutorial/interface/req_if_toolbar.png
     :alt: image
     
ツールバー「使用依存」を選択し、mySecondComponentパートの"service"ポートをクリックします。

続いて、そのままMoveServiceにマウスをホバーさせると、次のように三角のアイコンがついた線がMoveServiceまで伸びるので、そのままクリックしてください。

.. figure:: /images/tutorial/interface/add_req_if.png
     :alt: image
     
これまでの操作で、次のようなモデルが作成されているはずです。

.. figure:: /images/tutorial/interface/assembly_connector.png
     :alt: image

このような操作で、RTコンポーネント間のインタフェースを介したメッセージのやり取りを、SysMLを用いて設計することができます。

--------

.. _ref-tutorial-interface-rts-rtc:

RTS/RTCプロファイルの生成
=======================================
:ref:`ref-tutorial-basic` で行ったように、内部ブロック図からRTC/RTSプロファイルを生成するため、これまで設計してきた内部ブロック図を開き、メニュー :menuselection:`ツール  --> SysML-RTM --> 開いている図からRTC/RTSプロファイルを生成する` を選択します。
表示されるダイアログで、RTC/RTSプロファイルを生成するフォルダにを指定し「生成」ボタンを押下して下さい。
 
出力場所で指定したフォルダ(以下の例では/tmp/tutorial/interface_tutorial)には、次のようなファイルが生成されています。
 
 ::
 
  /tmp/tutorial/interface_tutorial/
   |- MyFirstComponent.xml
   |- MySecondComponent.xml
   |- MyService.idl
   |- tutorial_system.xml
   
.. describe:: MyFirstComponent.xml

   RTコンポーネントMyFirstComponentのRTCプロファイル

.. describe:: MySecondComponent.xml

   RTコンポーネントMySecondComponentのRTCプロファイル

.. describe:: MyService.idl

   SysMLで設計した、MoveServiceインタフェースの構造を表すIDLファイル
   
.. describe:: tutorial_system.xml

   TutorialSystemのRTSプロファイル
 
RTS/RTCプロファイルの他に、インタフェースを利用した場合、その構造がIDLファイルとしても生成されます。

-------

RTCプロファイルのインポート
--------------------------------------------------     
生成されたRTCプロファイルをRTCBuilderにインポートし、MyFirstComponentのソースコードのひな形を生成します。

基本的な手順は :ref:`ref-tutorial-basic` と同様ですが、今回はインタフェースから生成されたIDLファイルが参照する、OpenRTM-aistが提供するIDLファイルへのパスの設定が追加で必要になります。

* RTCBuilderを起動し、MyFirstComponentプロジェクトを作成します。       
* 「基本」タブのプロファイル情報のインポート・エクスポートの「インポート」ボタンから生成したRTCプロファイルを選択します。
* インポートされると、次のようにMoveServiceインタフェースを提供するコンポーネントがRTCBuilderに取り込まれるはずです。

  .. figure:: /images/tutorial/interface/rtc_builder.png
       :alt: image
       :width: 700
       
* 「サービスポート」タブを開き、ツリーから「service」-「myService」を選択します。

  .. figure:: /images/tutorial/interface/rtc_builder-service.png
       :alt: image
       :width: 700
       
* 表示されたプロパティのインタフェース名の「IDLパス」に、OpenRTM-aistのidlファイルが配置されているディレクトリ(ex. /usr/include/openrtm-1.1/rtm/idl)を選択します。
* 言語タブでC++やPythonなど任意の言語を選択し「基本」タブの「コード生成とパッケージ」の「コード生成」ボタンを押下し、ソースコードのひな形を生成します。

このような手順でSysMLから生成されたRTCプロファイルを元に、インタフェースを利用したRTコンポーネントのソースコードのひな形を作成できます。

-------

RTSプロファイルのインポート
--------------------------------------------------
生成されたRTSプロファイルを用いて、RTSystemEditorでシステムを復元します。

基本的な手順は :ref:`ref-tutorial-basic` と同様に、システムエディタのコンテキストメニュー「Open and Restore」を選択し、RTSプロファイルを選択し復元します。
ただし、:ref:`ref-tutorial-basic-import-rtcprofile` で説明したように、復元する前にSysMLのパートで指定したプロパティ名とRTコンポーネントのインスタンス名、CORBA Naming Serviceにバインドする際の名前を一致させて下さい。

  ex)MyFirstComponent/rtc.conf
   
   ::
   
    naming.format : myFirstComp.rtc
    manager.components.precreate: MyFirstComponent?instance_name=myFirstComp
    
  ex)MySecondComponent/rtc.conf
  
   ::
   
    naming.format : mySecondComp.rtc
    manager.components.precreate: MySecondComponent?instance_name=mySecondComp
    
このようにSysMLのポートと提供、要求インタフェースを用いて、RTコンポーネントにおけるサービスポートを介した、ロボットシステムのモデルを設計できます。