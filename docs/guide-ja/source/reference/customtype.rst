.. _ref-customtype:

独自のデータ型を設計し利用する
===============================================================================
|plugin_name| では、rtc_template.asmlファイルのRTCパッケージに、OpenRTM-aistが定義する基本的なデータ型定義しています。

 .. figure:: /images/reference/customtype/defined_rtcdatatype.png
    
このデータ型の他に、ユーザーが独自に定義した型をSysMLのValueTypeとして定義し、ポートの型や、インターフェースの属性や操作の戻り値、パラメータの型などに利用できます。

この独自データ型がどのようなメンバーを持つかといった、独自データ型の構造は、クラスの属性や関連を利用して表すことができます。

 .. figure:: /images/reference/customtype/sample.png

属性として定義する場合は、関連で設定する必要がある、名前や誘導可能性、多重度を設定せずともよく、手間が少ないですが、関係するデータ型が多い場合、データ型間の関係が一目で分かりにくい点がデメリットです。

複雑なデータ型の場合は関連を利用し、 単純なデータ型の場合は属性として定義するなどといった、他の利用者にとって分かりやすいモデルはどちらか？という視点で選ぶとよいでしょう。

また、どちらかで統一する必要はなく、構造が分かりにくいと思う箇所だけ関連を利用し、他の部分はシンプルに属性を利用するなど、モデルもシンプルな表現を目指すとよいでしょう。

このリファレンスで説明する機能は、次の通りです。

* :ref:`ref-customtype-sequence`
* :ref:`ref-customtype-array`
* :ref:`ref-customtype-attr`
* :ref:`ref-customtype-relation`
* :ref:`ref-customtype-hint`
* :ref:`ref-customtype-rtm`

------------------

.. _ref-customtype-sequence:

sequence型を利用する
----------------------------------
可変長のデータを扱えるsequenceを利用するには、次のような手順でsequence型のデータをモデルとして表現して設計します。

* sequenceが扱うデータ(以下の例ではItem)をクラスとして定義します。
* sequence型(以下の例ではItemSeq)自体もクラスとして定義し、sequence型を意味する<<CORBASequence>>ステレオタイプを付与します。
* sequenceが扱うデータのクラスと、sequence型のクラス間に多重度が0..*である関連を作成します。

 .. figure:: /images/reference/customtype/sequence_model_style.png
    :alt: image

このようなモデルから作成されるIDLは次のようになります。

::

  - Item.idl
  #ifndef ITEM_IDL
  #define ITEM_IDL
  #include "BasicDataType.idl"

  struct Item{
  };
  #endif

  - ItemSeq.idl
  #ifndef ITEMSEQ_IDL
  #define ITEMSEQ_IDL
  #include "BasicDataType.idl"
  #include "Item.idl"

  typedef sequence<::Item> ItemSeq;
  #endif

なお、ItemSeqにCORBASequenceを付与しない場合、次のようにItemSeq自体をstructとして定義されます。
sequence型のValueType(この例ではItemSeq)が、単なるコンテナ型である場合はCORBASequenceステレオタイプを付与すると、余計なstructが定義されません。

::

  - ItemSeq.idl
  #ifndef ITEMSEQ_IDL
  #define ITEMSEQ_IDL
  #include "BasicDataType.idl"
  #include "Item.idl"

  #ifndef ITEMSEQ
  #define ITEMSEQ
  typedef sequence<::Item> ItemSeq;
  #endif
  struct ItemSeq{
    ItemSeq item;
  };
  #endif

------

.. _ref-customtype-array:

配列を利用する
--------------------------------------
ValueTypeの属性のベースタブで多重度を5や3,4などと指定するか、ブロック定義図上でValueTypeの属性を編集して添字を付けると、次のように配列として生成されます。

 .. figure:: /images/reference/customtype/customtype_array.png

::

  - Sample.idl
  #ifndef SAMPLE_IDL
  #define SAMPLE_IDL
  #include "BasicDataType.idl"
  struct Sample{
    RTC::TimedDouble attr1[5];
    RTC::TimedDouble attr2[3][4];
  };
  #endif

------------------

.. _ref-customtype-attr:

属性を利用する
------------------------------------

 .. figure:: /images/reference/customtype/customtype_attr.png

ValueTypeの属性の名前、型がそのままIDLに生成されます。

たとえば、それぞれのクラスがcom::changevisionパッケージに属する場合、次のようなIDLが生成されます。

::

  - Order.idl
  #ifndef ORDER_IDL
  #define ORDER_IDL
  #include "BasicDataType.idl"
  #include "com/changevision/Customer.idl"
  
  module com{
      module changevision{
          struct Order{
              RTC::Time tm;
              com::changevision::Customer customer;
          };
      };
  };
  #endif
 
  - Customer.idl
  #ifndef CUSTOMER_IDL
  #define CUSTOMER_IDL
  #include "BasicDataType.idl"
  
  module com{
      module changevision{
          struct Customer{
              long id;
              string name;
          };
      };
  };
  #endif

-----

.. _ref-customtype-relation:

関連を利用する
----------------------------------
独自のデータ型の構造を表現するのに関連を利用する場合、生成されるIDLには関連の「誘導可能性」「多重度」「関連端名」が関係してきます。

| 誘導可能性は、関連で結ばれている先のValueTypeをメンバーとして生成するかどうかです。
| 多重度は、関連で結ばれている先のValueTypeを、単一か集合体として生成するかどうかに関係します。
| また関連端名は、関連で結ばれている先のValueTypeをメンバーとして生成する際のメンバー名として、利用されます。

誘導可能性
^^^^^^^^^^^^^^^^^^^
関連には、ValueTypeに対する誘導可能性を設定できます。誘導可能性とは、片方のValueTypeから、もう片方のValueTypeを参照できるかどうかを定義するものです。 
誘導可能性がどのタイプで定義されているかによって、関連を定義したValueTypeが、生成されるIDLにてValueTypeのメンバーに定義されるかどうか決定されます。

誘導可能性には、「誘導可能」「誘導不可能」「誘導可能性未定」が存在します。

 .. figure:: /images/reference/customtype/WS001266.jpg
    :alt: 

|plugin_name| では、誘導可能性が未定義である場合は、関連先への参照を持たないものとして、独自の型を生成します。

誘導可能
"""""""""""
誘導可能は、ValueTypeを参照できる方向を矢印で表します。
次の例の場合、Class0からClass1は誘導可能(参照できる)。Class1からClass0は参照できるかは未定という意味になります。

 .. figure:: /images/reference/customtype/472.png
    :alt: 
    
誘導不可能
"""""""""""""""""""
次の例の場合、Class0からClass1は誘導不可能(参照できない)。Class1からClass0は参照できるかは未定という意味になります。

 .. figure:: /images/reference/customtype/471.png
    :alt: 

誘導可能性未定
"""""""""""""""""""
次の例の場合、両方のクラスからそれぞれ参照できるかは未定という意味になります。

 .. figure:: /images/reference/customtype/473.png
    :alt: 

例

 .. figure:: /images/reference/customtype/110.png
    :alt: 
    
* AとBはそれぞれ参照できる

::

  struct A{
    ::B b;
  };
  struct B{
    ::A a;
  };

* CとDはそれぞれ参照できない

::

  struct C{};
  struct D{};
 
* EとFはそれぞれ参照できるかは未定

::

  struct E{};
  struct F{};

* GからHは参照できるが、HからGは参照できない

::

  struct G{
    ::H h;
  };
  struct H{};
 
* IからJは参照できるが、JからIは参照できるかは未定

::

  struct I{
    ::J j;
  };
  struct J{};

多重度
^^^^^^^^^^^^^^^^^^^
| 多重度が0,1,0..1の場合は単数、それ以外は集合とみなします。
| 多重度が定義されていない場合は、１が定義されているとみなします。

|plugin_name| の場合、集合はsequence型でのみ対応しているため、0..*など「0 , 1 , 0..1」以外の多重度が指定された場合、sequence型として定義します。
ただし、1..3などとサイズを定義しても、サイズは反映されず、サイズ未定義のsequenceとして生成します。

なお関連から生成されるメンバー名は、未定義の場合はValueType名を小文字にした名前で定義します。関連端名が定義されている場合は、その名前が利用されます。  

例1. 多重度0, 1, 0..1の場合
""""""""""""""""""""""""""""""""""""""
多重度が0, 1, 0..1の場合は、次のコードが生成されます

 .. figure:: /images/reference/customtype/WS001336.jpg
    :alt: 
    
::

  struct A{
    ::B b;
  };
  struct B{};

.. _ref-customtype-multiple:

例2. 多重度が0, 1, 0..1以外の場合
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
多重度を0, 1, 0..1以外にする場合は、次のようにステレオタイプ<<CORBASequence>>を持つクラスを作成して下さい。

 .. figure:: /images/reference/customtype/use_corbasequence.jpg
    :width: 300
    

1..3などとサイズを指定した場合でも、sequence<::B,3>とは定義されず、sequence<::B>として定義されます。

関連端名が定義されていないので、ValueType名を小文字にした名前で定義されます。

上記のブロック定義図でクラスBをdoubleなどのプリミティブ型にしたい場合は、IDLパッケージの型を利用してください。

::

  typedef sequence<::B> BSeq;
  struct A{
    ::BSeq bSeq;
  };
  struct B{};

例3. 関連端名が定義されている
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
関連端名が定義されているので、メンバーの変数名としてmemberが利用されます。

 .. figure:: /images/reference/customtype/member.jpg
    :alt: 

::

  struct A{
    ::B member;
  };
  struct B{};

例4. 多重度が定義されていない
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
多重度が定義されていない場合は1で定義されているとみなします。

 .. figure:: /images/reference/customtype/WS001340.jpg
    :alt: 

::

  struct A{
    ::B b;
  };
  struct B{};

例5. ステレオタイプ<<CORBASequence>>が定義されている
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
クラスにステレオタイプ<<CORBASequence>>が定義されている場合はtypedefとして生成されます。

 .. figure:: /images/reference/customtype/corbasequence.jpg
    :width: 300

::

  typedef sequence<::A> ASeq;
  struct A{};

上記のクラス図でクラスAをdoubleなどのプリミティブ型にしたい場合は、rtc_template.asmlファイルで提供するIDLパッケージの型を利用してください。

関連端名
^^^^^^^^^^^^^^^^^^^
関連端名は、メンバー変数の名前として利用されます。未定義の場合、クラス名を小文字にした名前で定義します。 

 .. figure:: /images/reference/customtype/member_line.jpg
    :alt: 

::

  struct A{
    ::B member;
  };
  struct B{};
 
関連端名が未定義の場合

::

  struct A{
    ::B b;
  };
  struct B{};


なお、複数の関連が存在し、それぞれ関連端名が未定義の場合、同じ名前のメンバーが重複します。omniIDLなど実行時にエラーとなります。

 .. figure:: /images/reference/customtype/117.png
    :alt: 
    

::

  struct Organization{
    ::Employee employee;
    ::Employee employee;
  };
  struct Employee{
    ::Organization organization;
    ::Organization organization;
  };


集約
^^^^^^^^^^^^^^^^^^^
集約は生成されるコードに影響を与えませんが、集合であることが明示されるので、指定する方が望ましいです。

 .. figure:: /images/reference/customtype/aggregation.jpg
    :alt: image

----------------

.. _ref-customtype-hint:

独自データ型の設計における注意点
------------------------------------------
* データポートに指定する、独自データ型はRTC::Time型のtmという名前のメンバーを定義する必要があります。

 .. figure:: /images/reference/customtype/WS001344.jpg
    :alt: 

* クラスが相互参照している場合、IDLのコンパイルでエラーが発生します。相互参照しないようモデルを修正して下さい。

次の例は、AとBが相互参照しているため、omniIDLなどのidlツールでエラーが発生します。

 .. figure:: /images/reference/customtype/WS001345.jpg
    :alt: 

::

  struct A{
    ::B b;
  };
  struct B{
    ::A a;
  };

------------------

.. _ref-customtype-rtm:

RTCBuilderによる独自データ型の利用
------------------------------------------
このように作成された独自データ型を含んだRTCプロファイルを元に、RTCBuilderでソースコードのひな形を作成する方法について説明します。

次のモデルで示されるような、Angleという独自データ型を扱うポートをもつCustomCompブロックを、RTコンポーネントとして出力します。

**ブロック定義図**

.. figure:: /images/reference/customtype/custom_bdd.png
    :alt: image

**内部ブロック図**

.. figure:: /images/reference/customtype/custom_ibd.png
    :alt: image

**モデル**
 :download:`モデルのダウンロード </sources/customtype.asml>`
 
:ref:`ref-tutorial-basic-import-rtcprofile` などを参考に、メニュー :menuselection:`ツール  --> SysML-RTM --> 開いている図からRTC/RTSプロファイルを生成する` を選択します。
表示されるダイアログで、RTC/RTSプロファイルを生成するフォルダにを指定し「生成」ボタンを押下して下さい。

出力場所で指定したフォルダ(以下の例では/tmp/tutorial/custom)には、次のようなファイルが生成されます。
 
 ::
 
  /tmp/tutorial/custom/
   |- Angle.idl
   |- CustomComp.xml
   |- sample.xml

.. describe:: Angle.idl

   CustomCompコンポーネントが利用する、独自データ型Angleの構造を示すIDLファイル
   
.. describe:: CustomComp.xml

   RTコンポーネントCustomCompのRTCプロファイル

.. describe:: sample.xml

   RTSプロファイル

上記のように生成されたRTCプロファイルを:ref:`ref-tutorial-basic-import-rtcprofile` にあるような、以下の手順でRTCBuilderにインポートし、CustomCompコンポーネントのソースコードのひな形を生成します。

* RTCBuilderを起動し、独自データ型をRTCBuilderが参照できるよう、メニュー :menuselection:`ウィンドウ  --> 設定 --> RTCBuilder` を選択し、生成された独自データ型のIDLが配置されているフォルダを指定します。(上記例の場合、/tmp/tutorial/custom/)

  .. figure:: /images/reference/customtype/rtcbuilder-preference.png
      :alt: image
      
* RTCBuilderを再起動し、CustomCompプロジェクトを作成します。
* 「基本」タブのプロファイル情報のインポート・エクスポートの「インポート」ボタンから生成したRTCプロファイルを選択します。
* 「データポート」タブを開き、独自データ型を利用しているポートのデータ型を確認すると、独自データ型がコンボボックスで選択されているはずです。
* 言語タブでC++やPythonなど任意の言語を選択し「基本」タブの「コード生成とパッケージ」の「コード生成」ボタンを押下し、ソースコードのひな形を生成します。

このように、独自データ型をデータポートの型として利用している場合、RTCBuilderでソースコードのひな形を作成するには、その型をRTCBuilderから参照するための設定が必要となります。   