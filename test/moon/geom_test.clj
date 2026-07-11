(ns moon.geom-test)
;(ns game.render.draw-on-world-viewport.geom-test)
;(:require [com.badlogic.gdx.color :as color])
; [clojure.g2d.circle-to-cells :refer [circle->cells]]
;
;(comment
; (require '[moon.grid :as grid])
; (require '[moon.circle :as circle])
; [com.badlogic.gdx.rgba.float-bits :as float-bits]
;
; (defn geom-test
;   [{:keys [ctx/grid
;            ctx/world-mouse-position]}]
;   (let [position world-mouse-position
;         radius 0.8
;         circle {:position position
;                 :radius radius}]
;     (conj (cons [:draw/circle position radius (float-bits/f [1 0 0 0.5])]
;                 (for [[x y] (map #(:position @%) (->> circle
; circle/outer-rectangle
; rectangle/touched-tiles
; (g2d/get-cells g2d)))]
;                   [:draw/rectangle x y 1 1 (float-bits/f [1 0 0 0.5])]))
;           (let [{:keys [x y width height]} (circle/outer-rectangle circle)]
;             [:draw/rectangle x y width height (float-bits/f [0 0 1 1])]))))
;
; )
