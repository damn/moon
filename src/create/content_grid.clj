(ns create.content-grid
  (:require [clojure.props-get :refer [props-get]]
            [clojure.get-properties :refer [get-properties]]
            [moon.content-grid :as content-grid]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (content-grid/create (props-get (get-properties tiled-map) "width")
                       (props-get (get-properties tiled-map) "height")
                       16))
