(ns moon.geom-test)
;(ns moon.application.render.draw-on-world-viewport.geom-test)
;(:require [moon.color :as color])
;
;(comment
; (require '[moon.grid :as grid])
; (require '[moon.circle :as circle])
;
; (defn geom-test
;   [{:keys [ctx/grid
;            ctx/world-mouse-position]}]
;   (let [position world-mouse-position
;         radius 0.8
;         circle {:position position
;                 :radius radius}]
;     (conj (cons [:draw/circle position radius (color/float-bits [1 0 0 0.5])]
;                 (for [[x y] (map #(:position @%) (grid/circle->cells grid circle))]
;                   [:draw/rectangle x y 1 1 (color/float-bits [1 0 0 0.5])]))
;           (let [{:keys [x y width height]} (circle/outer-rectangle circle)]
;             [:draw/rectangle x y width height (color/float-bits [0 0 1 1])]))))
;
; )
