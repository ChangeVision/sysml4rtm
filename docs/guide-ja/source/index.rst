.. SysML-RTM documentation master file, created by
   sphinx-quickstart on Fri Apr 25 10:42:02 2014.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Welcome to SysML-RTM's documentation
====================================
|plugin_name| は、SysMLモデルによるシステム設計を :term:`OMG Robotic Technology Component` に準拠したソフトウェア設計として生成、変換するプラグインです。

SysML内部ブロック図上に存在するパートから :term:`RTCプロファイル` 、:term:`RTSプロファイル` を生成し `OpenRTM-aist <http://openrtm.org/openrtm/>`_ が提供するOpenRTPと連携することで、Robot Technologyコンポーネント(RTコンポーネント)のソースコードのひな形の作成や、ロボットシステムに含まれるコンポーネントとその接続関係の復元を実現します。

このプラグインは、独立行政法人産業技術総合研究所と共同研究した成果を活用しています。

.. figure:: /images/sample.png
   :alt: image

   
インストール方法
---------------------
#. sysml4rtm-x.x.x.zip ファイルをダウンロードし、任意のフォルダへ展開します。
#. astah*を起動し、メニュー[ヘルプ] – [プラグイン一覧] から、 [インストール] ボタンをクリックします。
#. 展開したフォルダの"plugins"フォルダにある sysml4rtm-x.x.x.jar ファイルとmodel-validation-x.x.x.jarを選択し、メッセージに従ってastah*を再起動して下さい。
#. インストールを完了しastah*を再起動すると、メニュー[ツール]に [SysML-RTM]が追加されていれば、インストール完了です。

リファレンス
---------------------
..
 * RTコンポーネントモデリング
 * モデル検証
 * 独自の定義型

.. toctree::
   :maxdepth: 1
   :glob:

   reference/model_validation_view   
   reference/customtype

チュートリアル
---------------------
..
 * RT(Robot Technology)コンポーネントを設計する
 * ポートを介して、他のRTコンポーネントと連携するモデルを設計する
 * インタフェースを介して、他のRTコンポーネント連携するモデルを設計する

.. toctree::
   :maxdepth: 1
   :glob:

   tutorial/basic
   tutorial/dataport   
   tutorial/interface
   
License(SysML-RTM プラグイン)
-------------------------------------------------
Copyright 2014 Change Vision, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

License(OpenRTM-aist)
---------------------------------------
OpenRTM-aistは、各言語版 (C++, Java, Python) のミドルウエアライブラリと、RTCBuilder, RTSystemEditorなどツールから構成されており、それぞれOpenRTM-aist (C++, Java, Python版)は LGPLと個別契約のデュアルライセンス
RTSystemEditor, RTCBuilderはEPLと個別契約のデュアルライセンスのもとでオープンソース形式で配布しています。

* LGPL (GNU Lesser General Public License) はフリーソフトウェア財団（Free Software Foundation、以下FSFと略称）が公開しているコピーレフト型のフリーソフトウェアライセンスです。
* EPL (Eclipse Public License) は Free Software Foundation (FSF)によって 認められている「フリーソフトウェアライセンス」の1つであり、CPL (一部は LGPL) 等と似たライセンス形態であり、より商業利用を促進するものとなって います。

これらのライセンスは、(i) 別モジュールとして頒布されるソフトウエアや、(ii) プログラムの派生物でないもの、には及びません。またEPLライセンスは、特許 に関する条項が含まれており、コントリビュータが持つ特許が当該ソフトウエ アに影響しない (使用者には使用料無料の特許ライセンスが付与される) 形態 となっています。
詳細は `OpenRTM-aistライセンス <http://openrtm.org/openrtm/ja/content/openrtm-aist%E3%81%A8%E3%81%AF%EF%BC%9F-0>`_ を参照ください。