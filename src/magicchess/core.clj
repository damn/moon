(ns magicchess.core
  (:require [gdl.application :as app]))

; [x] create window

; [ ] board data structure

(comment
 [[:spider :wolf :goblin-mage :tauren :troll :goblin-mage :wolf :spider]
  [:goblin-warrior :goblin-warrior :goblin-warrior :goblin-warrior :goblin-warrior :goblin-warrior :goblin-warrior :goblin-warrior ]
  [nil nil nil nil nil nil nil nil]
  [nil nil nil nil nil nil nil nil]
  [nil nil nil nil nil nil nil nil]
  [nil nil nil nil nil nil nil nil]
  ]
 )

; [ ] draw the board
;    [ ] clear screen
;    [ ] textures - terrain & simple_heroes
;    [ ] sprite-batch
;    [ ] orthographic-camera
;    [ ] viewport ?
;    [ ] board square -> texture-region function.

; [ ] mouseover-info display square info (creature: type&player or empty)

; [ ] Turn: one player. State: no creature selected or selected (display possible moves)

; [ ] no creature selected -> can selecet , selected: can make move

; [ ] each creature has possible moves, highlight.

; [ ] click possible move: execute move on board (fn [board] -> new board ) & change which players turn

; [ ] check win condition (in that case show to player(s)  and reset board , restart game)

; =>
; [ ] for each creature type: possible moves & creature-type x other creature type : result.

(defn- create! [ctx]
  ctx)

(defn dispose! [ctx]
  nil)

(defn render! [ctx]
  ; display game -
  ; clear screen black
  ; draw (board)
  ; => draw _grid_
  ctx)

(defn resize! [ctx width height]
  nil)

(def state (atom nil))

(defn -main []
  (let [listener (reify app/Listener
                   (create! [_ ctx]
                     (reset! state (create! ctx)))

                   (dispose! [_]
                     (dispose! @state))

                   (render! [_]
                     (swap! state render!))

                   (resize! [_ width height]
                     (resize! @state width height))

                   (pause! [_])

                   (resume! [_]))
        config {:title "Magic Chess"
                :window {:width 1440
                         :height 900}
                :fps 60}]
    (app/start! listener config)))
