(ns moon.tile-color-setter
  (:require [clj.api.com.badlogic.gdx.math.vector3 :as vector3]
            [moon.raycaster :as raycaster])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn- tile-color-setter
  [{:keys [ray-blocked?
           explored-tile-corners
           light-position
           see-all-tiles?
           explored-tile-color
           visible-tile-color
           invisible-tile-color]}]
  #_(reset! do-once false)
  (let [light-cache (atom {})]
    (fn tile-color-setter [_color x y]
      (let [position [(int x) (int y)]
            explored? (get @explored-tile-corners position) ; TODO needs int call ?
            base-color (if explored?
                         explored-tile-color
                         invisible-tile-color)
            cache-entry (get @light-cache position :not-found)
            blocked? (if (= cache-entry :not-found)
                       (let [blocked? (ray-blocked? light-position position)]
                         (swap! light-cache assoc position blocked?)
                         blocked?)
                       cache-entry)]
        #_(when @do-once
            (swap! ray-positions conj position))
        (if blocked?
          (if see-all-tiles?
            visible-tile-color
            base-color)
          (do (when-not explored?
                (swap! explored-tile-corners assoc (mapv int position) true))
              visible-tile-color))))))

(comment
 (def ^:private count-rays? false)

 (def ray-positions (atom []))
 (def do-once (atom true))

 (count @ray-positions)
 2256
 (count (distinct @ray-positions))
 608
 (* 608 4)
 2432
 )

(defn create
  [{:keys [ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/world-viewport]}]
  (tile-color-setter
   {:ray-blocked? (partial raycaster/blocked? raycaster)
    :explored-tile-corners explored-tile-corners
    :light-position (vector3/->clj (.position (Viewport/.getCamera world-viewport)))
    :see-all-tiles? false
    :explored-tile-color  (:colors/explored-tile colors)
    :visible-tile-color   (:colors/visible-tile colors)
    :invisible-tile-color (:colors/invisible-tile colors)}))
