(ns moon.geom-test)
;(ns game.render.draw-on-world-viewport.geom-test)
;(:require [gdl.color :as color])
; [moon.grid.circle-to-cells :refer [circle->cells]]
;
;(comment
; (require '[moon.grid :as grid])
; (require '[math.circle :as circle])
; [color.float-bits :refer [float-bits]]
;
; (defn geom-test
;   [{:keys [ctx/grid
;            ctx/world-mouse-position]}]
;   (let [position world-mouse-position
;         radius 0.8
;         circle {:position position
;                 :radius radius}]
;     (conj (cons [:draw/circle position radius (float-bits [1 0 0 0.5])]
;                 (for [[x y] (map #(:position @%) (circle->cells grid circle))]
;                   [:draw/rectangle x y 1 1 (float-bits [1 0 0 0.5])]))
;           (let [{:keys [x y width height]} (circle/outer-rectangle circle)]
;             [:draw/rectangle x y width height (float-bits [0 0 1 1])]))))
;
; )
