(ns create.explored-tile-corners
  (:require [clojure.maps.properties.get :refer [props-get]]
            [clojure.maps.get-properties :refer [get-properties]]
            [clojure.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (props-get (get-properties tiled-map) "width")
                         (props-get (get-properties tiled-map) "height")
                         (constantly false))))
