(ns moon.draw-on-world-viewport.draw-cell-debug
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [moon.graphics.camera :as camera])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(defn draws
  [{:keys [ctx/grid
           ctx/factions-iterations
           ctx/world-viewport]}]
  (apply concat
         (for [[x y] (camera/visible-tiles (Viewport/.getCamera world-viewport))
               :let [cell (grid [x y])]
               :when cell
               :let [cell* @cell]]
           [(when (and show-cell-entities? (seq (:entities cell*)))
              [:draw/filled-rectangle x y 1 1 (color/float-bits [1 0 0 0.6])])
            (when (and show-cell-occupied? (seq (:occupied cell*)))
              [:draw/filled-rectangle x y 1 1 (color/float-bits [0 0 1 0.6])])
            (when-let [faction show-potential-field-colors?]
              (let [{:keys [distance]} (faction cell*)]
                (when distance
                  (let [ratio (/ distance (factions-iterations faction))]
                    [:draw/filled-rectangle x y 1 1 (color/float-bits [ratio (- 1 ratio) ratio 0.6])]))))])))
