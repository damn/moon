(ns create.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [gdx.application :as app]
            [gdx.files :as files]
            [gdx.graphics :as graphics])
  (:import (com.badlogic.gdx.graphics Pixmap)))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                            (update-vals data
                                         (fn [[path [hotspot-x hotspot-y]]]
                                           (let [pixmap (Pixmap. (files/internal (app/files app) (format path-format path)))
                                                 cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
                                             (.dispose pixmap)
                                             cursor))))))
