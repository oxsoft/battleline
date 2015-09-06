# バトルライン
バトルラインのコンピュータ版を作るプロジェクトです．  
オンライン対戦や強いAIの開発を目的としています．

## 使い方
Javaで書かれています．1.7以上が必要です．  
Eclipseを使う場合は.classpathと.projectを用意して下さい．  
exampleファイルを使うこともできます．
```
cp .classpath.example .classpath
cp .project.example .project
```

## 進捗
### Done
* コアとなる部分を開発
* オンライン対戦化
* 現状のAIたち
 * The Fool：ひたすらランダムにカードを置きます
 * Mr. Phalanx：Phalanx（スリーカード）を一生懸命狙います
 * Ms. Battalion：Battalion（フラッシュ）を一生懸命狙います
 * Dr. Wedge：Wedge（ストレートフラッシュ）を一生懸命狙います
* 強さ関係
 * Mr. Phalanx≫Ms. Battalion＞Dr. Wedge≫The Fool

### Todo
* バグとり
 * 通信が不安定な場合失敗する可能性がある
 * フォーメーションを完成させた順番を考慮していない
* UIの改善
 * Tacticsを使えるかどうか
 * 配置転換によるカード廃棄
* 強いAIの開発
 * みんなで考えよう！

## AI開発方法
AIはcom.oxsoft.battleline.ai以下で開発します．  
AIはクラスを作り，AriticialIntelligenceを継承して実装して下さい．  
com.oxsoft.battleline.ai以下にAriticialIntelligenceを継承したクラスを実装すると，自動的にプログラムがAIとして認識します．  
AriticialIntelligenceの各関数は，現在のゲームの状態をGameStateContainer型として受け取り，GameStateInput型のインスタンスinputの関数を呼び出すことにより，ゲームをプレイします．  
必ず何らかのinputの関数を実行し，成功(trueが返ってくる)しなければなりません．成功していない場合は，そのゲームは失格となります．

* action
 * カードを置く（input.putCard），パスをする（input.passTurn）などを行います．
 * パスはカードを置く場所がない，または手札が全て戦術カードの時のみ行うことができます．
* draw
 * 部隊カードを引く(input.drawTroop)，戦術カードを引く(input.drawTactics)，カードを引かずにターンを終了する（input.endTurnWithoutDraw）などを行います．
 * どちらの山札も無い時のみ，カードを引かずにターンを終了することができます．
* returnCard
 * 偵察を使い，カードを引いた時に，カードを山札に戻します（input.returnCards）．
* resolveFlags（オプショナル）
 * フラッグを取るこの関数が，actionの前後で呼ばれます．
 * 何も実装しないと，全てのフラッグについて，取ることができれば取ります．
* win（オプショナル）
 * ゲームに勝利したときに呼ばれます．（機械学習用）
* lose（オプショナル）
 * ゲームに敗北したときに呼ばれます．（機械学習用）

TheFool.javaがサンプルとなっていますのでぜひ一度ご覧ください．

